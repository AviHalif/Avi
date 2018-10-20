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
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class EmployeeEditDetails extends JFrame {

    private char[] pass;
    private Client client;
    private byte[] decoded;
    private int passLength;
    private JPanel jPanelMain;
    private Employee employee;
    private String branchName;
    private JComboBox type_box;
    private JSONObject jsonObject;
    private ObjectMapper objectMapper;
    private SpringLayout springLayout;
    private JPasswordField passEmployee_data;
    private DefaultComboBoxModel comboBoxModel;
    private boolean anyChangeInPass, isPhotoChanged;
    private JFrame oneBackFrame, twoBackFrame, thirdBackFrame;
    private JTextField idEmployee_data, nameEmployee_data, telEmployee_data, bankEmployee_data, snEmployee_data, branchEmployee_data;
    private JButton nameEdit_btton, telEdit_btton, typeEdit_btton, passEdit_btton, bankEdit_btton, ok_button, cancel_button, change_photo_button;
    private ImageIcon photoLabelJPG, backgroundPhotoTopPanelJPG, titlePhotoJPG, backLogoJPG, label_id, label_name, label_phone, label_type, label_sn, label_bank, label_branch, label_pass,
                      nameEditLogoJPG,  bankEditLogoJPG, passEditLogoJPG, typeEditLogoJPG, telEditLogoJPG, changeLogoJPG, updateEmpLogoJPG;
    private JLabel idEmployee_label, snEmployee_label, nameEmployee_label, telEmployee_label, bankEmployee_label, branchEmployee_label,
                   passEmployee_label, typeEmployee_label, photoLabel, backgroundPhotoLabel, titleLabel;
    public static final int FRAMEֹֹ_POSITION_X = 7, FRAMEֹֹ_POSITION_Y = 230, FRAMEֹֹ_WIDTH_ֹSIZE = 1520, FRAMEֹֹ_HEIGHT_ֹSIZE = 630, JTEXTFIELD_WIDTH = 250, JTEXTFIELD_HEIGHT = 50,
                            BUTTON_WIDTH = 100, BUTTON_HEIGHT = 50, BUTTON_WIDTH2 = 300, BUTTON_WIDTH3 = 350;
    public static final String UPDATE_EMPLOYEE = "/src/images/update_employee.png", CHANGE_PHOTO = "/src/images/change_photo.png", TITLE = "/src/images/employee_update.png",
                              JOPTIONPANE_MESSAGE_UPDATE = "Are you sure you want to update employee ID number: ",
                              JOPTIONPANE_MESSAGE_PASS = "Are you sure you want to change the password of employee ID: ", JOPTIONPANE_TITLE_UPDATE = "CASTRO - UPDATE DETAILS EMPLOYEE",
                              NO_PHOTO = "/src/images/No_Image_Available.png", BACK_PHOTO = "/src/images/back_edit_2.png", BACK_BUTTON = "/src/images/back.png",
                              LABEL_NAME = "/src/images/update_name.png", LABEL_PHONE = "/src/images/update_phone.png", LABEL_TYPE = "/src/images/update_type.png",
                              LABEL_ID = "/src/images/update_id.png", EDIT = "/src/images/edit.png", LABEL_SN = "/src/images/update_sn.png", LABEL_BANK = "/src/images/update_bank.png",
                              LABEL_BRANCH = "/src/images/update_branch.png", LABEL_PASSWORD = "/src/images/update_password.png";


    public EmployeeEditDetails(String empId,String empSn,String empName,String empTel,
                               String empBank, String empBranch, String empType, String empPhoto,
                               JFrame oneBackFrame,JFrame twoBackFrame, JFrame thirdBackFrame, String branchName) {

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

        String encodedPhotos;
        Base64 codec = new Base64();
        encodedPhotos = employee.getEmpPhoto();
        decoded = codec.decode(encodedPhotos);

        ImageIcon MyImage = new ImageIcon(decoded);
        Image img = MyImage.getImage();
        Image newImage = img.getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(),Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);
        photoLabel = new JLabel(image);

        springLayout.putConstraint(SpringLayout.EAST,photoLabel,0,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,photoLabel,0,SpringLayout.NORTH,jPanelMain);
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

                    if(MaxSizeImage(path)) {
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
                bankEmployee_data.setBorder(BorderFactory.createLineBorder(Color.red));
            }

            @Override
            public void focusLost(FocusEvent e) {
                employee.setEmpBank(bankEmployee_data.getText());
                bankEmployee_data.setBorder(BorderFactory.createLineBorder(Color.black));

            }
        });

        passEmployee_data.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                passEdit_btton.setEnabled(false);
                passEmployee_data.setBorder(BorderFactory.createLineBorder(Color.red));
            }

            @Override
            public void focusLost(FocusEvent e) {
                passLength = passEmployee_data.getPassword().length;
                pass = passEmployee_data.getPassword();
                passEmployee_data.setBorder(BorderFactory.createLineBorder(Color.black));
            }
        });

        bankEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ok_button.setEnabled(true);
                bankEmployee_data.setEnabled(true);
                bankEmployee_data.setBorder(BorderFactory.createLineBorder(Color.red));
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
                            passEmployee_data.setBorder(BorderFactory.createLineBorder(Color.red));

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
                nameEmployee_data.setBorder(BorderFactory.createLineBorder(Color.red));

            }
        });

        telEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ok_button.setEnabled(true);
                telEmployee_data.setEnabled(true);
                telEmployee_data.setBorder(BorderFactory.createLineBorder(Color.red));
            }
        });

        typeEdit_btton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ok_button.setEnabled(true);
                comboBoxModel.removeAllElements();
                getComboBoxModel().addElement("Seller");
                getComboBoxModel().addElement("Cashier");
                getComboBoxModel().addElement("Manager");
                type_box.setEnabled(true);
                type_box.setBorder(BorderFactory.createLineBorder(Color.red));
            }
        });

        type_box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                typeEdit_btton.setEnabled(false);
                employee.setEmpType((String) getType_box().getSelectedItem());
            }
        });

        type_box.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {

                type_box.setBorder(BorderFactory.createLineBorder(Color.red));
            }

            @Override
            public void focusLost(FocusEvent e) {

                type_box.setBorder(BorderFactory.createLineBorder(Color.black));

            }
        });

        nameEmployee_data.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                nameEdit_btton.setEnabled(false);
                nameEmployee_data.setBorder(BorderFactory.createLineBorder(Color.red));
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                employee.setEmpName(nameEmployee_data.getText());
                nameEmployee_data.setBorder(BorderFactory.createLineBorder(Color.black));

            }
        });

        telEmployee_data.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                telEdit_btton.setEnabled(false);
                telEmployee_data.setBorder(BorderFactory.createLineBorder(Color.red));
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                employee.setEmpTel(telEmployee_data.getText());
                telEmployee_data.setBorder(BorderFactory.createLineBorder(Color.black));
            }
        });

        getCancel_button().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){


                getFrame().setVisible(false);
                oneBackFrame.setVisible(false);
                EmployeeListManagement employeeListManagement = new EmployeeListManagement(twoBackFrame,branchName);
                try {

                    employeeListManagement.setUndecorated(true);
                    employeeListManagement.DrawEmployee();

                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                employeeListManagement.InitializeActions();
                employeeListManagement.setVisible(true);
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
            employeeListManagement.setUndecorated(true);

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        employeeListManagement.InitializeActions();
        employeeListManagement.setVisible(true);
    }

    private void ResizeThePhoto(String path) {

        // resize the photo
        ImageIcon MyImage = new ImageIcon(path);
        Image img = MyImage.getImage();
        Image newImage = img.getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);

        photoLabel = new JLabel(image);
        springLayout.putConstraint(SpringLayout.EAST,photoLabel,0,SpringLayout.EAST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,photoLabel,0,SpringLayout.NORTH,jPanelMain);
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

        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE,FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setLocation(FRAMEֹֹ_POSITION_X,FRAMEֹֹ_POSITION_Y);
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        springLayout.putConstraint(SpringLayout.WEST,titleLabel,130,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,titleLabel,25,SpringLayout.NORTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,idEmployee_data,160,SpringLayout.EAST,idEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,idEmployee_data,40,SpringLayout.SOUTH,titleLabel);

        springLayout.putConstraint(SpringLayout.WEST,telEmployee_data,100,SpringLayout.EAST,telEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,telEmployee_data,50,SpringLayout.SOUTH,type_box);

        springLayout.putConstraint(SpringLayout.WEST,snEmployee_data,110,SpringLayout.EAST,snEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,snEmployee_data,60,SpringLayout.SOUTH,idEmployee_data);

        springLayout.putConstraint(SpringLayout.WEST,bankEmployee_data,95,SpringLayout.EAST,bankEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,bankEmployee_data,50,SpringLayout.SOUTH,branchEmployee_data);

        springLayout.putConstraint(SpringLayout.WEST,passEmployee_data,15,SpringLayout.EAST,passEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,passEmployee_data,63,SpringLayout.SOUTH,telEmployee_data);

        springLayout.putConstraint(SpringLayout.WEST,branchEmployee_data,35,SpringLayout.EAST,branchEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,branchEmployee_data,60,SpringLayout.SOUTH,snEmployee_data);

        springLayout.putConstraint(SpringLayout.WEST,nameEmployee_data,110,SpringLayout.EAST,nameEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,nameEmployee_data,160,SpringLayout.NORTH,titleLabel);

        springLayout.putConstraint(SpringLayout.WEST,type_box,125,SpringLayout.EAST,typeEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,type_box,55,SpringLayout.SOUTH,nameEmployee_data);
    }

    private void GUIPlaceButtonsComponents() {

        springLayout.putConstraint(SpringLayout.EAST,change_photo_button,1195,SpringLayout.EAST,photoLabel);
        springLayout.putConstraint(SpringLayout.NORTH,change_photo_button,100,SpringLayout.NORTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,nameEdit_btton,400,SpringLayout.EAST,nameEmployee_label);
        springLayout.putConstraint(SpringLayout.NORTH,nameEdit_btton,40,SpringLayout.SOUTH,titleLabel);

        springLayout.putConstraint(SpringLayout.WEST,typeEdit_btton,40,SpringLayout.EAST,type_box);
        springLayout.putConstraint(SpringLayout.NORTH,typeEdit_btton,55,SpringLayout.SOUTH,nameEdit_btton);

        springLayout.putConstraint(SpringLayout.WEST,telEdit_btton,40,SpringLayout.EAST,telEmployee_data);
        springLayout.putConstraint(SpringLayout.NORTH,telEdit_btton,50,SpringLayout.SOUTH,typeEdit_btton);

        springLayout.putConstraint(SpringLayout.WEST,passEdit_btton,40,SpringLayout.EAST,passEmployee_data);
        springLayout.putConstraint(SpringLayout.NORTH,passEdit_btton,60,SpringLayout.SOUTH,telEdit_btton);

        springLayout.putConstraint(SpringLayout.WEST,ok_button,550,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.SOUTH,ok_button,-10,SpringLayout.SOUTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,bankEdit_btton,50,SpringLayout.EAST,bankEmployee_data);
        springLayout.putConstraint(SpringLayout.NORTH,bankEdit_btton,50,SpringLayout.SOUTH,branchEmployee_data);

        springLayout.putConstraint(SpringLayout.WEST,cancel_button,0,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.SOUTH,cancel_button,0,SpringLayout.SOUTH,jPanelMain);
    }

    private void GUIPlaceLabelsComponents() {

        springLayout.putConstraint(SpringLayout.WEST,idEmployee_label,10,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,idEmployee_label,25,SpringLayout.SOUTH,titleLabel);

        springLayout.putConstraint(SpringLayout.WEST,snEmployee_label,30,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,snEmployee_label,25,SpringLayout.SOUTH,idEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,branchEmployee_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,branchEmployee_label,25,SpringLayout.SOUTH,snEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,nameEmployee_label,780,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,nameEmployee_label,25,SpringLayout.SOUTH,titleLabel);

        springLayout.putConstraint(SpringLayout.WEST,typeEmployee_label,765,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,typeEmployee_label,25,SpringLayout.SOUTH,nameEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,telEmployee_label,790,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,telEmployee_label,25,SpringLayout.SOUTH,typeEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,bankEmployee_label,35,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,bankEmployee_label,25,SpringLayout.SOUTH,telEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,passEmployee_label,795,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,passEmployee_label,25,SpringLayout.SOUTH,telEmployee_label);

        springLayout.putConstraint(SpringLayout.WEST,backgroundPhotoLabel,-150,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,backgroundPhotoLabel,0,SpringLayout.NORTH,jPanelMain);
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
        photoLabel.setPreferredSize(new Dimension(150, 150));
        jPanelMain.add(photoLabel);

        backgroundPhotoTopPanelJPG = new ImageIcon(getClass().getResource(BACK_PHOTO));
        backgroundPhotoLabel = new JLabel(backgroundPhotoTopPanelJPG);
        jPanelMain.add(backgroundPhotoLabel);
    }

    private void GUISetButtonsComponents() {

        backLogoJPG = new ImageIcon(getClass().getResource(BACK_BUTTON));
        cancel_button = new JButton(backLogoJPG);
        cancel_button.setBorderPainted(false);
        cancel_button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.black, Color.black));

        nameEditLogoJPG = new ImageIcon(getClass().getResource(EDIT));
        nameEdit_btton = new JButton(nameEditLogoJPG);
        nameEdit_btton.setBorderPainted(true);
        nameEdit_btton.setOpaque(false);
        nameEdit_btton.setBackground(Color.white);
        nameEdit_btton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        nameEdit_btton.setBorder(new LineBorder(Color.red));

        telEditLogoJPG = new ImageIcon(getClass().getResource(EDIT));
        telEdit_btton = new JButton(telEditLogoJPG);
        telEdit_btton.setBorderPainted(true);
        telEdit_btton.setOpaque(false);
        telEdit_btton.setBackground(Color.white);
        telEdit_btton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        telEdit_btton.setBorder(new LineBorder(Color.red));

        typeEditLogoJPG = new ImageIcon(getClass().getResource(EDIT));
        typeEdit_btton = new JButton(typeEditLogoJPG);
        typeEdit_btton.setBorderPainted(true);
        typeEdit_btton.setOpaque(false);
        typeEdit_btton.setBackground(Color.white);
        typeEdit_btton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        typeEdit_btton.setBorder(new LineBorder(Color.red));

        passEditLogoJPG = new ImageIcon(getClass().getResource(EDIT));
        passEdit_btton = new JButton(passEditLogoJPG);
        passEdit_btton.setBorderPainted(true);
        passEdit_btton.setOpaque(false);
        passEdit_btton.setBackground(Color.white);
        passEdit_btton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        passEdit_btton.setBorder(new LineBorder(Color.red));

        bankEditLogoJPG = new ImageIcon(getClass().getResource(EDIT));
        bankEdit_btton = new JButton(bankEditLogoJPG);
        bankEdit_btton.setBorderPainted(true);
        bankEdit_btton.setOpaque(false);
        bankEdit_btton.setBackground(Color.white);
        bankEdit_btton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        bankEdit_btton.setBorder(new LineBorder(Color.red));

        bankEditLogoJPG = new ImageIcon(getClass().getResource(EDIT));
        bankEdit_btton = new JButton(bankEditLogoJPG);
        bankEdit_btton.setBorderPainted(true);
        bankEdit_btton.setOpaque(false);
        bankEdit_btton.setBackground(Color.white);
        bankEdit_btton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        bankEdit_btton.setBorder(new LineBorder(Color.red));

        changeLogoJPG = new ImageIcon(getClass().getResource(CHANGE_PHOTO));
        change_photo_button = new JButton(changeLogoJPG);
        change_photo_button.setBorderPainted(true);
        change_photo_button.setBackground(Color.white);
        change_photo_button.setPreferredSize(new Dimension(BUTTON_WIDTH2, BUTTON_HEIGHT));
        change_photo_button.setBorder(new LineBorder(Color.red));

        updateEmpLogoJPG = new ImageIcon(getClass().getResource(UPDATE_EMPLOYEE));
        ok_button = new JButton(updateEmpLogoJPG);
        ok_button.setBorderPainted(true);
        ok_button.setBackground(Color.black);
        ok_button.setPreferredSize(new Dimension(BUTTON_WIDTH3, BUTTON_HEIGHT));
        ok_button.setBorder(new LineBorder(Color.red));
        ok_button.setEnabled(false);

        jPanelMain.add(change_photo_button);
        jPanelMain.add(nameEdit_btton);
        jPanelMain.add(telEdit_btton);
        jPanelMain.add(typeEdit_btton);
        jPanelMain.add(passEdit_btton);
        jPanelMain.add(bankEdit_btton);

        jPanelMain.add(ok_button);
        jPanelMain.add(cancel_button);
    }

    private void GUISetLabelsComponents() {

        titlePhotoJPG = new ImageIcon(getClass().getResource(TITLE));
        titleLabel = new JLabel(titlePhotoJPG);

        label_id = new ImageIcon(getClass().getResource(LABEL_ID));
        idEmployee_label = new JLabel(label_id);

        label_name = new ImageIcon(getClass().getResource(LABEL_NAME));
        nameEmployee_label = new JLabel(label_name);

        label_phone = new ImageIcon(getClass().getResource(LABEL_PHONE));
        telEmployee_label = new JLabel(label_phone);

        label_type = new ImageIcon(getClass().getResource(LABEL_TYPE));
        typeEmployee_label = new JLabel(label_type);

        label_sn = new ImageIcon(getClass().getResource(LABEL_SN));
        snEmployee_label = new JLabel(label_sn);

        label_bank = new ImageIcon(getClass().getResource(LABEL_BANK));
        bankEmployee_label = new JLabel(label_bank);

        label_branch = new ImageIcon(getClass().getResource(LABEL_BRANCH));
        branchEmployee_label = new JLabel(label_branch);

        label_pass = new ImageIcon(getClass().getResource(LABEL_PASSWORD));
        passEmployee_label = new JLabel(label_pass);

        jPanelMain.add(titleLabel);
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
        idEmployee_data.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        idEmployee_data.setHorizontalAlignment(JTextField.CENTER);
        idEmployee_data.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        idEmployee_data.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        idEmployee_data.setForeground (Color.black);
        idEmployee_data.setOpaque(false);
        idEmployee_data.setEnabled(false);

        nameEmployee_data = new JTextField();
        nameEmployee_data.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        nameEmployee_data.setHorizontalAlignment(JTextField.CENTER);
        nameEmployee_data.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        nameEmployee_data.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        nameEmployee_data.setForeground (Color.black);
        nameEmployee_data.setOpaque(false);
        nameEmployee_data.setEnabled(false);

        telEmployee_data = new JTextField();
        telEmployee_data.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        telEmployee_data.setHorizontalAlignment(JTextField.CENTER);
        telEmployee_data.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        telEmployee_data.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        telEmployee_data.setForeground (Color.black);
        telEmployee_data.setOpaque(false);
        telEmployee_data.setEnabled(false);

        bankEmployee_data = new JTextField();
        bankEmployee_data.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        bankEmployee_data.setHorizontalAlignment(JTextField.CENTER);
        bankEmployee_data.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        bankEmployee_data.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        bankEmployee_data.setForeground (Color.black);
        bankEmployee_data.setOpaque(false);
        bankEmployee_data.setEnabled(false);

        branchEmployee_data = new JTextField();
        branchEmployee_data.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        branchEmployee_data.setHorizontalAlignment(JTextField.CENTER);
        branchEmployee_data.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        branchEmployee_data.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        branchEmployee_data.setForeground (Color.black);
        branchEmployee_data.setOpaque(false);
        branchEmployee_data.setEnabled(false);

        snEmployee_data = new JTextField();
        snEmployee_data.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        snEmployee_data.setHorizontalAlignment(JTextField.CENTER);
        snEmployee_data.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        snEmployee_data.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        snEmployee_data.setForeground (Color.black);
        snEmployee_data.setOpaque(false);
        snEmployee_data.setEnabled(false);

        passEmployee_data = new JPasswordField();
        passEmployee_data.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        passEmployee_data.setHorizontalAlignment(JTextField.CENTER);
        passEmployee_data.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        passEmployee_data.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        passEmployee_data.setForeground (Color.black);
        passEmployee_data.setOpaque(false);
        passEmployee_data.setEnabled(false);

        comboBoxModel = new DefaultComboBoxModel();
        type_box = new JComboBox(comboBoxModel);

        type_box.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        type_box.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        type_box.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        type_box.setOpaque(false);

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

    public static void main(String[] args) {
        EmployeeEditDetails employeeEditDetails = new EmployeeEditDetails("25232342", "dfsfsdf", "44444444","SDFEF","dsfsdf",
                                                                       "sdfsdf","sdfsdf","/src/images/No_Image_Available.png",
                                                                     null,null,null,"sdfdsf");
        employeeEditDetails.setUndecorated(true);
        employeeEditDetails.DrawEmployeeEditDetails();
        employeeEditDetails.InitializeActions();
        employeeEditDetails.setVisible(true);
        employeeEditDetails.resizePhotoInMenu();
    }


}
