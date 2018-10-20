package Gui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


public class Register extends JFrame{


    private JComboBox type_box;
    private JFrame jFramePrev, twoBackFrame;
    private DefaultComboBoxModel comboBoxModel;
    private String fullName = "", id = "", phone = "";
    private JTextField phone_text, fullname_text, id_text;
    private JPanel jPanelMain, jPanelButtons, jPanelTopFields;
    private JButton cancelButton, registerButton, checkButton;
    private SpringLayout springLayoutPanels, springLayoutFields, springLayoutButtons;
    private JLabel backgroundPhotoLabel, jLabelId, jLabelname, jLabelphone, jLabeltype;
    private ImageIcon backgroundPhotoJPG, label_id, label_name, label_phone, label_type, backLogoJPG,  registerLogoJPG, checkLogoJPG;
    public static final int FRAMEֹֹ_POSITION_X = 7, FRAMEֹֹ_POSITION_Y = 230, FRAMEֹֹ_WIDTH_ֹSIZE = 1520, FRAMEֹֹ_HEIGHT_ֹSIZE = 630, JTEXTFIELD_WIDTH = 250, JTEXTFIELD_HEIGHT = 50,
                            BUTTON_WIDTH = 190, BUTTON_HEIGHT = 50;
    public static final String BACK_PHOTO = "/src/images/castro_shop_background.png", LABEL_NAME = "/src/images/registry_name.png", LABEL_PHONE = "/src/images/registry_phone.png",
                               LABEL_TYPE = "/src/images/registry_type.png", LABEL_ID = "/src/images/registry_id.png", CHECK_ID = "/src/images/check_id.png",
                               REGISTER = "/src/images/register_id.png", BACK_BUTTON = "/src/images/back.png";


    public Register(JFrame oneBackFrame, JFrame twoBackFrame) {

        SetGUIComponents(oneBackFrame,twoBackFrame);
    }

    private void SetGUIComponents(JFrame oneBackFrame, JFrame twoBackFrame) {

        this.jFramePrev =  oneBackFrame;
        this.twoBackFrame = twoBackFrame;

        jPanelMain = new JPanel();
    }

    private void SetGUIButtons() {

        backLogoJPG = new ImageIcon(getClass().getResource(BACK_BUTTON));
        cancelButton = new JButton(backLogoJPG);
        cancelButton.setBorderPainted(false);
        cancelButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.black, Color.black));
        jPanelButtons.add(cancelButton);

        registerLogoJPG = new ImageIcon(getClass().getResource(REGISTER));
        registerButton = new JButton(registerLogoJPG);
        registerButton.setBorderPainted(true);
        registerButton.setBackground(Color.BLACK);
        registerButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        registerButton.setBorder(new LineBorder(Color.red));

        checkLogoJPG = new ImageIcon(getClass().getResource(CHECK_ID));
        checkButton = new JButton(checkLogoJPG);
        checkButton.setBorderPainted(true);
        checkButton.setBackground(Color.white);
        checkButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        checkButton.setBorder(new LineBorder(Color.red));
    }

    protected void DrawRegister(){

        GUISettingForJFrame();

        GUISettingForJPanel();

        this.setContentPane(jPanelMain);

        InitializeActions();
    }

    private void GUISettingForJPanel() {

        GUIDefineLayoutAndAddComponentsOnJPanel();

        GUIPlaceComponentsOnJPanel();

        GUIPlacePanelsOnJPanel();
    }

    private void GUIPlacePanelsOnJPanel() {

        springLayoutPanels.putConstraint(SpringLayout.WEST,jPanelTopFields,10,SpringLayout.WEST,jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.NORTH,jPanelTopFields,25,SpringLayout.NORTH,jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.WEST,jPanelButtons,0,SpringLayout.WEST,jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.SOUTH,jPanelButtons,0,SpringLayout.SOUTH,jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.EAST,backgroundPhotoLabel,0,SpringLayout.EAST,jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.SOUTH,backgroundPhotoLabel,0,SpringLayout.SOUTH,jPanelMain);

        jPanelMain.add(jPanelButtons);
        jPanelMain.add(jPanelTopFields);
        jPanelMain.add(backgroundPhotoLabel);
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayoutFields = new SpringLayout();
        springLayoutButtons = new SpringLayout();

        jPanelButtons.setLayout(springLayoutButtons);
        jPanelTopFields.setLayout(springLayoutFields);

        GUIPlaceLabelsComponents();

        GUIPlaceButtonsComponents();

        GUIPlaceTextFieldsAndComboComponents();
    }

    private void GUIPlaceTextFieldsAndComboComponents() {

        springLayoutFields.putConstraint(SpringLayout.WEST,id_text,-50,SpringLayout.EAST,jLabelId);
        springLayoutFields.putConstraint(SpringLayout.NORTH,id_text,120,SpringLayout.NORTH,jPanelTopFields);

        springLayoutFields.putConstraint(SpringLayout.WEST,fullname_text,8,SpringLayout.EAST,jLabelname);
        springLayoutFields.putConstraint(SpringLayout.NORTH,fullname_text,100,SpringLayout.NORTH,id_text);

        springLayoutFields.putConstraint(SpringLayout.WEST,phone_text,-2,SpringLayout.EAST,jLabelphone);
        springLayoutFields.putConstraint(SpringLayout.NORTH,phone_text,100,SpringLayout.NORTH,fullname_text);

        springLayoutFields.putConstraint(SpringLayout.WEST,type_box,-20,SpringLayout.EAST,jLabeltype);
        springLayoutFields.putConstraint(SpringLayout.NORTH,type_box,100,SpringLayout.NORTH,phone_text);
    }

    private void GUIPlaceButtonsComponents() {

        springLayoutFields.putConstraint(SpringLayout.WEST,checkButton,50,SpringLayout.WEST,jPanelTopFields);
        springLayoutFields.putConstraint(SpringLayout.NORTH,checkButton,120,SpringLayout.NORTH,jPanelTopFields);

        springLayoutButtons.putConstraint(SpringLayout.WEST,cancelButton,0,SpringLayout.WEST,jPanelButtons);
        springLayoutButtons.putConstraint(SpringLayout.SOUTH,cancelButton,0,SpringLayout.SOUTH,jPanelButtons);

        springLayoutFields.putConstraint(SpringLayout.WEST,registerButton,50,SpringLayout.WEST,jPanelTopFields);
        springLayoutFields.putConstraint(SpringLayout.NORTH,registerButton,120,SpringLayout.NORTH,jPanelTopFields);
    }

    private void GUIPlaceLabelsComponents() {

        springLayoutFields.putConstraint(SpringLayout.WEST,jLabelId,250,SpringLayout.WEST,jPanelTopFields);
        springLayoutFields.putConstraint(SpringLayout.NORTH,jLabelId,100,SpringLayout.NORTH,jPanelTopFields);

        springLayoutFields.putConstraint(SpringLayout.WEST,jLabelname,250,SpringLayout.WEST,jPanelTopFields);
        springLayoutFields.putConstraint(SpringLayout.NORTH,jLabelname,100,SpringLayout.NORTH,jLabelId);

        springLayoutFields.putConstraint(SpringLayout.WEST,jLabelphone,205,SpringLayout.WEST,jPanelTopFields);
        springLayoutFields.putConstraint(SpringLayout.NORTH,jLabelphone,100,SpringLayout.NORTH,jLabelname);

        springLayoutFields.putConstraint(SpringLayout.WEST,jLabeltype,275,SpringLayout.WEST,jPanelTopFields);
        springLayoutFields.putConstraint(SpringLayout.NORTH,jLabeltype,100,SpringLayout.NORTH,jLabelphone);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelButtons = new JPanel();
        jPanelButtons.setOpaque(false);

        jPanelTopFields = new JPanel();
        jPanelTopFields.setOpaque(false);

        springLayoutPanels = new SpringLayout();
        jPanelMain.setLayout(springLayoutPanels);

        jPanelButtons.setPreferredSize(new Dimension(300, 100));
        jPanelTopFields.setPreferredSize(new Dimension(1600, 750));

        backgroundPhotoJPG = new ImageIcon(getClass().getResource(BACK_PHOTO));
        backgroundPhotoLabel = new JLabel(backgroundPhotoJPG);

        GUISetLabelsComponents();

        GUISetButtonsComponents();

        GUISetTextFieldsAndComboComponents();

        registerButton.setVisible(false);
    }

    private void GUISetTextFieldsAndComboComponents() {

        phone_text = new JTextField(15);
        fullname_text = new JTextField(15);
        id_text = new JTextField(15);
        comboBoxModel = new DefaultComboBoxModel();
        type_box = new JComboBox(comboBoxModel);

        phone_text.setEnabled(false);
        fullname_text.setEnabled(false);

        DefineTextFieldsAndComboSizeAndDecorations();

        jPanelTopFields.add(phone_text);
        jPanelTopFields.add(fullname_text);
        jPanelTopFields.add(id_text);
        jPanelTopFields.add(type_box);
    }

    private void DefineTextFieldsAndComboSizeAndDecorations() {

        phone_text.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        phone_text.setHorizontalAlignment(JTextField.CENTER);
        phone_text.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        phone_text.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        phone_text.setForeground (Color.black);
        phone_text.setOpaque(false);


        fullname_text.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        fullname_text.setHorizontalAlignment(JTextField.CENTER);
        fullname_text.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        fullname_text.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        fullname_text.setForeground (Color.black);
        fullname_text.setOpaque(false);


        id_text.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        id_text.setHorizontalAlignment(JTextField.CENTER);
        id_text.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        id_text.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        id_text.setForeground (Color.black);
        id_text.setOpaque(false);


        type_box.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        type_box.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        type_box.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        type_box.setOpaque(false);
    }

    private void GUISetButtonsComponents() {

        SetGUIButtons();

        registerButton.setEnabled(false);

        jPanelTopFields.add(registerButton);
        jPanelButtons.add(cancelButton);
        jPanelTopFields.add(checkButton);
    }

    private void GUISetLabelsComponents() {

        label_id = new ImageIcon(getClass().getResource(LABEL_ID));
        jLabelId = new JLabel(label_id);

        label_name = new ImageIcon(getClass().getResource(LABEL_NAME));
        jLabelname = new JLabel(label_name);

        label_type = new ImageIcon(getClass().getResource(LABEL_TYPE));
        jLabeltype = new JLabel(label_type);

        label_phone = new ImageIcon(getClass().getResource(LABEL_PHONE));
        jLabelphone = new JLabel(label_phone);

        jPanelTopFields.add(jLabelId);
        jPanelTopFields.add(jLabelname);
        jPanelTopFields.add(jLabeltype);
        jPanelTopFields.add(jLabelphone);
    }

    private void GUISettingForJFrame() {

        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE,FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setLocation(FRAMEֹֹ_POSITION_X,FRAMEֹֹ_POSITION_Y);
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void InitializeActions() {

        fullname_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                fullname_text.setBorder(BorderFactory.createLineBorder(Color.red));
            }

            @Override
            public void focusLost(FocusEvent e) {

                fullname_text.setBorder(BorderFactory.createLineBorder(Color.black));
                fullName = fullname_text.getText();
            }
        });

        phone_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                phone_text.setBorder(BorderFactory.createLineBorder(Color.red));
            }

            @Override
            public void focusLost(FocusEvent e) {

                phone_text.setBorder(BorderFactory.createLineBorder(Color.black));
                phone = phone_text.getText();
            }
        });

        id_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                id_text.setBorder(BorderFactory.createLineBorder(Color.red));
                checkButton.setEnabled(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                id = id_text.getText();
                id_text.setBorder(BorderFactory.createLineBorder(Color.black));
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                getJFrame().setVisible(false);
                jFramePrev.setEnabled(true);
                jFramePrev.toFront();
                jFramePrev.toFront();

            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                jFramePrev.setEnabled(true);
                jFramePrev.toFront();
            }
        });
    }

    protected static boolean CheckId(String custId) {

        if (!(custId.length() <= 9 && custId.length() >= 1)) {
            JOptionPane.showMessageDialog(null, "ID: Your Id length is incorrect");
            return false;
        }
        if (!(custId.matches("[0-9]+"))) {
            JOptionPane.showMessageDialog(null, "ID: Please insert only digits");
            return false;
        }
        return true;
    }

    public JFrame getJFrame() {
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public SpringLayout getSpringLayoutFields() {
        return springLayoutFields;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JLabel getjLabeltype() {
        return jLabeltype;
    }

    public String getPhone() {
        return phone;
    }

    public JFrame getjFramePrev() {
        return jFramePrev;
    }

    public JFrame getTwoBackFrame() {
        return twoBackFrame;
    }

    public JPanel getjPanelMain() {
        return jPanelMain;
    }

    public JPanel getjPanelTopFields() {
        return jPanelTopFields;
    }

    public SpringLayout getSpringLayoutPanels() {
        return springLayoutPanels;
    }

    public JTextField getPhone_text() {
        return phone_text;
    }

    public JTextField getFullname_text() {
        return fullname_text;
    }

    public JTextField getId_text() {
        return id_text;
    }

    public JComboBox getType_box() {
        return type_box;
    }

    public DefaultComboBoxModel getComboBoxModel() {
        return comboBoxModel;
    }

    public JButton getRegisterButton() {
        return registerButton;
    }

    public JButton getCheckButton() {
        return checkButton;
    }

    public static void main(String[] args) throws IOException {
        JFrame oneBackFrame = null, twoBackFrame = null;
        Register reg = new Register(oneBackFrame, twoBackFrame);
        reg.DrawRegister();
        reg.setVisible(true);
        reg.setLocationRelativeTo(null);
    }
}












