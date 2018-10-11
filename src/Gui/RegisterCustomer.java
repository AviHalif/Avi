package Gui;

import Classes.Customer;
import Client.Client;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RegisterCustomer extends Register {

    private static Logger logger = Logger.getLogger(RegisterCustomer.class.getName());

    private Customer customer;
    private boolean showCustomersTable;
    private String empBranchName, propFileName;

    private Properties properties;
    private InputStream inStream;

    private Client client;

    private JSONObject jsonObject;
    private ObjectMapper objectMapper;


    public RegisterCustomer(JFrame oneBackFrame, JFrame twoBackFrame, boolean showCustomersTable, String empBranchName) throws IOException { // הגיע לכאן המסך הראשי של האדמין ואת המסך של הניהול עובדים

        super(oneBackFrame,twoBackFrame);

        this.showCustomersTable = showCustomersTable;

        this.empBranchName = empBranchName;

        DefineLogAndConfig();
    }

    private void DefineLogAndConfig() throws IOException {

        properties = new Properties();
        propFileName = "config.properties";
        inStream = RegisterEmployee.class.getClassLoader().getResourceAsStream(propFileName);
        properties.load(inStream);
        String LogPath = properties.getProperty("LogPath");
        FileHandler fileHandler = new FileHandler(LogPath);
        logger.addHandler(fileHandler);
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        fileHandler.setFormatter(simpleFormatter);
    }

    private void SetObjectsComponents() {

        customer = new Customer();
        customer.setCustType("New");
    }

    private void SetGUIComponents(boolean showCustomersTable) {

        GUISettingForComponents(showCustomersTable);

        GUISettingForJFrame();
    }

    private void GUISettingForComponents(Boolean showCustomersTable) {

        this.showCustomersTable = showCustomersTable;

        getComboBoxModel().addElement("New");
        getComboBoxModel().addElement("VIP");
        getComboBoxModel().addElement("Returned");

        getType_box().setEnabled(false);
    }

    private void GUISettingForJFrame() {

        this.setTitle("Customer Register");
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

        getRegister().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    customer.setCustName( getFullname_text().getText());
                    customer.setCustTel( getPhone_text().getText());

                    if (!CheckCustomerFields(customer)) { // בדיקת תקינות קלט ת.ז
                        return;
                    }

                    client = new Client();

                    PrepareAndSendJsonDataNewCustomerToServer(); // הכנסת הלקוח החדש למערכת

                    String response = GetRegisterCustomerResponseFromServer();

                    if (!response.equals("")) { // לא הצליח לרשום את הלקוח החדש
                        JOptionPane.showMessageDialog(null, response);
                        client.getSslSocket().close();

                    } else { // אם הצליח לרשום את הלקוח החדש

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

        getOKButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){

                try {

                    customer.setCustId(getId_text().getText());

                    if(!CheckId(customer.getCustId())) { // בדיקת תקינות קלט ת.ז
                        return;
                    }

                    client = new Client();

                    PrepareAndSendJsonDataIdForCheckingToServer(); // בדיקה האם הת.ז נמצא כבר במערכת

                    String response = GetRegisterCustomerResponseFromServer();

                    if (response.equals("")) { // אם התז כבר נמצא במערכת תציג הודעה ותאפס את השדה טקסט
                        client.getSslSocket().close();
                        JOptionPane.showMessageDialog(null, "ID Customer : " + customer.getCustId() + " is already Exist !!!");
                        getOKButton().setEnabled(false);
                        getId_text().setText("");

                    } else { // אם הת. לא נמצאת בבסיס נתונים עדיין אז תפתח את הנתונים להזנה ותנעל את הת.ז ללא עריכה
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

        getOKButton().setVisible(false); // תחליף בין הכפתורים ע"י העלמתם
        getRegister().setVisible(true);
    }

    private void DrawCustomersList() {

        try {

            getjFramePrev().setVisible(false);
            CustomerListManagement customerListManagement = new CustomerListManagement(getTwoBackFrame(), empBranchName);
            customerListManagement.DrawCustomer();
            customerListManagement.InitializeActions();
            customerListManagement.setVisible(true);
            customerListManagement.setLocationRelativeTo(null);

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
