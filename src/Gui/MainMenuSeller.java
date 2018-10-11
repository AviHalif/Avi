package Gui;

import Classes.Employee;
import Client.Client;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainMenuSeller extends JFrame {

    public static final String FRAME_NAME = "SELLER";
    public static final String LABEL_TITLE = "Employee Details";
    public static final String LABEL_SN = "Employee S/N:";
    public static final String LABEL_NAME = "Employee Name:";
    public static final String LABEL_BRANCH = "Employee Branch:";
    public static final String LABEL_ROLE = "Employee Role:";
    public static final String BUTTON_STORAGE = "Branch Storage";
    public static final String BUTTON_SIGNOUT = "SIGN OUT";
    public static final String BUTTON_CHAT = "Chat";
    public static final String JOPTIONPANE_MESSAGE = "Are you sure you want to sign out?";
    public static final String JOPTIONPANE_TITLE = "CASTRO - SIGN OUT";
    public static final String NO_PHOTO = "/src/images/No_Image_Available.png";

    public static final int FRAMEֹֹ_WIDTH_ֹSIZE = 900;
    public static final int FRAMEֹֹ_HEIGHT_ֹSIZE = 400;
    public static final int PANELֹֹ_WIDTH_ֹSIZE = 300;
    public static final int PANELֹֹ_HEIGHT_ֹSIZE = 400;

    private Employee employee;
    private byte[] decoded;
    private Client client;

    private JSONObject jsonObject;

    private JPanel jPanelMain, jPanelLeft;
    private SpringLayout springLayout;
    private JLabel photoLabel, jLabelEmpDetails, jLabelUserNum, jLabelUserName, jLabelBranchNum, jLabelUserType;
    private ImageIcon photoLabelJPG;
    private JButton jButtonStorage, jButtonChat, jButtonSignOut;

    public MainMenuSeller(Employee employee) {

        client = new Client();

        SetObjectsComponents(employee);
        SetGUIComponents();
    }

    private void SetObjectsComponents(Employee employee) {

        this.employee = new Employee(employee);
    }

    private void SetGUIComponents() {

        jPanelMain = new JPanel();
        jPanelLeft = new JPanel();

        springLayout = new SpringLayout();
        jPanelLeft.setPreferredSize(new Dimension(PANELֹֹ_WIDTH_ֹSIZE, PANELֹֹ_HEIGHT_ֹSIZE));

        jLabelEmpDetails = new JLabel(LABEL_TITLE);
        jLabelUserNum = new JLabel( LABEL_SN + employee.getEmpSn());
        jLabelUserName = new JLabel(LABEL_NAME + employee.getEmpName());
        jLabelBranchNum = new JLabel( LABEL_BRANCH + employee.getEmpBranch());
        jLabelUserType = new JLabel( LABEL_ROLE + employee.getEmpType());
        jButtonStorage = new JButton(BUTTON_STORAGE);
        jButtonSignOut = new JButton(BUTTON_SIGNOUT);
        jButtonChat = new JButton(BUTTON_CHAT);

        photoLabelJPG = new ImageIcon(getClass().getResource(NO_PHOTO));
        photoLabel = new JLabel(photoLabelJPG);
        photoLabel.setPreferredSize(new Dimension(200, 200)); // לשנות שיהיה ריסאייזבאל כמו שמציג את התמונות ברגע שבוחר אותם מהמחשב
        jPanelLeft.add(photoLabel);
    }

    protected void resizePhotoInMenu(){

        photoLabel.setVisible(false);

        ConvertThePhotoFormatFromTheDB();

        PutTheResizePhotoOnLabel();

        PlaceTheEmployeePhoto();

        jPanelLeft.add(photoLabel);
    }

    private void PutTheResizePhotoOnLabel() {

        ImageIcon MyImage = new ImageIcon(decoded);
        Image img = MyImage.getImage();
        Image newImage = img.getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(),Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);
        photoLabel = new JLabel(image);
    }

    private void ConvertThePhotoFormatFromTheDB() {

        // המרה של מה שהגיע בתוך אמפלויי.פוטו שהוא איזה שהוא המרה של מה שאוחסן בבסיס נתונים כבלוב וכעת יש להמירו לפני הצגה על הלייבל
        String encodedPhotos;
        Base64 codec = new Base64();
        encodedPhotos = employee.getEmpPhoto();
        decoded = codec.decode(encodedPhotos);
    }

    private void PlaceTheEmployeePhoto() {

        springLayout.putConstraint(SpringLayout.WEST, photoLabel, 30, SpringLayout.WEST, jPanelLeft);
        springLayout.putConstraint(SpringLayout.NORTH, photoLabel, 50, SpringLayout.NORTH, jPanelLeft);
    }

    protected void InitializeActions() {

        jButtonStorage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Storage storage = new Storage(employee.getEmpBranch());
                setEnabled(false); // Disable edit this window when you open the next window

                try {
                    storage.DrawStorage(getJFrame());
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                storage.setVisible(true);
                storage.setLocationRelativeTo(null);
            }
        });

        jButtonSignOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                askUserBeforeCloseWindow(getJFrame());
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                askUserBeforeCloseWindow(getJFrame());
            }
        });
    }

    protected void DrawMainMenu() {

        GUISettingForJFrame();
        GUISettingForJPanel();
    }

    private void GUISettingForJPanel() {

        GUIDefineLayoutAndAddComponentsOnJPanel();
        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

        GUIPlaceLabels();
        GUIPlaceLogInButton();
    }

    private void GUIPlaceLogInButton() {

        springLayout.putConstraint(SpringLayout.WEST, jButtonStorage, 10, SpringLayout.WEST, jPanelLeft);
        springLayout.putConstraint(SpringLayout.NORTH, jButtonStorage, 25, SpringLayout.NORTH, jLabelUserType);
        springLayout.putConstraint(SpringLayout.WEST, jButtonChat, 10, SpringLayout.WEST, jPanelLeft);
        springLayout.putConstraint(SpringLayout.NORTH, jButtonChat, 25, SpringLayout.NORTH, jButtonStorage);
    }

    private void GUIPlaceLabels() {

        // מיקום התמונה
        PlaceTheEmployeePhoto();

        springLayout.putConstraint(SpringLayout.WEST, jLabelEmpDetails, 10, SpringLayout.WEST, jPanelLeft);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelEmpDetails, 15, SpringLayout.NORTH, jPanelLeft);
        springLayout.putConstraint(SpringLayout.WEST, jLabelUserNum, 10, SpringLayout.WEST, jPanelLeft);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelUserNum, 25, SpringLayout.NORTH, jLabelEmpDetails);
        springLayout.putConstraint(SpringLayout.WEST, jLabelUserName, 10, SpringLayout.WEST, jPanelLeft);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelUserName, 15, SpringLayout.NORTH, jLabelUserNum);
        springLayout.putConstraint(SpringLayout.WEST, jLabelBranchNum, 10, SpringLayout.WEST, jPanelLeft);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelBranchNum, 15, SpringLayout.NORTH, jLabelUserName);
        springLayout.putConstraint(SpringLayout.WEST, jLabelUserType, 10, SpringLayout.WEST, jPanelLeft);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelUserType, 15, SpringLayout.NORTH, jLabelBranchNum);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelLeft.setLayout(springLayout);

        jPanelLeft.add(jLabelUserNum);
        jPanelLeft.add(jLabelEmpDetails);
        jPanelLeft.add(jLabelUserName);
        jPanelLeft.add(jLabelBranchNum);
        jPanelLeft.add(jLabelUserType);

        jPanelLeft.add(jButtonSignOut);
        jPanelLeft.add(jButtonStorage);
        jPanelLeft.add(jButtonChat);

        jPanelMain.add(jPanelLeft);
    }

    private void GUISettingForJFrame() {

        setTitle(FRAME_NAME);
        setSize(FRAMEֹֹ_WIDTH_ֹSIZE, FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setResizable(false);

        setContentPane(jPanelMain);
    }

    private  void askUserBeforeCloseWindow(JFrame currentFrame){

        if (JOptionPane.showConfirmDialog(currentFrame,
                JOPTIONPANE_MESSAGE, JOPTIONPANE_TITLE,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){

            ChangeEmployeeStatusToOUT();
            currentFrame.setVisible(false);
            GUICreateWelcomeWindow();
        }

        else{
            currentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }

    private void ChangeEmployeeStatusToOUT() {

        try {

            PrepareAndSendJsonLogOutDataToServer();
        }

        catch (Exception ex){

        }
    }

    private void PrepareAndSendJsonLogOutDataToServer() throws IOException {

        jsonObject = new JSONObject();
        jsonObject.put("GuiName", "Log out");
        jsonObject.put("employee id", employee.getEmpId());
        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    private static void GUICreateWelcomeWindow() {

        Welcome welcomeWindow = new Welcome();
        welcomeWindow.DrawWelcome();
        welcomeWindow.setVisible(true);
        welcomeWindow.setLocationRelativeTo(null);
    }

    public JFrame getJFrame(){

        return this;
    }

    public JPanel getjPanelMain() {

        return jPanelMain;
    }

    public Employee getEmployee() {

        return employee;
    }

    public void setEmployee(Employee employee) {

        this.employee = employee;
    }

    public static void main(String[] args) {

        Employee employee = new Employee("0123456789", "sdfsd", "sdfsdf", "sdfsdf",
                "sdfsdf", "sdfsdf", "sdfsf", "sdfsdf");
        MainMenuSeller mainMenu = new MainMenuSeller(employee);
        mainMenu.DrawMainMenu();
        mainMenu.InitializeActions();
        mainMenu.setVisible(true);
        mainMenu.setLocationRelativeTo(null);
    }
}
