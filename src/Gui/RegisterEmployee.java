package Gui;

import Classes.Employee;
import Classes.PasswordUtils;
import Client.Client;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RegisterEmployee extends Register {

    private static Logger logger = Logger.getLogger(RegisterEmployee.class.getName());

    public static final String HELP_LABEL = "/src/images/ques_mark.png";
    public static final String NO_PHOTO = "/src/images/No_Image_Available.png";
    public static final String TITLE = "/src/images/employee_register.png";
    public static final String BLACK_PHOTO = "/src/images/black_back.png";
    public static final String LABEL_BRANCH = "/src/images/registry_branch.png";
    public static final String LABEL_BANK = "/src/images/registry_bank.png";
    public static final String LABEL_PASS = "/src/images/registry_pass.png";
    public static final String CHANGE_PHOTO = "/src/images/change_photo.png";
    public static final String REMOVE_PHOTO = "/src/images/remove_photo.png";
    public static final String VERIFY_REGISTER = "/src/images/verify_photo.png";
    public static final int BUTTON_WIDTH2 = 350;
    public static final int BUTTON_HEIGHT2 = 50;
    public static final int BUTTON_WIDTH3 = 300;
    public static final int BUTTON_HEIGHT3 = 50;

    private Employee employee;
    private String empBranchName, propFileName;
    private int passLength;
    private char[] pass;

    private Properties properties;
    private InputStream inStream;
    private ImageIcon label_branch, label_pass, label_bank;
    private JLabel jLabelbranch, jLabelpass, jLabelbank;

    private Client client;

    private JSONObject jsonObject;
    private ObjectMapper objectMapper;

    private JLabel helpLabel, photoLabel, titleLabel, blackgroundPhotoLabel;
    private ImageIcon titlePhotoJPG, helpLabelJPG, photoLabelJPG, blackgroundPhotoJPG, changeLogoJPG, removeLogoJPG, verifyLogoJPG;
    private JTextField branchName_text, bankaccount_text;
    private JPasswordField password_text, password_authentication_text;
    private JButton verifyAndRegister, browse_button, remove_photo_button;


    public RegisterEmployee(JFrame oneBackFrame, JFrame twoBackFrame, String empBranchName) throws IOException {

        super(oneBackFrame,twoBackFrame);
        
        this.empBranchName = empBranchName;

        DefineLogAndConfig();
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

        employee = new Employee();

        employee.setEmpBranch("Tel Aviv");
        employee.setEmpType("Seller");
    }

    private void SetGUIComponents() {

        GUISettingForJFrame();
    }


    private void GUISetComponentsOnJPanelDown() {

        GUISetLabelsComponents();

        GUISetButtonsComponents();

        GUISetTextFieldsAndComboComponents();

        GUISetToolKitsComponents();
    }

    private void GUISetLabelsComponents() {

        label_bank = new ImageIcon(getClass().getResource(LABEL_BANK));
        jLabelbank = new JLabel(label_bank);

        label_pass = new ImageIcon(getClass().getResource(LABEL_PASS));
        jLabelpass = new JLabel(label_pass);

        label_branch = new ImageIcon(getClass().getResource(LABEL_BRANCH));
        jLabelbranch = new JLabel(label_branch);

        getjPanelTopFields().add(jLabelbank);
        getjPanelTopFields().add(jLabelpass);
        getjPanelTopFields().add(jLabelbranch);
    }

    private void GUISetButtonsComponents() {

        changeLogoJPG = new ImageIcon(getClass().getResource(CHANGE_PHOTO));
        browse_button = new JButton(changeLogoJPG);
        browse_button.setBorderPainted(true);
        browse_button.setBackground(Color.white);
        browse_button.setPreferredSize(new Dimension(BUTTON_WIDTH3, BUTTON_HEIGHT3));
        browse_button.setBorder(new LineBorder(Color.red));
        browse_button.setEnabled(false);

        removeLogoJPG = new ImageIcon(getClass().getResource(REMOVE_PHOTO));
        remove_photo_button = new JButton(removeLogoJPG);
        remove_photo_button.setBorderPainted(true);
        remove_photo_button.setBackground(Color.white);
        remove_photo_button.setPreferredSize(new Dimension(BUTTON_WIDTH3, BUTTON_HEIGHT3));
        remove_photo_button.setBorder(new LineBorder(Color.red));
        remove_photo_button.setEnabled(false);

        verifyLogoJPG = new ImageIcon(getClass().getResource(VERIFY_REGISTER));
        verifyAndRegister = new JButton(verifyLogoJPG);
        verifyAndRegister.setBorderPainted(true);
        verifyAndRegister.setBackground(Color.white);
        verifyAndRegister.setPreferredSize(new Dimension(BUTTON_WIDTH2, BUTTON_HEIGHT2));
        verifyAndRegister.setBorder(new LineBorder(Color.red));
        verifyAndRegister.setEnabled(false);
        verifyAndRegister.setVisible(false);

        getjPanelTopFields().add(verifyAndRegister);
        getjPanelTopFields().add(browse_button);
        getjPanelTopFields().add(remove_photo_button);
    }

    private void GUISetTextFieldsAndComboComponents() {

        bankaccount_text = new JTextField();
        bankaccount_text.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        bankaccount_text.setHorizontalAlignment(JTextField.CENTER);
        bankaccount_text.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        bankaccount_text.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        bankaccount_text.setForeground (Color.black);
        bankaccount_text.setOpaque(false);

        bankaccount_text.setEnabled(false);


        password_text = new JPasswordField();
        password_text.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        password_text.setHorizontalAlignment(JTextField.CENTER);
        password_text.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        password_text.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        password_text.setForeground (Color.black);
        password_text.setOpaque(false);

        password_text.setEnabled(false);


        password_authentication_text = new JPasswordField();
        password_authentication_text.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        password_authentication_text.setHorizontalAlignment(JTextField.CENTER);
        password_authentication_text.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        password_authentication_text.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        password_authentication_text.setForeground (Color.black);
        password_authentication_text.setOpaque(false);

        password_authentication_text.setVisible(false);


        branchName_text = new JTextField(empBranchName);
        branchName_text.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        branchName_text.setHorizontalAlignment(JTextField.CENTER);
        branchName_text.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        branchName_text.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        branchName_text.setForeground (Color.black);
        branchName_text.setOpaque(false);

        branchName_text.setEditable(false);


        getType_box().setEnabled(false);

        getComboBoxModel().addElement("Seller");
        getComboBoxModel().addElement("Cashier");
        getComboBoxModel().addElement("Shift Manager");

        getjPanelTopFields().add(bankaccount_text);
        getjPanelTopFields().add(branchName_text);
        getjPanelTopFields().add(password_text);
        getjPanelTopFields().add(password_authentication_text);
    }

    private void GUISetToolKitsComponents() {

        helpLabelJPG = new ImageIcon(getClass().getResource(HELP_LABEL));
        helpLabel = new JLabel(helpLabelJPG);
        helpLabel.setPreferredSize(new Dimension(120, 120));
        getjPanelTopFields().add(helpLabel);
        helpLabel.setToolTipText("Please insert at least one char A-Z, a-z and 0-9 !!!");

        photoLabelJPG = new ImageIcon(getClass().getResource(NO_PHOTO));
        photoLabel = new JLabel(photoLabelJPG);
        photoLabel.setPreferredSize(new Dimension(200, 250)); // לשנות שיהיה ריסאייזבאל כמו שמציג את התמונות ברגע שבוחר אותם מהמחשב
        employee.setEmpPhoto("C:/Users/AviHalif/IdeaProjects/Avi/src/images/No_Image_Available.png"); // כאן חייב להזין את הנתיב המלא של בחירת התמונה
    }

    private void GUISettingForJFrame() {

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    protected void DrawRegister() {

        titlePhotoJPG = new ImageIcon(getClass().getResource(TITLE));
        titleLabel = new JLabel(titlePhotoJPG);
        getjPanelMain().add(titleLabel);

        blackgroundPhotoJPG = new ImageIcon(getClass().getResource(BLACK_PHOTO));
        blackgroundPhotoLabel = new JLabel(blackgroundPhotoJPG);

        super.DrawRegister();

        getSpringLayoutPanels().putConstraint(SpringLayout.WEST,titleLabel,60,SpringLayout.WEST,getjPanelMain());
        getSpringLayoutPanels().putConstraint(SpringLayout.NORTH,titleLabel,5,SpringLayout.NORTH,getjPanelMain());

        getSpringLayoutFields().putConstraint(SpringLayout.WEST,blackgroundPhotoLabel,5,SpringLayout.EAST,getFullname_text());
        getSpringLayoutFields().putConstraint(SpringLayout.SOUTH,blackgroundPhotoLabel,230,SpringLayout.SOUTH,getjPanelTopFields());

        SetObjectsComponents();

        SetGUIComponents();

        GUISetComponentsOnJPanelDown();

        GUIPlaceComponentsOnDownPanel();

        getjPanelTopFields().add(blackgroundPhotoLabel);

        getjPanelTopFields().add(photoLabel);
    }

    private void GUIPlaceComponentsOnDownPanel() {

        getSpringLayoutFields().putConstraint(SpringLayout.WEST,jLabelbank,230,SpringLayout.WEST,getjPanelTopFields());
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH,jLabelbank,100,SpringLayout.NORTH,getjLabeltype());

        getSpringLayoutFields().putConstraint(SpringLayout.WEST, jLabelbranch, 850, SpringLayout.WEST, getjPanelMain());
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, jLabelbranch, 200, SpringLayout.NORTH, getjPanelMain());

        getSpringLayoutFields().putConstraint(SpringLayout.WEST, jLabelpass, 850, SpringLayout.WEST, getjPanelMain());
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, jLabelpass, 17, SpringLayout.SOUTH, jLabelbranch);

        getSpringLayoutFields().putConstraint(SpringLayout.WEST, bankaccount_text, -10, SpringLayout.EAST, jLabelbank);
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, bankaccount_text, 35, SpringLayout.SOUTH,getjLabeltype());

        getSpringLayoutFields().putConstraint(SpringLayout.WEST, branchName_text, 15, SpringLayout.EAST, jLabelbranch);
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, branchName_text, 215, SpringLayout.SOUTH, getjPanelMain());

        getSpringLayoutFields().putConstraint(SpringLayout.WEST, password_text, 5, SpringLayout.EAST, jLabelpass);
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, password_text, 48, SpringLayout.SOUTH, branchName_text);

        getSpringLayoutFields().putConstraint(SpringLayout.WEST, password_authentication_text, 5, SpringLayout.EAST, jLabelpass);
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, password_authentication_text, 48, SpringLayout.SOUTH, password_text);

        getSpringLayoutFields().putConstraint(SpringLayout.EAST, helpLabel, 10, SpringLayout.WEST, password_text);
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, helpLabel, 110, SpringLayout.SOUTH, branchName_text);

        getSpringLayoutFields().putConstraint(SpringLayout.WEST, photoLabel, 5, SpringLayout.WEST, getjPanelTopFields());
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, photoLabel, 0, SpringLayout.SOUTH, getCheckButton());

        getSpringLayoutFields().putConstraint(SpringLayout.EAST, verifyAndRegister, 25, SpringLayout.EAST, blackgroundPhotoLabel);
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, verifyAndRegister, 480, SpringLayout.NORTH, getjPanelTopFields());

        getSpringLayoutFields().putConstraint(SpringLayout.EAST, browse_button, 350, SpringLayout.EAST, blackgroundPhotoLabel);
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, browse_button, 480, SpringLayout.NORTH, getjPanelTopFields());

        getSpringLayoutFields().putConstraint(SpringLayout.EAST, remove_photo_button, 350, SpringLayout.EAST, blackgroundPhotoLabel);
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, remove_photo_button, 550, SpringLayout.NORTH, getjPanelTopFields());
    }

    protected void InitializeActions() {

        browse_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.IMAGE", "jpg","gif","png");
                fileChooser.addChoosableFileFilter(filter);

                int result = fileChooser.showSaveDialog(null);

                if(result == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = fileChooser.getSelectedFile();
                    String path = selectedFile.getAbsolutePath();

                    if(MaxSizeImage(path)) { // אם עברנו את הגוsל המירבי של תמונה

                        photoLabel.setVisible(false);

                        ResizeThePhoto(path);

                        employee.setEmpPhoto(path);

                    }else{
                        JOptionPane.showMessageDialog(null, "Image size is too large!!! Limit to 32KB - Select other Image");
                    }
                }
                else if(result == JFileChooser.CANCEL_OPTION){
                    System.out.println("No Data");
                }

                if(!employee.getEmpPhoto().equals("C:/Users/AviHalif/IdeaProjects/Avi/src/images/No_Image_Available.png"))
                    remove_photo_button.setEnabled(true);
            }
        });

        remove_photo_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                photoLabel.setVisible(false);

                photoLabelJPG = new ImageIcon(getClass().getResource(NO_PHOTO));
                photoLabel = new JLabel(photoLabelJPG);
                photoLabel.setPreferredSize(new Dimension(200, 250)); // לשנות שיהיה ריסאייזבאל כמו שמציג את התמונות ברגע שבוחר אותם מהמחשב

                employee.setEmpPhoto("C:/Users/AviHalif/IdeaProjects/Avi/src/images/No_Image_Available.png"); // כאן חייב להזין את הנתיב המלא של בחירת התמונה

                getSpringLayoutFields().putConstraint(SpringLayout.WEST, photoLabel, 5, SpringLayout.WEST, getjPanelTopFields());
                getSpringLayoutFields().putConstraint(SpringLayout.NORTH, photoLabel, 0, SpringLayout.SOUTH, getCheckButton());

                photoLabel.setVisible(true);
                remove_photo_button.setEnabled(false);
            }

        });

        getType_box().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                employee.setEmpType((String) getType_box().getSelectedItem());
            }
        });

        password_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                passLength = password_text.getPassword().length;
                pass = password_text.getPassword(); // שומר את הסיסמא המקורית כצארים

                // Generate Salt. The generated value stored in DB.
                String salt = PasswordUtils.getSalt(30);

                employee.setEmpPassSalt(salt);

                // Protect user's password. The generated value stored in DB.
                employee.setEmpPass(PasswordUtils.generateSecurePassword(new String(password_text.getPassword()), salt));
            }
        });

        verifyAndRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    if (IsAuthenticationPassword()) { // בדוק האם הסיסמא שהמשתמש הזין שוב תקינה

                        EnableAllFields();

                        if(!CheckEmpFields(employee,passLength,pass)){
                            return;
                        }

                        client = new Client();

                        PrepareAndSendJsonDataNewEmployeeToServer(); // הכנסת העובד החדש למערכת

                        String response = GetEmployeeRegisterResponseFromServer();

                        JSONObject jsonObjectResponse;
                        JSONParser jsonParser = new JSONParser();
                        jsonObjectResponse = (JSONObject) jsonParser.parse(response);
                        response = (String) jsonObjectResponse.get("Failed");

                        if (!response.equals("")) {

                            JOptionPane.showMessageDialog(null, response);
                            client.getSslSocket().close();

                        } else {

                            response = (String) jsonObjectResponse.get("emp sn");

                            JOptionPane.showMessageDialog(null, "Registered successfully, you're S/N is: " + response);

                            logger.info("Registered successfully - Employee ID : " + employee.getEmpId());
                            client.getSslSocket().close();
                            DrawUpdateEmployeeList();
                        }

                    } else { // אם הסיסמא שהמשתמש הזין שוב לא אותה סיסמא

                        JOptionPane.showMessageDialog(null, "No matching between the passwords! Enter your password again! ");
                        ChangeGUIComponents();
                    }
                } catch (Exception ex) {
                    System.out.print(ex);
                }
            }
        });

        getRegisterButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    InsertAllFieldsToEmployeeObject();

                    if (!CheckEmpFields(employee, passLength, pass)) {
                        return;
                    }
                    JOptionPane.showMessageDialog(null, "PASSWORD VERIFICATION : Please enter your password again");

                    password_authentication_text.setVisible(true);
                    getRegisterButton().setVisible(false);
                    verifyAndRegister.setVisible(false);
                } catch (Exception ex) {
                }
            }
        });

        getCheckButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    employee.setEmpId(getId_text().getText());

                    if(!CheckId(employee.getEmpId())) { // בדיקת תקינות קלט ת.ז
                        return;
                    }

                    client = new Client();

                    PrepareAndSendJsonDataIdForCheckingToServer(); // בדיקה האם הת.ז נמצא כבר במערכת

                    String response = GetResponseFromServer();


                    if (!(response.equals(""))) { // אם התז כבר נמצא במערכת תציג הודעה ותאפס את השדה טקסט
                        JOptionPane.showMessageDialog(null, response);
                        getCheckButton().setEnabled(false);
                        getId_text().setText("");


                    } else { // אם הת. לא נמצאת בבסיס נתונים עדיין אז תפתח את הנתונים להזנה ותנעל את הת.ז ללא עריכה

                        ChangeGUIComponentsForInsertData();
                    }

                    client.getSslSocket().close();

                } catch (Exception ex) {
                    System.out.print(ex);
                }
            }
        });

        bankaccount_text.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {

                    bankaccount_text.setBorder(BorderFactory.createLineBorder(Color.red));
                }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                employee.setEmpBank(bankaccount_text.getText());
                bankaccount_text.setBorder(BorderFactory.createLineBorder(Color.black));

            }


        });

    }

    private void ChangeGUIComponentsForInsertData() {

        getId_text().setEnabled(false);

        browse_button.setEnabled(true);
        getPhone_text().setEnabled(true);
        getFullname_text().setEnabled(true);
        getType_box().setEnabled(true);
        bankaccount_text.setEnabled(true);
        password_text.setEnabled(true);

        getCheckButton().setVisible(false); // תחליף בין הכפתורים ע"י העלמתם
        getRegisterButton().setVisible(true);
    }

    private void InsertAllFieldsToEmployeeObject() {

        employee.setEmpTel(getPhone());
        employee.setEmpName(getFullName());
        employee.setEmpId(getId());
        employee.setEmpBank(bankaccount_text.getText());
        employee.setEmpType((String) getType_box().getSelectedItem());
        employee.setEmpBranch(empBranchName);
    }

    private void ChangeGUIComponents() {

        password_text.setText("");
        password_authentication_text.setText("");
        password_authentication_text.setVisible(false);
        verifyAndRegister.setVisible(false);
        getRegisterButton().setVisible(true);
    }

    private void DrawUpdateEmployeeList() throws IOException, ParseException {

        getJFrame().setVisible(false);

        getjFramePrev().setVisible(false);

        EmployeeListManagement EmployeeListManagement = new EmployeeListManagement(getTwoBackFrame(), empBranchName);
        EmployeeListManagement.DrawEmployee();
        EmployeeListManagement.InitializeActions();
        EmployeeListManagement.setVisible(true);
        EmployeeListManagement.setLocationRelativeTo(null);
    }

    private void EnableAllFields() {

        employee.setEmpTel(getPhone());
        employee.setEmpName(getFullName());
        employee.setEmpId(getId());
        employee.setEmpBank(bankaccount_text.getText());
        employee.setEmpType((String) getType_box().getSelectedItem());
        employee.setEmpBranch(empBranchName);
    }

    private void ResizeThePhoto(String path) {

        // resize the photo
        ImageIcon MyImage = new ImageIcon(path);
        Image img = MyImage.getImage();
        Image newImage = img.getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);

        photoLabel = new JLabel(image);
        getSpringLayoutFields().putConstraint(SpringLayout.WEST, photoLabel, 5, SpringLayout.WEST, getjPanelTopFields());
        getSpringLayoutFields().putConstraint(SpringLayout.NORTH, photoLabel, 0, SpringLayout.SOUTH, getCheckButton());
        getjPanelTopFields().add(photoLabel);
    }

    private boolean MaxSizeImage(String path) {

            boolean temp = false;
            File file = new File(path);
            long length = file.length();

            if (length < 32000)
                temp = true;

            return temp;
    }

    private Boolean IsAuthenticationPassword() {

        String repeat_input_pass = new String(password_text.getPassword());

        if((repeat_input_pass).equals(new String(password_authentication_text.getPassword())))
            return true;
        return false;
    }

    private String GetEmployeeRegisterResponseFromServer() throws IOException {

        String line = client.getInputStream().readUTF();

        return line;
    }

    private void PrepareAndSendJsonDataNewEmployeeToServer() throws IOException {

        jsonObject = new JSONObject();
        objectMapper = new ObjectMapper();
        String empStr = objectMapper.writeValueAsString(employee);
        jsonObject.put("GuiName", "Register");
        jsonObject.put("employee", empStr);

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    private String GetResponseFromServer() throws IOException, ParseException {

        String line = client.getInputStream().readUTF();
        JSONObject jsonObjectResponse;
        JSONParser jsonParser = new JSONParser();
        jsonObjectResponse = (JSONObject) jsonParser.parse(line);
        String response = (String) jsonObjectResponse.get("Failed");

        return response;
    }

    private void PrepareAndSendJsonDataIdForCheckingToServer() throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "Check employee already exists");
        jsonObject.put("employeeId", employee.getEmpId());

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
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

    private static boolean PassCheck(char[] pass){

        boolean smallLeter=false,capitalLeter=false,numbers=false;

        for(int i =0; i<pass.length;i++){

            if(!(pass[i] >='a' && pass[i]<= 'z')) {

                if(!(pass[i] >='A' && pass[i] <='Z')){

                    if(!(pass[i] >='0' && pass[i] <='9')){

                        return false;
                    }
                    else{
                        numbers = true;
                    }
                }
                else {
                    capitalLeter = true;
                }
            }
            else{
                smallLeter = true;
            }
        }
        if(smallLeter&&capitalLeter&&numbers)

            return true;
        else
            return false;
    }

    private static boolean CheckEmpFields(Employee employee,int passLength,char[] pass){

        if (!(employee.getEmpId().length() <= 9 && employee.getEmpId().length()>=1)) {

            JOptionPane.showMessageDialog(null, "ID: Your Id length is incorrect");
            return false;
        }

        if (!(employee.getEmpId().matches("[0-9]+"))) {

            JOptionPane.showMessageDialog(null, "ID: Please insert only digits");
            return false;
        }

        if (!(employee.getEmpName().length() <= 20 && employee.getEmpName().length() >= 5)) {

            JOptionPane.showMessageDialog(null, "Full Name: Legal length name  is between 5 to 20 letters");
            return false;
        }

        if (!(onlyLettersSpaces(employee.getEmpName()))) {

            JOptionPane.showMessageDialog(null, "Full Name: Please insert only Letters and Spaces");
            return false;
        }

        if (!(employee.getEmpTel().length() <= 10 && employee.getEmpTel().length() >= 9)) {

            JOptionPane.showMessageDialog(null, "Phone: Your phone number length is incorrect");
            return false;
        }

        if (!(employee.getEmpTel().matches("[0-9]+"))) {

            JOptionPane.showMessageDialog(null, "Phone: Please enter only digits");
            return false;
        }

        if (!(employee.getEmpBank().length() <= 10 && employee.getEmpBank().length() >=5)) {

            JOptionPane.showMessageDialog(null, "Bank Account: Your account number length is incorrect");
            return false;
        }

        if (!(employee.getEmpBank().matches("[0-9]+"))) {

            JOptionPane.showMessageDialog(null, "Bank Account: Please insert only digits");
            return false;
        }

        if (!(passLength >=8 && passLength <=12)) {

            JOptionPane.showMessageDialog(null, "Password: Your password length is incorrect");
            return false;
        }

        if (!(PassCheck(pass))) {

            JOptionPane.showMessageDialog(null, "Password: you're password is incorrect");
            return false;
        }

        return true;
    }

    public static void main(String[] args) throws IOException {

        RegisterEmployee employee = new RegisterEmployee(null, null,"Tel Aviv"); // שולח למסך הרישום את המסך הראשי של האדמין ואת המסך של הניהול עובדים
        employee.DrawRegister();
        employee.InitializeActions();
        employee.setVisible(true);
        employee.setLocationRelativeTo(null);
    }

}
