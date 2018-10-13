package Gui;

import Classes.Employee;
import Classes.PasswordUtils;
import Client.Client;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class EmployeeEditDetails extends JFrame {

    public static final String JOPTIONPANE_MESSAGE_UPDATE = "Are you sure you want to update employee ID number: ";
    public static final String JOPTIONPANE_MESSAGE_PASS = "Are you sure you want to change the password of employee ID: ";
    public static final String JOPTIONPANE_TITLE_UPDATE = "CASTRO - UPDATE DETAILS EMPLOYEE";
    public static final String JOPTIONPANE_MESSAGE_CLOSE = "Are you sure you want to close the window? Data will not be saved";
    public static final String JOPTIONPANE_TITLE_CLOSE = "CASTRO";
    public static final String NO_PHOTO = "/src/images/No_Image_Available.png";

    private String branchName;
    private Employee employee;
    private int passLength;
    private char[] pass;
    private boolean anyChangeInPass, isPhotoChanged;
    private byte[] decoded;

    private Client client;

    private JSONObject jsonObject;
    private ObjectMapper objectMapper;

    private JFrame oneBackFrame, twoBackFrame, thirdBackFrame;
    private JPanel jPanelMain;
    private JLabel label_message, idEmployee_label, snEmployee_label, nameEmployee_label, telEmployee_label, bankEmployee_label, branchEmployee_label, passEmployee_label, typeEmployee_label, photoLabel;
    private SpringLayout springLayout;
    private JTextField idEmployee_data, nameEmployee_data, telEmployee_data, bankEmployee_data, snEmployee_data, branchEmployee_data;
    private JPasswordField passEmployee_data;
    private JComboBox type_box;
    private DefaultComboBoxModel comboBoxModel;
    private JButton nameEdit_btton, telEdit_btton, typeEdit_btton, passEdit_btton, bankEdit_btton;
    private JButton ok_button, cancel_button, remove_photo_button, change_photo_button;
    private ImageIcon photoLabelJPG;

    public EmployeeEditDetails(String empId,String empSn,String empName,String empTel,
                               String empBank, String empBranch, String empType, String empPhoto,
                               JFrame oneBackFrame,JFrame twoBackFrame, JFrame thirdBackFrame, String branchName) {
//// הגיעה החלונית הקטנה להזנת ת.ז לעדכון, המסך הראשי של האדמין ואת המסך של הניהול עובדים

        SetGUIComponents(oneBackFrame, twoBackFrame, thirdBackFrame);

        SetObjectsComponents(branchName, empId, empSn, empName, empTel, empBank, empBranch, empType, empPhoto);

        client = new Client();
    }

    private void SetGUIComponents(JFrame oneBackFrame, JFrame twoBackFrame, JFrame thirdBackFrame) {

        this.oneBackFrame = oneBackFrame;
        this.twoBackFrame = twoBackFrame;
        this.thirdBackFrame = thirdBackFrame;
    }

    private void SetObjectsComponents(String branchName, String empId, String empSn, String empName, String empTel, String empBank, String empBranch, String empType, String empPhoto) {

        this.anyChangeInPass = false;
        this.isPhotoChanged = false;

        this.branchName = branchName;
        employee = new Employee(empId, empSn, empName, empTel, empBank, empBranch, empType, empPhoto);
    }

    protected void resizePhotoInMenu() {

        // resize the photo
        photoLabel.setVisible(false);

        // המרה של מה שהגיע בתוך אמפלויי.פוטו שהוא איזה שהוא המרה של מה שאוחסן בבסיס נתונים כבלוב וכעת יש להמירו לפני הצגה על הלייבל
        String encodedPhotos;
        Base64 codec = new Base64();
        encodedPhotos = employee.getEmpPhoto();
        decoded = codec.decode(encodedPhotos);


        ImageIcon MyImage = new ImageIcon(decoded);
        Image img = MyImage.getImage();
        Image newImage = img.getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(),Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);
        photoLabel = new JLabel(image);

        // מיקום התמונה
        springLayout.putConstraint(SpringLayout.WEST,photoLabel,100,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,photoLabel,500,SpringLayout.NORTH,jPanelMain);
        jPanelMain.add(photoLabel);

    }

    protected void InitializeActions() {

        change_photo_button.addActionListener(new ActionListener() {
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

                    if(MaxSizeImage(path)) { // אם עברנו את הגול המירבי של תמונה

                        photoLabel.setVisible(false);

                        ResizeThePhoto(path);

                        employee.setEmpPhoto(path);

                        isPhotoChanged = true;
                        ok_button.setEnabled(true);

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
                photoLabel.setPreferredSize(new Dimension(300, 300)); // לשנות שיהיה ריסאייזבאל כמו שמציג את התמונות ברגע שבוחר אותם מהמחשב
                jPanelMain.add(photoLabel);

                employee.setEmpPhoto("C:/Users/AviHalif/IdeaProjects/Avi/src/images/No_Image_Available.png"); // כאן חייב להזין את הנתיב המלא של בחירת התמונה
                ok_button.setEnabled(true);
                isPhotoChanged = true;

                springLayout.putConstraint(SpringLayout.WEST,photoLabel,100,SpringLayout.WEST,jPanelMain);
                springLayout.putConstraint(SpringLayout.NORTH,photoLabel,500,SpringLayout.NORTH,jPanelMain);

                photoLabel.setVisible(true);
                remove_photo_button.setEnabled(false);
            }

        });

        ok_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    if (!CheckEmpFields(employee)||((passEmployee_data.isEnabled())&&!(CheckThePass()))) {
                        return;
                    }

                    else if (JOptionPane.showConfirmDialog(getFrame(),
                            JOPTIONPANE_MESSAGE_UPDATE + employee.getEmpId() + " ?", JOPTIONPANE_TITLE_UPDATE,
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                        if(!passEdit_btton.isEnabled()) {
                            anyChangeInPass = true;
                            generateNewSafePassword();
                        }

                        PrepareAndSendJsonDataToServer();

                        String response = GetResponseFromServer();

                        if (!response.equals("")) {
                            JOptionPane.showMessageDialog(null, response);

                        } else {
                            JOptionPane.showMessageDialog(null, "ID number : " + employee.getEmpId() + "   update successfully");

                        }

                        client.getSslSocket().close();

                        getFrame().setVisible(false);
                        oneBackFrame.setVisible(false);

                        DrawUpdateEmployeeList();
                    }

                    else
                    {
                        getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }

                } catch (Exception ex) {
                    System.out.print(ex);
                }

            }

        });

        bankEmployee_data.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                bankEdit_btton.setEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                employee.setEmpBank(bankEmployee_data.getText());
            }
        });

        passEmployee_data.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                passEdit_btton.setEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                passLength = passEmployee_data.getPassword().length;
                pass = passEmployee_data.getPassword(); // שומר את הסיסמא המקורית כצארים
            }
        });

        bankEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ok_button.setEnabled(true);
                bankEmployee_data.setEnabled(true);
            }
        });

        passEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                if (JOptionPane.showConfirmDialog(getFrame(),
                        JOPTIONPANE_MESSAGE_PASS  + employee.getEmpId() + " ?", JOPTIONPANE_TITLE_UPDATE,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                            ok_button.setEnabled(true);
                            passEmployee_data.setBackground(Color.WHITE);
                            passEmployee_data.setEnabled(true);

                    } else {
                         getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }
            }
        });

        nameEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ok_button.setEnabled(true);
                nameEmployee_data.setEnabled(true);
            }
        });

        telEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ok_button.setEnabled(true);
                telEmployee_data.setEnabled(true);
            }
        });

        typeEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ok_button.setEnabled(true);
                comboBoxModel.removeAllElements();
                getComboBoxModel().addElement("Seller");
                getComboBoxModel().addElement("Cashier");
                getComboBoxModel().addElement("Shift manager");
                type_box.setEnabled(true);
            }
        });

        type_box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                typeEdit_btton.setEnabled(false);
                employee.setEmpType((String) getType_box().getSelectedItem());
            }
        });

        nameEmployee_data.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                nameEdit_btton.setEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                employee.setEmpName(nameEmployee_data.getText());

            }
        });

        telEmployee_data.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                telEdit_btton.setEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                employee.setEmpTel(telEmployee_data.getText());
            }
        });

        getCancel_button().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){


                getFrame().setVisible(false);
                oneBackFrame.setVisible(false);
                EmployeeListManagement employeeListManagement = new EmployeeListManagement(twoBackFrame,branchName);
                try {

                    employeeListManagement.DrawEmployee();

                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                employeeListManagement.InitializeActions();
                employeeListManagement.setVisible(true);
                employeeListManagement.setLocationRelativeTo(null);
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

    private void DrawUpdateEmployeeList() {

        EmployeeListManagement employeeListManagement = new EmployeeListManagement(twoBackFrame,branchName);

        try {
            employeeListManagement.DrawEmployee();

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        employeeListManagement.InitializeActions();
        employeeListManagement.setVisible(true);
        employeeListManagement.setLocationRelativeTo(null);
    }

    private void ResizeThePhoto(String path) {

        // resize the photo
        ImageIcon MyImage = new ImageIcon(path);
        Image img = MyImage.getImage();
        Image newImage = img.getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);

        photoLabel = new JLabel(image);
        springLayout.putConstraint(SpringLayout.WEST,photoLabel,100,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,photoLabel,500,SpringLayout.NORTH,jPanelMain);
        jPanelMain.add(photoLabel);
    }

    private boolean CheckThePass() {

        if (!(passLength >=8 && passLength <=12)) {

            JOptionPane.showMessageDialog(null, "Password: Your password length is incorrect");
            return false;
        }

        else if (!(PassCheck(pass))) {

            JOptionPane.showMessageDialog(null, "Password: you're password is incorrect");
            return false;
        }
        return true;
    }

    private void generateNewSafePassword() {

        // Generate Salt. The generated value stored in DB.
        String salt = PasswordUtils.getSalt(30);

        employee.setEmpPassSalt(salt);

        // Protect user's password. The generated value stored in DB.
        employee.setEmpPass(PasswordUtils.generateSecurePassword(new String(passEmployee_data.getPassword()), salt));
    }

    private String GetResponseFromServer() throws ParseException, IOException {

        String line = client.getInputStream().readUTF();
        JSONObject jsonObjectResponse;
        JSONParser jsonParser = new JSONParser();
        jsonObjectResponse = (JSONObject) jsonParser.parse(line);
        String response = (String) jsonObjectResponse.get("Failed");

        return response;
    }

    private void PrepareAndSendJsonDataToServer() throws IOException {

        jsonObject = new JSONObject();
        objectMapper = new ObjectMapper();

        String empStr = objectMapper.writeValueAsString(employee);
        String ChangeInPass = String.valueOf(anyChangeInPass);

        String IsPhotoChange;
        if(isPhotoChanged)
            IsPhotoChange = "true";
        else
            IsPhotoChange = "false";

        jsonObject.put("GuiName", "UpdateEmployee");
        jsonObject.put("IsChangePass", ChangeInPass);
        jsonObject.put("IsChangePhoto", IsPhotoChange);
        jsonObject.put("employee", empStr);


        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    protected void DrawEmployeeEditDetails() {

        GUISettingForJFrame();

        GUISettingForJPanel();

        this.setContentPane(jPanelMain);
    }

    private void GUISettingForJFrame() {

        this.setTitle("Employee Edit Details");
        this.setSize(500, 700);
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

        springLayout.putConstraint(SpringLayout.WEST,label_message,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,label_message,25,SpringLayout.NORTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,idEmployee_data,35,SpringLayout.EAST,idEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,idEmployee_data,25,SpringLayout.SOUTH,label_message);

        springLayout.putConstraint(SpringLayout.WEST,telEmployee_data,35,SpringLayout.EAST,telEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,telEmployee_data,25,SpringLayout.SOUTH,type_box);

        springLayout.putConstraint(SpringLayout.WEST,snEmployee_data,35,SpringLayout.EAST,snEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,snEmployee_data,25,SpringLayout.SOUTH,idEmployee_data);

        springLayout.putConstraint(SpringLayout.WEST,bankEmployee_data,35,SpringLayout.EAST,bankEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,bankEmployee_data,25,SpringLayout.SOUTH,telEmployee_data);

        springLayout.putConstraint(SpringLayout.WEST,passEmployee_data,35,SpringLayout.EAST,passEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,passEmployee_data,25,SpringLayout.SOUTH,bankEmployee_data);

        springLayout.putConstraint(SpringLayout.WEST,branchEmployee_data,35,SpringLayout.EAST,branchEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,branchEmployee_data,25,SpringLayout.SOUTH,snEmployee_data);

        springLayout.putConstraint(SpringLayout.WEST,nameEmployee_data,35,SpringLayout.EAST,nameEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,nameEmployee_data,25,SpringLayout.SOUTH,branchEmployee_data);

        springLayout.putConstraint(SpringLayout.WEST,type_box,35,SpringLayout.EAST,typeEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,type_box,25,SpringLayout.SOUTH,nameEmployee_data);
    }

    private void GUIPlaceButtonsComponents() {

        springLayout.putConstraint(SpringLayout.WEST,remove_photo_button,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,remove_photo_button,50,SpringLayout.SOUTH,ok_button);

        springLayout.putConstraint(SpringLayout.WEST,change_photo_button,50,SpringLayout.EAST,remove_photo_button);
        springLayout.putConstraint(SpringLayout.NORTH,change_photo_button,50,SpringLayout.SOUTH,cancel_button);

        springLayout.putConstraint(SpringLayout.EAST,nameEdit_btton,-100,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,nameEdit_btton,25,SpringLayout.SOUTH,branchEmployee_data);

        springLayout.putConstraint(SpringLayout.EAST,typeEdit_btton,-100,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,typeEdit_btton,25,SpringLayout.SOUTH,nameEdit_btton);

        springLayout.putConstraint(SpringLayout.EAST,telEdit_btton,-100,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,telEdit_btton,25,SpringLayout.SOUTH,typeEdit_btton);

        springLayout.putConstraint(SpringLayout.EAST,passEdit_btton,-100,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,passEdit_btton,25,SpringLayout.SOUTH,bankEdit_btton);

        springLayout.putConstraint(SpringLayout.WEST,ok_button,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,ok_button,50,SpringLayout.SOUTH,passEmployee_label);

        springLayout.putConstraint(SpringLayout.EAST,bankEdit_btton,-100,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,bankEdit_btton,25,SpringLayout.SOUTH,telEdit_btton);

        springLayout.putConstraint(SpringLayout.WEST,cancel_button,35,SpringLayout.EAST,ok_button);
        springLayout.putConstraint(SpringLayout.NORTH,cancel_button,50,SpringLayout.SOUTH,passEmployee_label);
    }

    private void GUIPlaceLabelsComponents() {

        springLayout.putConstraint(SpringLayout.WEST,photoLabel,100,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,photoLabel,500,SpringLayout.NORTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,idEmployee_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,idEmployee_label,25,SpringLayout.SOUTH,label_message);

        springLayout.putConstraint(SpringLayout.WEST,snEmployee_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,snEmployee_label,25,SpringLayout.SOUTH,idEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,branchEmployee_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,branchEmployee_label,25,SpringLayout.SOUTH,snEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,nameEmployee_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,nameEmployee_label,25,SpringLayout.SOUTH,branchEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,typeEmployee_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,typeEmployee_label,25,SpringLayout.SOUTH,nameEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,telEmployee_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,telEmployee_label,25,SpringLayout.SOUTH,typeEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,bankEmployee_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,bankEmployee_label,25,SpringLayout.SOUTH,telEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,passEmployee_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,passEmployee_label,25,SpringLayout.SOUTH,bankEmployee_label);
    }

    private void GUIAddComponentsOnJPanel() {

        jPanelMain = new JPanel();

        GUISetTextFieldsAndComboComponents();

        GUISetLabelsComponents();

        GUISetButtonsComponents();

        GUISetPhotoComponent();
    }

    private void GUISetPhotoComponent() {

        photoLabelJPG = new ImageIcon(getClass().getResource(NO_PHOTO));
        photoLabel = new JLabel(photoLabelJPG);
        photoLabel.setPreferredSize(new Dimension(150, 150)); // לשנות שיהיה ריסאייזבאל כמו שמציג את התמונות ברגע שבוחר אותם מהמחשב
        jPanelMain.add(photoLabel);
    }

    private void GUISetButtonsComponents() {

        change_photo_button = new JButton("CHANGE IMAGE");
        cancel_button = new JButton("CANCEL");
        nameEdit_btton = new JButton("EDIT");
        telEdit_btton = new JButton("EDIT");
        typeEdit_btton = new JButton("EDIT");
        passEdit_btton =  new JButton("EDIT");
        bankEdit_btton = new JButton("EDIT");

        ok_button = new JButton("OK");
        ok_button.setEnabled(false);

        remove_photo_button = new JButton("REMOVE IMAGE");
        if(employee.getEmpPhoto().equals("C:/Users/AviHalif/IdeaProjects/Avi/src/images/No_Image_Available.png"))
            remove_photo_button.setEnabled(false);

        jPanelMain.add(change_photo_button);
        jPanelMain.add(remove_photo_button);
        jPanelMain.add(nameEdit_btton);
        jPanelMain.add(telEdit_btton);
        jPanelMain.add(typeEdit_btton);
        jPanelMain.add(passEdit_btton);
        jPanelMain.add(bankEdit_btton);

        jPanelMain.add(ok_button);
        jPanelMain.add(cancel_button);
    }

    private void GUISetLabelsComponents() {

        label_message = new JLabel("Employee details:");
        idEmployee_label = new JLabel("Employee ID:");
        snEmployee_label = new JLabel("Employee S/N:");
        nameEmployee_label = new JLabel("Employee name:");
        telEmployee_label = new JLabel("Employee tel:");
        bankEmployee_label = new JLabel("Employee bank:");
        branchEmployee_label = new JLabel("Employee branch:");
        passEmployee_label = new JLabel("Employee password:");
        typeEmployee_label = new JLabel("Employee type:");

        jPanelMain.add(label_message);
        jPanelMain.add(idEmployee_label);
        jPanelMain.add(snEmployee_label);
        jPanelMain.add(nameEmployee_label);
        jPanelMain.add(telEmployee_label);
        jPanelMain.add(bankEmployee_label);
        jPanelMain.add(branchEmployee_label);
        jPanelMain.add(passEmployee_label);
        jPanelMain.add(typeEmployee_label);






    }

    private void GUISetTextFieldsAndComboComponents() {

        idEmployee_data = new JTextField();
        idEmployee_data.setEnabled(false);

        nameEmployee_data= new JTextField();
        nameEmployee_data.setEnabled(false);

        telEmployee_data = new JTextField();
        telEmployee_data.setEnabled(false);

        bankEmployee_data = new JTextField();
        bankEmployee_data.setEnabled(false);

        branchEmployee_data = new JTextField();
        branchEmployee_data.setEnabled(false);

        snEmployee_data = new JTextField();
        snEmployee_data.setEnabled(false);

        passEmployee_data = new JPasswordField(15);
        passEmployee_data.setEnabled(false);
        passEmployee_data.setBackground(Color.BLACK);

        comboBoxModel = new DefaultComboBoxModel();
        type_box = new JComboBox(comboBoxModel);

        comboBoxModel.addElement(employee.getEmpType());
        type_box.setEnabled(false);


        idEmployee_data.setText(employee.getEmpId());
        nameEmployee_data.setText(employee.getEmpName());
        telEmployee_data.setText(employee.getEmpTel());
        snEmployee_data.setText(employee.getEmpSn());
        bankEmployee_data.setText(employee.getEmpBank());
        branchEmployee_data.setText(employee.getEmpBranch());

        jPanelMain.add(idEmployee_data);
        jPanelMain.add(nameEmployee_data);
        jPanelMain.add(telEmployee_data);
        jPanelMain.add(bankEmployee_data);
        jPanelMain.add(passEmployee_data);
        jPanelMain.add(snEmployee_data);
        jPanelMain.add(branchEmployee_data);
        jPanelMain.add(type_box);
    }

    public JFrame getFrame() {
        return this;
    }

    private static boolean CheckEmpFields(Employee employee){


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

        return true;
    }

    private boolean MaxSizeImage(String path) {

        boolean temp = false;
        File file = new File(path);
        long length = file.length();

        if (length < 32000)
            temp = true;

        return temp;
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

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public JPanel getjPanelMain() {
        return jPanelMain;
    }

    public JFrame getOneBackFrame() {
        return oneBackFrame;
    }

    public void setOneBackFrame(JFrame oneBackFrame) {
        this.oneBackFrame = oneBackFrame;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
