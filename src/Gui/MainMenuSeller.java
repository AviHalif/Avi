package Gui;

import Classes.Employee;
import Client.Client;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;

public class MainMenuSeller extends JFrame {


    private Client client;
    private byte[] decoded;
    private Employee employee;
    private Dimension screenSize;
    private JSONObject jsonObject;
    private ImageIcon castroLogoJPG;
    private JButton jButtonStorage, jButtonSignOut;
    private JPanel jPanelMain, jPanelMid, jPanelUp;
    private SpringLayout springLayout,springLayoutUpPanel, springLayoutPanels;
    private ImageIcon photoLabelJPG, label_name, label_sn, label_type, label_branch, storageLogoJPG, signOutLogoJPG, backPhoto, backLeftPhoto;
    public static final int BUTTON_WIDTH = 403, BUTTON_HEIGHT = 80, LEFT_PANELֹֹ_WIDTH_ֹSIZE = 740, LEFT_PANELֹֹ_HEIGHT_ֹSIZE = 800, UP_PANELֹֹ_HEIGHT_ֹSIZE = 195;
    private JLabel photoLabel, jLabelUserNum, jLabelUserName, jLabelBranchName, jLabelUserType, castroLogoLabel, jLabeName, jLabelSn, jLabelType, jLabelBranch,
                   jLabelBackPhoto, jLabelBackLeftPhoto;
    public static final String LOG_OUT_BUTTON = "/src/images/LOGOUT.png", BACK_PHOTO = "/src/images/back_photo.png", BACK_LEFT_PHOTO = "/src/images/back_photo_left.png",
                               FRAME_NAME = "CASTRO - MAIN MENU", LABEL_SN = "/src/images/employee_sn.png", LABEL_NAME = "/src/images/employee_name.png",
                               LABEL_TYPE = "/src/images/employee_type.png", LABEL_BRANCH = "/src/images/employee_branch.png", BUTTON_STORAGE = "/src/images/view_branch_storage.png",
                               CASTRO_ICON = "/src/images/icon.png", CASTRO_LOGO = "/src/images/title_castro.png", JOPTIONPANE_MESSAGE = "Are you sure you want to sign out?",
                               JOPTIONPANE_TITLE = "CASTRO - SIGN OUT", NO_PHOTO = "/src/images/No_Image_Available.png";



    public MainMenuSeller(Employee employee) {

        client = new Client();

        SetObjectsComponents(employee);
        SetGUIComponents();
    }

    private void SetObjectsComponents(Employee employee) {

        this.employee = new Employee(employee);
    }

    private void SetGUIComponents() {

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        jPanelMain = new JPanel();
        jPanelMain.setBackground(Color.white);
        jPanelMid = new JPanel();
        jPanelMid.setBackground(Color.black);
        jPanelUp = new JPanel();
        jPanelUp.setBackground(Color.lightGray);

        springLayout = new SpringLayout();
        springLayoutPanels = new SpringLayout();
        springLayoutUpPanel = new SpringLayout();

        jPanelMid.setPreferredSize(new Dimension(LEFT_PANELֹֹ_WIDTH_ֹSIZE, LEFT_PANELֹֹ_HEIGHT_ֹSIZE));
        jPanelUp.setPreferredSize(new Dimension(screenSize.width, UP_PANELֹֹ_HEIGHT_ֹSIZE));

        backLeftPhoto = new ImageIcon(getClass().getResource(BACK_LEFT_PHOTO));
        jLabelBackLeftPhoto = new JLabel(backLeftPhoto);
        jPanelMain.add(jLabelBackLeftPhoto);

        backPhoto = new ImageIcon(getClass().getResource(BACK_PHOTO));
        jLabelBackPhoto = new JLabel(backPhoto);
        jPanelMain.add(jLabelBackPhoto);

        castroLogoJPG = new ImageIcon(getClass().getResource(CASTRO_LOGO));
        castroLogoLabel = new JLabel(castroLogoJPG);
        jPanelUp.add(castroLogoLabel);

        SetLabelsOnLeftPanel();

        SetButtonsOnLeftPanel();

        photoLabelJPG = new ImageIcon(getClass().getResource(NO_PHOTO));
        photoLabel = new JLabel(photoLabelJPG);
        photoLabel.setPreferredSize(new Dimension(250, 300));
        jPanelMid.add(photoLabel);
    }

    private void SetButtonsOnLeftPanel() {

        storageLogoJPG = new ImageIcon(getClass().getResource(BUTTON_STORAGE));
        jButtonStorage = new JButton(storageLogoJPG);
        jButtonStorage.setBorderPainted(true);
        jButtonStorage.setBackground(Color.BLACK);
        jButtonStorage.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        jButtonStorage.setBorder(new LineBorder(Color.red));

        signOutLogoJPG = new ImageIcon(getClass().getResource(LOG_OUT_BUTTON));
        jButtonSignOut = new JButton(signOutLogoJPG);
        jButtonSignOut.setBorderPainted(false);
        jButtonSignOut.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.black, Color.black));
    }

    private void SetLabelsOnLeftPanel() {

        Font font = new Font("Urban Sketch", Font.PLAIN, 30);

        label_name = new ImageIcon(getClass().getResource(LABEL_NAME));
        jLabeName = new JLabel(label_name);
        jLabelUserName = new JLabel( employee.getEmpName());
        jLabelUserName.setFont(font);
        jLabelUserName.setForeground (Color.white);

        label_sn = new ImageIcon(getClass().getResource(LABEL_SN));
        jLabelSn = new JLabel(label_sn);
        jLabelUserNum = new JLabel( employee.getEmpSn());
        jLabelUserNum.setFont(font);
        jLabelUserNum.setForeground (Color.white);

        label_type = new ImageIcon(getClass().getResource(LABEL_TYPE));
        jLabelType = new JLabel(label_type);
        jLabelUserType = new JLabel( employee.getEmpType());
        jLabelUserType.setFont(font);
        jLabelUserType.setForeground (Color.red);

        label_branch = new ImageIcon(getClass().getResource(LABEL_BRANCH));
        jLabelBranch = new JLabel(label_branch);
        jLabelBranchName = new JLabel( employee.getEmpBranch());
        jLabelBranchName.setFont(font);
        jLabelBranchName.setForeground (Color.white);
    }

    protected void resizePhotoInMenu(){

        photoLabel.setVisible(false);

        ConvertThePhotoFormatFromTheDB();

        PutTheResizePhotoOnLabel();

        PlaceTheEmployeePhoto();

        jPanelMid.add(photoLabel);
    }

    private void PutTheResizePhotoOnLabel() {

        ImageIcon MyImage = new ImageIcon(decoded);
        Image img = MyImage.getImage();
        Image newImage = img.getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(),Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);
        photoLabel = new JLabel(image);
    }

    private void ConvertThePhotoFormatFromTheDB() {

        String encodedPhotos;
        Base64 codec = new Base64();
        encodedPhotos = employee.getEmpPhoto();
        decoded = codec.decode(encodedPhotos);
    }

    private void PlaceTheEmployeePhoto() {

        springLayout.putConstraint(SpringLayout.WEST, photoLabel, 30, SpringLayout.WEST, jPanelMid);
        springLayout.putConstraint(SpringLayout.NORTH, photoLabel, 50, SpringLayout.NORTH, jPanelMid);
    }

    protected void InitializeActions() {

        jButtonStorage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Storage storage = new Storage(employee.getEmpBranch());
                setEnabled(false); // Disable edit this window when you open the next window
                storage.setUndecorated(true);

                try {
                    storage.DrawStorage(getJFrame());
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                storage.setVisible(true);
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

        springLayoutPanels.putConstraint(SpringLayout.WEST, jLabelBackLeftPhoto, -5, SpringLayout.WEST, jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.NORTH, jLabelBackLeftPhoto, 200, SpringLayout.NORTH, jPanelMain);

        springLayoutUpPanel.putConstraint(SpringLayout.WEST, castroLogoLabel, -120, SpringLayout.WEST, jPanelUp);
        springLayoutUpPanel.putConstraint(SpringLayout.NORTH, castroLogoLabel, -10, SpringLayout.NORTH, jPanelUp);

        springLayoutPanels.putConstraint(SpringLayout.NORTH, jPanelUp, 0, SpringLayout.NORTH, jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.WEST, jPanelUp, 0, SpringLayout.WEST, jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.WEST, jPanelMid, 370, SpringLayout.WEST, jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.NORTH, jPanelMid, 200, SpringLayout.NORTH, jPanelMain);

        springLayoutPanels.putConstraint(SpringLayout.EAST, jButtonSignOut, 0, SpringLayout.EAST, jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.SOUTH, jButtonSignOut, 0, SpringLayout.SOUTH, jPanelMain);

        springLayoutPanels.putConstraint(SpringLayout.EAST, jLabelBackPhoto, 0, SpringLayout.EAST, jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.NORTH, jLabelBackPhoto, 450, SpringLayout.NORTH, jPanelMain);

    }

    private void GUIPlaceComponentsOnJPanel() {

        GUIPlaceLabels();
        GUIPlaceLogInButton();
    }

    private void GUIPlaceLogInButton() {

        springLayout.putConstraint(SpringLayout.WEST, jButtonStorage, 30, SpringLayout.EAST, photoLabel);
        springLayout.putConstraint(SpringLayout.NORTH, jButtonStorage, 50, SpringLayout.NORTH, jPanelMid);
    }

    private void GUIPlaceLabels() {

        PlaceTheEmployeePhoto();

        springLayout.putConstraint(SpringLayout.WEST, jLabeName, 10, SpringLayout.WEST, jPanelMid);
        springLayout.putConstraint(SpringLayout.NORTH, jLabeName, 25, SpringLayout.SOUTH, photoLabel);
        springLayout.putConstraint(SpringLayout.WEST, jLabelUserName, 5, SpringLayout.EAST, jLabeName);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelUserName, 55, SpringLayout.SOUTH, photoLabel);

        springLayout.putConstraint(SpringLayout.WEST, jLabelSn, 22, SpringLayout.WEST, jPanelMid);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelSn, -20, SpringLayout.SOUTH, jLabeName);
        springLayout.putConstraint(SpringLayout.WEST, jLabelUserNum, 5, SpringLayout.EAST, jLabelSn);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelUserNum, 28, SpringLayout.SOUTH, jLabelUserName);


        springLayout.putConstraint(SpringLayout.WEST, jLabelType, 20, SpringLayout.WEST, jPanelMid);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelType, -20, SpringLayout.SOUTH, jLabelSn);
        springLayout.putConstraint(SpringLayout.WEST, jLabelUserType, 5, SpringLayout.EAST, jLabelType);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelUserType, 28, SpringLayout.SOUTH, jLabelUserNum);


        springLayout.putConstraint(SpringLayout.WEST, jLabelBranch, 10, SpringLayout.WEST, jPanelMid);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelBranch, -20, SpringLayout.SOUTH, jLabelType);
        springLayout.putConstraint(SpringLayout.WEST, jLabelBranchName, 5, SpringLayout.EAST, jLabelBranch);
        springLayout.putConstraint(SpringLayout.NORTH, jLabelBranchName, 28, SpringLayout.SOUTH, jLabelUserType);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelMid.setLayout(springLayout);
        jPanelMain.setLayout(springLayoutPanels);
        jPanelUp.setLayout(springLayoutUpPanel);

        jPanelMid.add(jLabeName);
        jPanelMid.add(jLabelUserName);
        jPanelMid.add(jLabelUserName);

        jPanelMid.add(jLabelSn);
        jPanelMid.add(jLabelUserNum);

        jPanelMid.add(jLabelType);
        jPanelMid.add(jLabelUserType);

        jPanelMid.add(jLabelBranch);
        jPanelMid.add(jLabelBranchName);

        jPanelMid.add(jButtonStorage);

        jPanelMain.add(jButtonSignOut);

        jPanelMain.add(jPanelMid);
        jPanelMain.add(jPanelUp);
    }

    private void GUISettingForJFrame() {

        setTitle(FRAME_NAME);
        setIconImages(Collections.singletonList(Toolkit.getDefaultToolkit().getImage(getClass().getResource(CASTRO_ICON))));
        setSize(screenSize.width, screenSize.height);
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

    public JPanel getjPanelMid() {
        return jPanelMid;
    }

    public JLabel getjLabelBackPhoto() {
        return jLabelBackPhoto;
    }

    public SpringLayout getSpringLayoutPanels() {
        return springLayoutPanels;
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

    public JLabel getjLabelBackLeftPhoto() {
        return jLabelBackLeftPhoto;
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
