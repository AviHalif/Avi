package Gui;

import Classes.Customer;
import Client.Client;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RegisterCustomer extends Register {


    private Client client;
    private Customer customer;
    private JLabel titleLabel;
    private InputStream inStream;
    private Properties properties;
    private JSONObject jsonObject;
    private ImageIcon titlePhotoJPG;
    private ObjectMapper objectMapper;
    private boolean showCustomersTable;
    private String empBranchName, propFileName;
    public static final String TITLE = "/src/images/customer_register.png";
    private static Logger logger = Logger.getLogger(RegisterCustomer.class.getName());

    public RegisterCustomer(JFrame oneBackFrame, JFrame twoBackFrame, boolean showCustomersTable, String empBranchName) throws IOException {

        super(oneBackFrame,twoBackFrame);

        this.showCustomersTable = showCustomersTable;

        this.empBranchName = empBranchName;

        DefineLogAndConfig();
    }

    @Override
    protected void DrawRegister() {

        titlePhotoJPG = new ImageIcon(getClass().getResource(TITLE));
        titleLabel = new JLabel(titlePhotoJPG);
        getjPanelMain().add(titleLabel);

        super.DrawRegister();

        getSpringLayoutPanels().putConstraint(SpringLayout.WEST,titleLabel,60,SpringLayout.WEST,getjPanelMain());
        getSpringLayoutPanels().putConstraint(SpringLayout.NORTH,titleLabel,5,SpringLayout.NORTH,getjPanelMain());
    }

    private void DefineLogAndConfig() throws IOException {

        properties = new Properties();
        propFileName = "config.properties";
        inStream = RegisterEmployee.class.getClassLoader().getResourceAsStream(propFileName);
        properties.load(inStream);

        String log4jConfigFile = System.getProperty("user.dir")+ File.separator+"src"+
                                 File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
    }

    private void SetObjectsComponents() {

        customer = new Customer();
        customer.setCustType("New");
    }

    private void SetGUIComponents(boolean showCustomersTable) {

        this.showCustomersTable = showCustomersTable;

        getComboBoxModel().addElement("New");
        getComboBoxModel().addElement("VIP");
        getComboBoxModel().addElement("Returned");

        getType_box().setEnabled(false);
    }

    protected void InitializeActions() {

        SetObjectsComponents();
        SetGUIComponents(showCustomersTable);

        getType_box().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customer.setCustType((String) getType_box().getSelectedItem());
            }
        });

        getRegisterButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    customer.setCustName( getFullname_text().getText());
                    customer.setCustTel( getPhone_text().getText());

                    if (!CheckCustomerFields(customer)) {
                        return;
                    }

                    client = new Client();

                    PrepareAndSendJsonDataNewCustomerToServer();

                    String response = GetRegisterCustomerResponseFromServer();

                    if (!response.equals("")) {
                        JOptionPane.showMessageDialog(null, response);
                        client.getSslSocket().close();

                    } else {

                        JOptionPane.showMessageDialog(null, "Customer ID :" + "was registered successfully");
                        client.getSslSocket().close();
                        logger.info("Registered successfully - Customer ID : " + customer.getCustId());

                        getJFrame().setVisible(false);

                        if (showCustomersTable) {

                            DrawCustomersList();
                        }
                    }

                    getjFramePrev().setEnabled(true);

                } catch (Exception ex) {
                    System.out.print(ex);
                }
            }
        });

        getCheckButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){

                try {

                    customer.setCustId(getId_text().getText());

                    if(!CheckId(customer.getCustId())) {
                        return;
                    }

                    client = new Client();

                    PrepareAndSendJsonDataIdForCheckingToServer();

                    String response = GetRegisterCustomerResponseFromServer();

                    if (response.equals("")) {
                        client.getSslSocket().close();
                        JOptionPane.showMessageDialog(null, "ID Customer : " + customer.getCustId() + " is already Exist !!!");
                        getCheckButton().setEnabled(false);
                        getId_text().setText("");

                    } else {
                        client.getSslSocket().close();
                        SetGUIComponentsToEdit();
                    }

                } catch (Exception ex) {
                    System.out.print(ex);
                }
            }
        });
    }

    private void SetGUIComponentsToEdit() {

        getId_text().setEnabled(false);

        getPhone_text().setEnabled(true);
        getFullname_text().setEnabled(true);

        getCheckButton().setVisible(false);
        getRegisterButton().setVisible(true);
        getRegisterButton().setEnabled(true);
    }

    private void DrawCustomersList() {

        try {

            getjFramePrev().setVisible(false);
            CustomerListManagement customerListManagement = new CustomerListManagement(getTwoBackFrame(), empBranchName);
            customerListManagement.setUndecorated(true);
            customerListManagement.DrawCustomer();
            customerListManagement.InitializeActions();
            customerListManagement.setVisible(true);

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    private void PrepareAndSendJsonDataIdForCheckingToServer() throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "Is customer in DB");
        jsonObject.put("customerId", customer.getCustId());

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    private String GetRegisterCustomerResponseFromServer() throws IOException, ParseException {

        String line = client.getInputStream().readUTF();

        JSONObject jsonObjectResponse;
        JSONParser jsonParser = new JSONParser();
        jsonObjectResponse = (JSONObject) jsonParser.parse(line);
        String response = (String) jsonObjectResponse.get("Failed");

        return response;
    }

    private void PrepareAndSendJsonDataNewCustomerToServer() throws IOException {

        jsonObject = new JSONObject();

        objectMapper = new ObjectMapper();
        String custStr = objectMapper.writeValueAsString(customer);
        jsonObject.put("GuiName", "Customer register");
        jsonObject.put("customer", custStr);

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();

    }

    private static boolean CheckCustomerFields(Customer customer){

        if (!(customer.getCustName().length() <= 20 && customer.getCustName().length() >= 5)) {
            JOptionPane.showMessageDialog(null, "Full Name: Legal length name  is between 5 to 20 letters");
            return false;
        }
        if (!(OnlyLettersSpaces(customer.getCustName()))) {
            JOptionPane.showMessageDialog(null, "Full Name: Please insert only Letters and Spaces");
            return false;
        }


        if (!(customer.getCustTel().matches("[0-9]+"))) {
            JOptionPane.showMessageDialog(null, "Phone: Please enter only digits");
            return false;
        }
        if (!(customer.getCustTel().length() <= 10 && customer.getCustTel().length() >= 9)) {
            JOptionPane.showMessageDialog(null, "Phone: Your phone number incorrect");
            return false;
        }

        return true;
    }

    private static boolean OnlyLettersSpaces(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (Character.isLetter(ch) || ch == ' ') {
                continue;
            }
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
                RegisterCustomer customers = new RegisterCustomer(null, null, true, "asdasdf");
                customers.DrawRegister();
                customers.InitializeActions();
                customers.setVisible(true);
                customers.setLocationRelativeTo(null);
    }
}
