package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


public class Register extends JFrame{

    private String fullName = "", id = "", phone = "";

    private JFrame jFramePrev, twoBackFrame;
    private JPanel jPanelMain, jPanelButtons, jPanelTopFields;

    private  SpringLayout springLayoutPanels, springLayoutFields, springLayoutButtons;

    private JTextField phone_text, fullname_text, id_text;
    private JComboBox type_box;
    private DefaultComboBoxModel comboBoxModel;

    private JLabel phone_label, fullname_label, id_label, type_label;

    private JButton OKButton, cancelButton, register;


    public Register(JFrame oneBackFrame, JFrame twoBackFrame) { // הגיע לכאן המסך הראשי של האדמין ואת המסך של הניהול עובדים

        SetGUIComponents(oneBackFrame,twoBackFrame);
    }

    private void SetGUIComponents(JFrame oneBackFrame, JFrame twoBackFrame) {

        this.jFramePrev =  oneBackFrame;
        this.twoBackFrame = twoBackFrame;
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
        springLayoutPanels.putConstraint(SpringLayout.WEST,jPanelButtons,10,SpringLayout.WEST,jPanelMain);
        springLayoutPanels.putConstraint(SpringLayout.SOUTH,jPanelButtons,25,SpringLayout.SOUTH,jPanelMain);

        jPanelMain.add(jPanelButtons);
        jPanelMain.add(jPanelTopFields);
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

        springLayoutFields.putConstraint(SpringLayout.WEST,id_text,10,SpringLayout.EAST,id_label);
        springLayoutFields.putConstraint(SpringLayout.NORTH,id_text,25,SpringLayout.NORTH,jPanelTopFields);

        springLayoutFields.putConstraint(SpringLayout.WEST,fullname_text,10,SpringLayout.EAST,fullname_label);
        springLayoutFields.putConstraint(SpringLayout.NORTH,fullname_text,25,SpringLayout.NORTH,id_text);

        springLayoutFields.putConstraint(SpringLayout.WEST,phone_text,10,SpringLayout.EAST,phone_label);
        springLayoutFields.putConstraint(SpringLayout.NORTH,phone_text,25,SpringLayout.NORTH,fullname_text);

        springLayoutFields.putConstraint(SpringLayout.WEST,type_box,10,SpringLayout.EAST,type_label);
        springLayoutFields.putConstraint(SpringLayout.NORTH,type_box,25,SpringLayout.NORTH,phone_text);
    }

    private void GUIPlaceButtonsComponents() {

        springLayoutButtons.putConstraint(SpringLayout.WEST,OKButton,10,SpringLayout.WEST,jPanelButtons);
        springLayoutButtons.putConstraint(SpringLayout.NORTH,OKButton,25,SpringLayout.NORTH,jPanelButtons);
        springLayoutButtons.putConstraint(SpringLayout.WEST,cancelButton,10,SpringLayout.EAST,OKButton);
        springLayoutButtons.putConstraint(SpringLayout.NORTH,jPanelButtons,25,SpringLayout.NORTH,jPanelButtons);
    }

    private void GUIPlaceLabelsComponents() {

        springLayoutFields.putConstraint(SpringLayout.WEST,id_label,10,SpringLayout.WEST,jPanelTopFields);
        springLayoutFields.putConstraint(SpringLayout.NORTH,id_label,25,SpringLayout.NORTH,jPanelTopFields);

        springLayoutFields.putConstraint(SpringLayout.WEST,fullname_label,10,SpringLayout.WEST,jPanelTopFields);
        springLayoutFields.putConstraint(SpringLayout.NORTH,fullname_label,25,SpringLayout.NORTH,id_label);

        springLayoutFields.putConstraint(SpringLayout.WEST,phone_label,10,SpringLayout.WEST,jPanelTopFields);
        springLayoutFields.putConstraint(SpringLayout.NORTH,phone_label,25,SpringLayout.NORTH,fullname_label);

        springLayoutFields.putConstraint(SpringLayout.WEST,type_label,10,SpringLayout.WEST,jPanelTopFields);
        springLayoutFields.putConstraint(SpringLayout.NORTH,type_label,25,SpringLayout.NORTH,phone_label);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelMain = new JPanel();
        jPanelButtons = new JPanel();
        jPanelTopFields = new JPanel();

        springLayoutPanels = new SpringLayout();
        jPanelMain.setLayout(springLayoutPanels);

        jPanelButtons.setPreferredSize(new Dimension(800, 100));
        jPanelTopFields.setPreferredSize(new Dimension(800, 300));

        GUISetLabelsComponents();

        GUISetButtonsComponents();

        GUISetTextFieldsAndComboComponents();

        register.setVisible(false);
    }

    private void GUISetTextFieldsAndComboComponents() {

        phone_text = new JTextField(15);
        fullname_text = new JTextField(15);
        id_text = new JTextField(15);
        comboBoxModel = new DefaultComboBoxModel();
        type_box = new JComboBox(comboBoxModel);

        phone_text.setEnabled(false);
        fullname_text.setEnabled(false);

        jPanelTopFields.add(phone_text);
        jPanelTopFields.add(fullname_text);
        jPanelTopFields.add(id_text);
        jPanelTopFields.add(type_box);
    }

    private void GUISetButtonsComponents() {

        OKButton = new JButton("OK");
        cancelButton = new JButton("CANCEL");
        register = new JButton("REGISTER");

        OKButton.setEnabled(false);

        jPanelButtons.add(OKButton);
        jPanelButtons.add(cancelButton);
        jPanelButtons.add(register);
    }

    private void GUISetLabelsComponents() {

        id_label = new JLabel("ID :");
        fullname_label = new JLabel("FULL NAME :");
        phone_label = new JLabel("PHONE :");
        type_label = new JLabel("TYPE");

        jPanelTopFields.add(id_label);
        jPanelTopFields.add(fullname_label);
        jPanelTopFields.add(phone_label);
        jPanelTopFields.add(type_label);
    }

    private void GUISettingForJFrame() {

        this.setSize(800,800);
    }

    private void InitializeActions() {

        fullname_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                fullName = fullname_text.getText();
            }
        });

        phone_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                phone = phone_text.getText();
            }
        });

        id_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                getOKButton().setEnabled(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                id = id_text.getText();
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

    public JPanel getjPanelMain() {
        return jPanelMain;
    }

    public JPanel getjPanelTopFields() {
        return jPanelTopFields;
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

    public JButton getOKButton() {
        return OKButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getRegister() {
        return register;
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public SpringLayout getSpringLayoutPanels() {
        return springLayoutPanels;
    }

    public JFrame getjFramePrev() {
        return jFramePrev;
    }

    public JFrame getTwoBackFrame() {
        return twoBackFrame;
    }

    public static void main(String[] args) throws IOException {
        JFrame oneBackFrame = null, twoBackFrame = null;
        Register reg = new Register(oneBackFrame, twoBackFrame);
        reg.DrawRegister();
        reg.setVisible(true);
        reg.setLocationRelativeTo(null);
    }
}












