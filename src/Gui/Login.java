package Gui;

import Classes.Employee;
import Client.Client;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.Collections;

public class Login extends JFrame {

    public static final String CASTRO_ICON = "/src/images/icon.png";
    public static final String CASTRO_TAG_LOGIN = "/src/images/tagLogIn.png";
    public static final String FRAME_NAME = "CASTRO - LOG IN";
    public static final String LABEL_ID_NAME = "Employee ID :";
    public static final String LABEL_PASSWORD_NAME = "Password :";
    public static final String LABEL_BRANCH_NAME = "Branch Name :";
    public static final String BUTTON_LOGIN_NAME = "Log in";
    public static final String BUTTON_CANCEL = "Cancel";
    public static final String DEFAULT_BRANCH_NAME = "Jerusalem";
    public static final String BRANCH_NAME1 = "Tel Aviv";
    public static final String BRANCH_NAME2 = "Jerusalem";

    public static final int FRAMEֹֹ_WIDTH_ֹSIZE = 1279;
    public static final int FRAMEֹֹ_HEIGHT_ֹSIZE = 565;
    public static final int FRAMEֹֹ_POSITION_X = 133;
    public static final int FRAMEֹֹ_POSITION_Y = 250;
    public static final int JTEXTFIELD_ֹSIZE = 15;

    private Employee employee;
    private JSONObject jsonObject;

    private JFrame prevFrame;
    private JPanel jPanelData;
    private SpringLayout springLayout;
    private JLabel jLabelId, jLabelPass, jLabelBranch, tagLogInLabel;
    private JTextField jTextFieldId;
    private JPasswordField jTextFieldPass;
    private JComboBox<String> jComboBoxBranch;
    private JButton jButtonLogin, jButtonCancel;
    private ImageIcon tagLogInJPG;
    private Client client;

    public Login(JFrame prevFrame) {

        SetObjectsComponents();
        SetGUIComponents(prevFrame);

        client = new Client();

    }

    private void SetObjectsComponents() {

        employee = new Employee();
        employee.setEmpBranch(DEFAULT_BRANCH_NAME); // Default value for branch name field
    }

    private void SetGUIComponents(JFrame prevFrame) {

        this.prevFrame = prevFrame;

        jPanelData = new JPanel();
        springLayout = new SpringLayout();

        jLabelId = new JLabel(LABEL_ID_NAME);
        jTextFieldId = new JTextField(JTEXTFIELD_ֹSIZE);
        jTextFieldId.setText("333333");

        jLabelPass = new JLabel(LABEL_PASSWORD_NAME);
        jTextFieldPass = new JPasswordField(JTEXTFIELD_ֹSIZE);
        jTextFieldPass.setText("SSSsss333");
        employee.setEmpPass("SSSsss333");

        jLabelBranch = new JLabel(LABEL_BRANCH_NAME);
        jComboBoxBranch = new JComboBox<>(new String[]{BRANCH_NAME1, BRANCH_NAME2});

        jButtonLogin = new JButton(BUTTON_LOGIN_NAME);
        jButtonCancel = new JButton(BUTTON_CANCEL);

        tagLogInJPG = new ImageIcon(getClass().getResource(CASTRO_TAG_LOGIN));
        tagLogInLabel = new JLabel(tagLogInJPG);
    }

    private void InitializeActions() {

        jTextFieldId.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {


                employee.setEmpId(jTextFieldId.getText());
            }
        });

        jTextFieldPass.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {

                employee.setEmpPass(new String(jTextFieldPass.getPassword()));
            }
        });

        jComboBoxBranch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                employee.setEmpBranch((String) jComboBoxBranch.getSelectedItem());
            }
        });

        jButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(LoginDataCorrect(employee.getEmpId(), employee.getEmpPass())) {

                        try {

                            PrepareAndSendJsonDataToServer();

                            String responseFromServer = GetResponseFromServer();

                            client.getSslSocket().close();

                            if (!responseFromServer.equals("")) {
                                JOptionPane.showMessageDialog(null, responseFromServer);

                            } else {

                                // Before drawing the next window hide the previous and the current windows
                                HidePreviousWindow(prevFrame);

                                // Draw the Main Menu user according to the employee type
                                DrawTheRelevantMainMenuUser();

                            }
                        } catch (Exception ex) {
                            System.out.print(ex);
                        }
                }
                else
                    JOptionPane.showMessageDialog(null, "Your Employee ID or Password is incorrect !!!");
            }
        });

        jButtonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setVisible(false);
                prevFrame.setEnabled(true);
                prevFrame.toFront();
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                prevFrame.setEnabled(true);
                prevFrame.toFront();
            }
        });


    }

    private void DrawTheRelevantMainMenuUser() {

        switch (employee.getEmpType()) {

            case "Seller": {

                GUICreateMainMenuSeller();
                break;
            }

            case "Cashier": {

                GUICreateMainMenuCashier();
                break;
            }

            case "Shift Manager": {

                GUICreateMainMenuAdmin();
                break;
            }
        }
    }

    private void GUICreateMainMenuAdmin() {

        MainMenuAdmin mainMenuAdmin = new MainMenuAdmin(employee);
        mainMenuAdmin.DrawMainMenu();
        mainMenuAdmin.InitializeActions();
        mainMenuAdmin.setVisible(true);
        mainMenuAdmin.setLocationRelativeTo(null);
        mainMenuAdmin.resizePhotoInMenu();
    }

    private void GUICreateMainMenuCashier() {

        MainMenuCashier mainMenuCashier = new MainMenuCashier(employee);
        mainMenuCashier.DrawMainMenu();
        mainMenuCashier.InitializeActions();
        mainMenuCashier.setVisible(true);
        mainMenuCashier.setLocationRelativeTo(null);
        mainMenuCashier.resizePhotoInMenu();
    }

    private void GUICreateMainMenuSeller() {

        MainMenuSeller mainMenu = new MainMenuSeller(employee);
        mainMenu.DrawMainMenu();
        mainMenu.InitializeActions();
        mainMenu.setVisible(true);
        mainMenu.setLocationRelativeTo(null);
        mainMenu.resizePhotoInMenu();
    }

    protected void DrawLogin (){

        GUISettingForJFrame();
        GUISettingForJPanel();
        InitializeActions();
        }

    private void GUISettingForJPanel() {

        GUIDefineLayoutAndAddComponentsOnJPanel();
        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

    GUIPlaceTagLogInLabel();
    GUIPlaceIDLabelAndTextField();
    GUIPlacePasswordLabelAndTextField();
    GUIPlaceBranchLabelAndTextField();
    GUIPlaceLogInButton();
    GUIPlaceCancelButton();
    }

    private void GUIPlaceLogInButton() {

        springLayout.putConstraint(SpringLayout.WEST, jButtonLogin, 400, SpringLayout.WEST, jPanelData);
        springLayout.putConstraint(SpringLayout.NORTH, jButtonLogin, 500, SpringLayout.NORTH, jPanelData);
    }

    private void GUIPlaceCancelButton() {

        springLayout.putConstraint(SpringLayout.WEST, jButtonCancel, 50, SpringLayout.EAST, jButtonLogin);
        springLayout.putConstraint(SpringLayout.NORTH, jButtonCancel, 500, SpringLayout.NORTH, jPanelData);
    }

    private void GUIPlaceBranchLabelAndTextField() {

        springLayout.putConstraint(SpringLayout.WEST, jLabelBranch, 300, SpringLayout.WEST, jPanelData);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelBranch, 30, SpringLayout.SOUTH, jLabelPass);
        springLayout.putConstraint(SpringLayout.WEST, jComboBoxBranch, 10, SpringLayout.EAST, jLabelBranch);
        springLayout.putConstraint(SpringLayout.NORTH, jComboBoxBranch, 30, SpringLayout.NORTH, jTextFieldPass);
    }

    private void GUIPlacePasswordLabelAndTextField() {

        springLayout.putConstraint(SpringLayout.WEST, jLabelPass, 300, SpringLayout.WEST, jPanelData);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelPass, 30, SpringLayout.SOUTH, jLabelId);
        springLayout.putConstraint(SpringLayout.WEST, jTextFieldPass, 10, SpringLayout.EAST, jLabelPass);
        springLayout.putConstraint(SpringLayout.NORTH, jTextFieldPass, 30, SpringLayout.SOUTH, jTextFieldId);
    }

    private void GUIPlaceIDLabelAndTextField() {

        springLayout.putConstraint(SpringLayout.WEST, jLabelId, 300, SpringLayout.WEST, jPanelData);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelId, 300, SpringLayout.NORTH, jPanelData);
        springLayout.putConstraint(SpringLayout.WEST, jTextFieldId, 10, SpringLayout.EAST, jLabelId);
        springLayout.putConstraint(SpringLayout.NORTH, jTextFieldId, 300, SpringLayout.NORTH, jPanelData);
    }

    private void GUIPlaceTagLogInLabel() {

        springLayout.putConstraint(SpringLayout.WEST, tagLogInLabel, 0, SpringLayout.WEST, jPanelData);
        springLayout.putConstraint(SpringLayout.NORTH, tagLogInLabel, 150, SpringLayout.NORTH, jPanelData);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelData.setLayout(springLayout);
        jPanelData.add(jLabelId);
        jPanelData.add(jTextFieldId);
        jPanelData.add(jLabelPass);
        jPanelData.add(jTextFieldPass);
        jPanelData.add(jLabelBranch);
        jPanelData.add(jComboBoxBranch);
        jPanelData.add(jButtonLogin);
        jPanelData.add(jButtonCancel);
        jPanelData.add(tagLogInLabel);
    }

    private void GUISettingForJFrame() {

        this.setTitle(FRAME_NAME);
        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE, FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setLocation(FRAMEֹֹ_POSITION_X,FRAMEֹֹ_POSITION_Y);
        this.setIconImages(Collections.singletonList(Toolkit.getDefaultToolkit().getImage(getClass().getResource(CASTRO_ICON))));
        this.setResizable(false);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.add(jPanelData);
    }

    private void HidePreviousWindow(JFrame prevFrame) {

        prevFrame.setVisible(false);
        getJFrame().setVisible(false);
    }

    private String GetResponseFromServer() throws IOException, ParseException {

        String line = client.getInputStream().readUTF();
        JSONObject jsonObjectResponse;
        JSONParser jsonParser = new JSONParser();
        jsonObjectResponse = (JSONObject) jsonParser.parse(line);
        String responseFromServer = (String) jsonObjectResponse.get("Failed");
        employee.setEmpType((String) jsonObjectResponse.get("employee type"));
        employee.setEmpName((String) jsonObjectResponse.get("employee name"));
        employee.setEmpSn((String) jsonObjectResponse.get("employee sn"));
        employee.setEmpBank((String) jsonObjectResponse.get("employee bank"));
        employee.setEmpTel((String) jsonObjectResponse.get("employee tel"));
        employee.setEmpPhoto((String) jsonObjectResponse.get("employee photo"));

        return responseFromServer;
    }

    private void PrepareAndSendJsonDataToServer() throws IOException {

        jsonObject = new JSONObject();
        jsonObject.put("GuiName", "Log in");
        jsonObject.put("employee id", employee.getEmpId());
        jsonObject.put("employee pass", employee.getEmpPass());
        jsonObject.put("employee branch", employee.getEmpBranch());
        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    private static boolean CheckPassSyntax (String str){

        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;

        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (Character.isDigit(ch)) {
                numberFlag = true;
            } else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if (numberFlag && capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }

    private boolean LoginDataCorrect (String empId, String empPass){

        if (empId.length() >= 1 && empId.length() <= 9) {
            if (empId.matches("[0-9]+")) {
                if (empPass.length() >= 8 && empPass.length() <= 15) {
                    if (CheckPassSyntax(empPass)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public JFrame getJFrame () {
        return this;
    }

    public static void main (String[]args) {

            Login login = new Login(null);
            login.DrawLogin();
            login.setVisible(true);
            login.setLocationRelativeTo(null);
        }
}






































