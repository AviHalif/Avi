package Gui;

import Classes.Customer;
import Client.Client;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class CustomerEditDetails extends JFrame {

    public static final String JOPTIONPANE_MESSAGE = "Are you sure you want to update customer ID number: ";
    public static final String JOPTIONPANE_TITLE = "CASTRO - UPDATE DETAILS CUSTOMER";
    public static final String JOPTIONPANE_MESSAGE_CLOSE = "Are you sure you want to close the window? Data will not be saved";
    public static final String JOPTIONPANE_TITLE_CLOSE = "CASTRO";

    private Customer customer;
    private String branchName;
    private Client client;

    private JSONObject jsonObject;
    private ObjectMapper objectMapper;

    private JFrame oneBackFrame, twoBackFrame, thirdBackFrame;
    private JPanel jPanelMain;
    private JLabel label_message, idCustomer_label, nameCustomer_label, telCustomer_label, typeCustomer_label;

    private  SpringLayout springLayout;

    private JTextField idCustomer_data, nameCustomer_data, telCustomer_data;

    private JComboBox type_box;
    private DefaultComboBoxModel comboBoxModel;

    private JButton nameEdit_btton, telEdit_btton, typeEdit_btton, ok_button, cancel_button;


    public CustomerEditDetails(String custId,String custName,String custTel,String custType,
                               JFrame oneBackFrame,JFrame twoBackFrame,JFrame thirdBackFrame, String branchName) {
//// הגיעה החלונית הקטנה להזנת ת.ז לעדכון, המסך הראשי של האדמין ואת המסך של הניהול עובדים

        SetGUIComponents(oneBackFrame,twoBackFrame,thirdBackFrame);

        SetObjectsComponents(branchName,custId,custName,custTel,custType);

        /*
        if(custType.equals("New"))
        this.customer = new NewCustomer(custId,custName,custTel,custType);

        else
            if(custType.equals("Return"))
                this.customer = new ReturnedCustomer(custId,custName,custTel,custType);

            else
            if(custType.equals("VIP"))
                this.customer = new VipCustomer(custId,custName,custTel,custType);
*/
    }

    private void SetGUIComponents(JFrame oneBackFrame, JFrame twoBackFrame, JFrame thirdBackFrame) {

        this.oneBackFrame = oneBackFrame;
        this.twoBackFrame = twoBackFrame;
        this.thirdBackFrame = thirdBackFrame;
    }

    private void SetObjectsComponents(String branchName,String custId,String custName,String custTel,String custType) {

        customer = new Customer(custId,custName,custTel,custType);
        this.branchName = branchName;
    }

    protected void InitializeActions() {

        ok_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    if (!CheckCustomerFields(customer)) {
                        return;
                    }

                    if (JOptionPane.showConfirmDialog(getJFrame(),
                            JOPTIONPANE_MESSAGE + customer.getCustId() + " ?", JOPTIONPANE_TITLE,
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                        client = new Client();

                        PrepareAndSendJsonUpdateCustomerDataToServer();

                        String response="";

                        try {
                            response = GetResponseUpdateCustomerFromServer();
                        }
                        catch (Exception ex){}

                        if (!response.equals("")) {
                            JOptionPane.showMessageDialog(null, response);
                            client.getSslSocket().close();

                        } else {
                            JOptionPane.showMessageDialog(null, "ID number : " + customer.getCustId() + "   update successfully");
                            client.getSslSocket().close();
                        }

                        getJFrame().setVisible(false);
                        oneBackFrame.setVisible(false);

                        DrawCustomerListManagement();
                    }

                    else
                    {
                        getJFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }

                } catch (Exception ex) {
                    System.out.print(ex);
                }
            }
        });

        nameEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                nameCustomer_data.setEnabled(true);
            }
        });

        telEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                telCustomer_data.setEnabled(true);
            }
        });

        typeEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                comboBoxModel.removeAllElements();
                getComboBoxModel().addElement("New");
                getComboBoxModel().addElement("VIP");
                getComboBoxModel().addElement("Returned");
                type_box.setEnabled(true);
            }
        });

        type_box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customer.setCustType((String) getType_box().getSelectedItem());
            }
        });

        getCancel_button().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){

                getJFrame().setVisible(false);
                oneBackFrame.setVisible(false);

                DrawCustomerListManagement();
            }
        });

        nameCustomer_data.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                customer.setCustName(nameCustomer_data.getText());
            }
        });

        telCustomer_data.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                customer.setCustTel(telCustomer_data.getText());

            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                oneBackFrame.setEnabled(true);
                oneBackFrame.toFront();
            }
        });
    }

    private void DrawCustomerListManagement() {

        CustomerListManagement customerListManagement = new CustomerListManagement(twoBackFrame,branchName);

        try {

            customerListManagement.DrawCustomer();

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        customerListManagement.InitializeActions();
        customerListManagement.setVisible(true);
        customerListManagement.setLocationRelativeTo(null);
    }

    private String GetResponseUpdateCustomerFromServer() throws ParseException, IOException {

        String line = client.getInputStream().readUTF();
        JSONObject jsonObjectResponse;
        JSONParser jsonParser = new JSONParser();
        jsonObjectResponse = (JSONObject) jsonParser.parse(line);
        String response = (String) jsonObjectResponse.get("Failed");

        return response;
    }

    private void PrepareAndSendJsonUpdateCustomerDataToServer() throws IOException {

        jsonObject = new JSONObject();
        objectMapper = new ObjectMapper();

        String custStr = objectMapper.writeValueAsString(customer);
        jsonObject.put("GuiName", "Update Customer");
        jsonObject.put("customer", custStr);

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    protected void DrawCustomerEditDetails() {

        GUISettingForJFrame();

        GUISettingForJPanel();

        this.setContentPane(jPanelMain);
    }

    private void GUISettingForJPanel() {

        GUIAddComponentsOnJPanel();

        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout = new SpringLayout();
        jPanelMain.setLayout(springLayout);

        GUIPlaceLabelsComponents();

        GUIPlaceButtonsComponents();

        GUIPlaceTextFieldsAndComboComponents();
    }

    private void GUIPlaceTextFieldsAndComboComponents() {

        springLayout.putConstraint(SpringLayout.WEST,idCustomer_data,35,SpringLayout.EAST,idCustomer_label);
        springLayout.putConstraint(SpringLayout.NORTH,idCustomer_data,25,SpringLayout.SOUTH,label_message);

        springLayout.putConstraint(SpringLayout.WEST,nameCustomer_data,35,SpringLayout.EAST,nameCustomer_label);
        springLayout.putConstraint(SpringLayout.NORTH,nameCustomer_data,25,SpringLayout.SOUTH,idCustomer_data);

        springLayout.putConstraint(SpringLayout.WEST,telCustomer_data,35,SpringLayout.EAST,telCustomer_label);
        springLayout.putConstraint(SpringLayout.NORTH,telCustomer_data,25,SpringLayout.SOUTH,nameCustomer_data);

        springLayout.putConstraint(SpringLayout.WEST,type_box,35,SpringLayout.EAST,typeCustomer_label);
        springLayout.putConstraint(SpringLayout.NORTH,type_box,25,SpringLayout.SOUTH,telCustomer_data);
    }

    private void GUIPlaceButtonsComponents() {

        springLayout.putConstraint(SpringLayout.EAST,nameEdit_btton,-100,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,nameEdit_btton,25,SpringLayout.SOUTH,idCustomer_data);

        springLayout.putConstraint(SpringLayout.EAST,telEdit_btton,-100,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,telEdit_btton,25,SpringLayout.SOUTH,nameEdit_btton);

        springLayout.putConstraint(SpringLayout.WEST,ok_button,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,ok_button,25,SpringLayout.SOUTH,typeEdit_btton);

        springLayout.putConstraint(SpringLayout.WEST,cancel_button,35,SpringLayout.EAST,ok_button);
        springLayout.putConstraint(SpringLayout.NORTH,cancel_button,25,SpringLayout.SOUTH,typeEdit_btton);

        springLayout.putConstraint(SpringLayout.EAST,typeEdit_btton,-100,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,typeEdit_btton,25,SpringLayout.SOUTH,telEdit_btton);
    }

    private void GUIPlaceLabelsComponents() {

        springLayout.putConstraint(SpringLayout.WEST,label_message,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,label_message,25,SpringLayout.NORTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,idCustomer_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,idCustomer_label,25,SpringLayout.SOUTH,label_message);

        springLayout.putConstraint(SpringLayout.WEST,nameCustomer_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,nameCustomer_label,25,SpringLayout.SOUTH,idCustomer_label);

        springLayout.putConstraint(SpringLayout.WEST,telCustomer_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,telCustomer_label,25,SpringLayout.SOUTH,nameCustomer_label);

        springLayout.putConstraint(SpringLayout.WEST,typeCustomer_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,typeCustomer_label,25,SpringLayout.SOUTH,telCustomer_label);
    }

    private void GUIAddComponentsOnJPanel() {

        jPanelMain = new JPanel();

        GUISetTextFieldsAndComboComponents();

        GUISetLabelsComponents();

        GUISetButtonsComponents();
    }

    private void GUISetButtonsComponents() {

        nameEdit_btton = new JButton("EDIT");
        telEdit_btton = new JButton("EDIT");
        typeEdit_btton = new JButton("EDIT");

        ok_button = new JButton("OK");
        cancel_button = new JButton("CANCEL");

        jPanelMain.add(nameEdit_btton);
        jPanelMain.add(telEdit_btton);
        jPanelMain.add(typeEdit_btton);

        jPanelMain.add(ok_button);
        jPanelMain.add(cancel_button);
    }

    private void GUISetTextFieldsAndComboComponents() {

        idCustomer_data = new JTextField();
        idCustomer_data.setEnabled(false);

        nameCustomer_data = new JTextField();
        nameCustomer_data.setEnabled(false);

        telCustomer_data = new JTextField();
        telCustomer_data.setEnabled(false);

        comboBoxModel = new DefaultComboBoxModel();
        type_box = new JComboBox(comboBoxModel);

        comboBoxModel.addElement(customer.getCustType());
        type_box.setEnabled(false);

        idCustomer_data.setText(customer.getCustId());
        nameCustomer_data.setText(customer.getCustName());
        telCustomer_data.setText(customer.getCustTel());

        jPanelMain.add(idCustomer_data);
        jPanelMain.add(nameCustomer_data);
        jPanelMain.add(telCustomer_data);
        jPanelMain.add(type_box);
    }

    private void GUISetLabelsComponents() {

        label_message = new JLabel("Customer details:");
        idCustomer_label = new JLabel("Customer ID:");
        nameCustomer_label = new JLabel("Customer name:");
        telCustomer_label = new JLabel("Customer phone:");
        typeCustomer_label = new JLabel("Customer type:");

        jPanelMain.add(label_message);
        jPanelMain.add(idCustomer_label);
        jPanelMain.add(nameCustomer_label);
        jPanelMain.add(telCustomer_label);
        jPanelMain.add(typeCustomer_label);
    }

    private void GUISettingForJFrame() {

        this.setTitle("Customer Edit Details");
        this.setSize(500, 400);
    }

    public JFrame getJFrame() {
        return this;
    }

    private static boolean CheckCustomerFields(Customer customer){

        if (!(customer.getCustName().length() <= 20 && customer.getCustName().length() >= 5)) {
            JOptionPane.showMessageDialog(null, "Full Name: Legal length name  is between 5 to 20 letters");
            return false;
        }
        if (!(onlyLettersSpaces(customer.getCustName()))) {
            JOptionPane.showMessageDialog(null, "Full Name: Please insert only Letters and Spaces");
            return false;
        }

        if (!(customer.getCustId().length() <= 9 && customer.getCustId().length()>=1)) {
            JOptionPane.showMessageDialog(null, "ID: Your Id length is incorrect");
            return false;
        }
        if (!(customer.getCustId().matches("[0-9]+"))) {
            JOptionPane.showMessageDialog(null, "ID: Please insert only digits");
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

    private static boolean onlyLettersSpaces(String s) {

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (Character.isLetter(ch) || ch == ' ') {
                continue;
            }
            return false;
        }
        return true;
    }

    public JComboBox getType_box() {

        return type_box;
    }

    public DefaultComboBoxModel getComboBoxModel() {

        return comboBoxModel;
    }

    public JButton getCancel_button() {

        return cancel_button;
    }

    public Customer getCustomer() {

        return customer;
    }

    public void setCustomer(Customer customer) {

        this.customer = customer;
    }

    public JPanel getjPanelMain() {

        return jPanelMain;
    }

    public static void main(String[] args) {
        CustomerEditDetails CustomerEditDetails = new CustomerEditDetails("25232342", "dfsfsdf", "44444444","SDFEF",
                null,null,null,"sdfdsf");
        // שולח את החלונית הקטנה להזנת ת.ז לעדכון, המסך הראשי של האדמין ואת המסך של הניהול עובדים
        CustomerEditDetails.DrawCustomerEditDetails();
        CustomerEditDetails.InitializeActions();
        CustomerEditDetails.setVisible(true);
        CustomerEditDetails.setLocationRelativeTo(null);
    }
}
