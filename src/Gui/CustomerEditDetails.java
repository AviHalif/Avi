package Gui;

import Classes.Customer;
import Client.Client;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class CustomerEditDetails extends JFrame {

    private Customer customer;
    private String branchName;
    private Client client;
    private JPanel jPanelMain;
    private JComboBox type_box;
    private JSONObject jsonObject;
    private ObjectMapper objectMapper;
    private SpringLayout springLayout;
    private DefaultComboBoxModel comboBoxModel;
    private JFrame oneBackFrame, twoBackFrame, thirdBackFrame;
    private JTextField idCustomer_data, nameCustomer_data, telCustomer_data;
    private JButton nameEdit_btton, telEdit_btton, typeEdit_btton, ok_button, cancel_button;
    private JLabel jLabelId, jLabelname, jLabelphone, backgroundPhotoLabel, titleLabel, jLabeltype;
    public static final int FRAMEֹֹ_POSITION_X = 7, FRAMEֹֹ_POSITION_Y = 230, FRAMEֹֹ_WIDTH_ֹSIZE = 1520, FRAMEֹֹ_HEIGHT_ֹSIZE = 630, JTEXTFIELD_WIDTH = 250, JTEXTFIELD_HEIGHT = 50,
                            BUTTON_WIDTH = 220, BUTTON_HEIGHT = 50, BUTTON_WIDTH2 = 360;
    private ImageIcon  backgroundPhotoTopPanelJPG, backLogoJPG, titlePhotoJPG, label_id, label_name, label_phone, label_type, nameEditLogoJPG, phoneEditLogoJPG,
                       typeEditLogoJPG, updateCustLogoJPG;
    public static final String UPDATE_CUSTOMER = "/src/images/update_customer.png", JOPTIONPANE_MESSAGE = "Are you sure you want to update customer ID number: ",
                               JOPTIONPANE_TITLE = "CASTRO - UPDATE DETAILS CUSTOMER", TITLE = "/src/images/customer_update.png", BACK_PHOTO = "/src/images/back_edit_1.png",
                               BACK_BUTTON = "/src/images/back.png", LABEL_NAME = "/src/images/update_name.png", LABEL_PHONE = "/src/images/update_phone.png",
                               LABEL_TYPE = "/src/images/update_type.png", LABEL_ID = "/src/images/update_id.png", EDIT_PHONE = "/src/images/edit_phone.png",
                               EDIT_NAME = "/src/images/edit_name.png", EDIT_TYPE = "/src/images/edit_type.png";


    public CustomerEditDetails(String custId,String custName,String custTel,String custType,
                               JFrame oneBackFrame,JFrame twoBackFrame,JFrame thirdBackFrame, String branchName) {

        SetGUIComponents(oneBackFrame,twoBackFrame,thirdBackFrame);

        SetObjectsComponents(branchName,custId,custName,custTel,custType);
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
                nameCustomer_data.setBorder(BorderFactory.createLineBorder(Color.red));
            }
        });

        telEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                telCustomer_data.setEnabled(true);
                telCustomer_data.setBorder(BorderFactory.createLineBorder(Color.red));
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
                type_box.setBorder(BorderFactory.createLineBorder(Color.red));

            }
        });

        type_box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customer.setCustType((String) getType_box().getSelectedItem());
            }
        });

        type_box.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {

                type_box.setBorder(BorderFactory.createLineBorder(Color.red));
            }

            @Override
            public void focusLost(FocusEvent e) {

                super.focusLost(e);
                type_box.setBorder(BorderFactory.createLineBorder(Color.black));

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
                nameCustomer_data.setBorder(BorderFactory.createLineBorder(Color.black));
            }

            @Override
            public void focusGained(FocusEvent e) {

                nameCustomer_data.setBorder(BorderFactory.createLineBorder(Color.red));
            }
        });

        telCustomer_data.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                customer.setCustTel(telCustomer_data.getText());
                telCustomer_data.setBorder(BorderFactory.createLineBorder(Color.black));
            }

            @Override
            public void focusGained(FocusEvent e) {

                telCustomer_data.setBorder(BorderFactory.createLineBorder(Color.red));
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
        customerListManagement.setUndecorated(true);

        try {

            customerListManagement.DrawCustomer();

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        customerListManagement.InitializeActions();
        customerListManagement.setVisible(true);
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

        springLayout.putConstraint(SpringLayout.WEST,idCustomer_data,250,SpringLayout.WEST,titleLabel);
        springLayout.putConstraint(SpringLayout.NORTH,idCustomer_data,45,SpringLayout.SOUTH,titleLabel);

        springLayout.putConstraint(SpringLayout.WEST,nameCustomer_data,15,SpringLayout.EAST,jLabelname);
        springLayout.putConstraint(SpringLayout.NORTH,nameCustomer_data,50,SpringLayout.SOUTH,idCustomer_data);

        springLayout.putConstraint(SpringLayout.WEST,type_box,35,SpringLayout.EAST,jLabeltype);
        springLayout.putConstraint(SpringLayout.NORTH,type_box,50,SpringLayout.SOUTH,nameCustomer_data);

        springLayout.putConstraint(SpringLayout.WEST,telCustomer_data,10,SpringLayout.EAST,jLabelphone);
        springLayout.putConstraint(SpringLayout.NORTH,telCustomer_data,55,SpringLayout.SOUTH,type_box);
    }

    private void GUIPlaceButtonsComponents() {

        springLayout.putConstraint(SpringLayout.EAST,nameEdit_btton,-600,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,nameEdit_btton,50,SpringLayout.SOUTH,idCustomer_data);

        springLayout.putConstraint(SpringLayout.EAST,telEdit_btton,-600,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,telEdit_btton,50,SpringLayout.SOUTH,typeEdit_btton);

        springLayout.putConstraint(SpringLayout.EAST,ok_button,-60,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.SOUTH,ok_button,-55,SpringLayout.SOUTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,cancel_button,0,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.SOUTH,cancel_button,0,SpringLayout.SOUTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.EAST,typeEdit_btton,-600,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,typeEdit_btton,50,SpringLayout.SOUTH,nameEdit_btton);
    }

    private void GUIPlaceLabelsComponents() {

        springLayout.putConstraint(SpringLayout.WEST,titleLabel,120,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,titleLabel,25,SpringLayout.NORTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,jLabelId,135,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,jLabelId,30,SpringLayout.SOUTH,titleLabel);

        springLayout.putConstraint(SpringLayout.WEST,jLabelname,155,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,jLabelname,20,SpringLayout.SOUTH,jLabelId);

        springLayout.putConstraint(SpringLayout.WEST,jLabelphone,160,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,jLabelphone,20,SpringLayout.SOUTH,jLabeltype);

        springLayout.putConstraint(SpringLayout.WEST,jLabeltype,135,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,jLabeltype,20,SpringLayout.SOUTH,jLabelname);

        springLayout.putConstraint(SpringLayout.EAST,backgroundPhotoLabel,300,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,backgroundPhotoLabel,0,SpringLayout.NORTH,jPanelMain);
    }

    private void GUIAddComponentsOnJPanel() {

        jPanelMain = new JPanel();

        GUISetTextFieldsAndComboComponents();

        GUISetLabelsComponents();

        GUISetButtonsComponents();

        jPanelMain.add(backgroundPhotoLabel);
    }

    private void GUISetButtonsComponents() {

        nameEditLogoJPG = new ImageIcon(getClass().getResource(EDIT_NAME));
        nameEdit_btton = new JButton(nameEditLogoJPG);
        nameEdit_btton.setBorderPainted(true);
        nameEdit_btton.setOpaque(false);
        nameEdit_btton.setBackground(Color.white);
        nameEdit_btton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        nameEdit_btton.setBorder(new LineBorder(Color.red));

        phoneEditLogoJPG = new ImageIcon(getClass().getResource(EDIT_PHONE));
        telEdit_btton = new JButton(phoneEditLogoJPG);
        telEdit_btton.setBorderPainted(true);
        telEdit_btton.setOpaque(false);
        telEdit_btton.setBackground(Color.white);
        telEdit_btton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        telEdit_btton.setBorder(new LineBorder(Color.red));

        typeEditLogoJPG = new ImageIcon(getClass().getResource(EDIT_TYPE));
        typeEdit_btton = new JButton(typeEditLogoJPG);
        typeEdit_btton.setBorderPainted(true);
        typeEdit_btton.setOpaque(false);
        typeEdit_btton.setBackground(Color.white);
        typeEdit_btton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        typeEdit_btton.setBorder(new LineBorder(Color.red));

        updateCustLogoJPG = new ImageIcon(getClass().getResource(UPDATE_CUSTOMER));
        ok_button = new JButton(updateCustLogoJPG);
        ok_button.setBorderPainted(true);
        ok_button.setBackground(Color.black);
        ok_button.setPreferredSize(new Dimension(BUTTON_WIDTH2, BUTTON_HEIGHT));
        ok_button.setBorder(new LineBorder(Color.red));

        backgroundPhotoTopPanelJPG = new ImageIcon(getClass().getResource(BACK_PHOTO));
        backgroundPhotoLabel = new JLabel(backgroundPhotoTopPanelJPG);

        backLogoJPG = new ImageIcon(getClass().getResource(BACK_BUTTON));
        cancel_button = new JButton(backLogoJPG);
        cancel_button.setBorderPainted(false);
        cancel_button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.black, Color.black));

        jPanelMain.add(nameEdit_btton);
        jPanelMain.add(telEdit_btton);
        jPanelMain.add(typeEdit_btton);

        jPanelMain.add(ok_button);
        jPanelMain.add(cancel_button);
    }

    private void GUISetTextFieldsAndComboComponents() {

        idCustomer_data = new JTextField();
        idCustomer_data.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        idCustomer_data.setHorizontalAlignment(JTextField.CENTER);
        idCustomer_data.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        idCustomer_data.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        idCustomer_data.setForeground (Color.black);
        idCustomer_data.setOpaque(false);
        idCustomer_data.setEnabled(false);

        nameCustomer_data = new JTextField();
        nameCustomer_data.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        nameCustomer_data.setHorizontalAlignment(JTextField.CENTER);
        nameCustomer_data.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        nameCustomer_data.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        nameCustomer_data.setForeground (Color.black);
        nameCustomer_data.setOpaque(false);
        nameCustomer_data.setEnabled(false);

        telCustomer_data = new JTextField();
        telCustomer_data.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        telCustomer_data.setHorizontalAlignment(JTextField.CENTER);
        telCustomer_data.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        telCustomer_data.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        telCustomer_data.setForeground (Color.black);
        telCustomer_data.setOpaque(false);
        telCustomer_data.setEnabled(false);

        comboBoxModel = new DefaultComboBoxModel();
        type_box = new JComboBox(comboBoxModel);

        type_box.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        type_box.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        type_box.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        type_box.setOpaque(false);

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

        titlePhotoJPG = new ImageIcon(getClass().getResource(TITLE));
        titleLabel = new JLabel(titlePhotoJPG);

        label_id = new ImageIcon(getClass().getResource(LABEL_ID));
        jLabelId = new JLabel(label_id);

        label_name = new ImageIcon(getClass().getResource(LABEL_NAME));
        jLabelname = new JLabel(label_name);

        label_phone = new ImageIcon(getClass().getResource(LABEL_PHONE));
        jLabelphone = new JLabel(label_phone);

        label_type = new ImageIcon(getClass().getResource(LABEL_TYPE));
        jLabeltype = new JLabel(label_type);

        jPanelMain.add(titleLabel);
        jPanelMain.add(jLabelId);
        jPanelMain.add(jLabelname);
        jPanelMain.add(jLabelphone);
        jPanelMain.add(jLabeltype);
    }

    private void GUISettingForJFrame() {

        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE,FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setLocation(FRAMEֹֹ_POSITION_X,FRAMEֹֹ_POSITION_Y);
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

    public JPanel getjPanelMain() {

        return jPanelMain;
    }

    public static void main(String[] args) {
        CustomerEditDetails CustomerEditDetails = new CustomerEditDetails("25232342", "dfsfsdf", "44444444","SDFEF",
                                                                     null,null,null,"sdfdsf");
        CustomerEditDetails.DrawCustomerEditDetails();
        CustomerEditDetails.InitializeActions();
        CustomerEditDetails.setVisible(true);
        CustomerEditDetails.setLocationRelativeTo(null);
    }
}
