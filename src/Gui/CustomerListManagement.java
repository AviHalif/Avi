package Gui;

import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class CustomerListManagement extends CustomerList {

    private JPanel jPanelManage;
    private SpringLayout springLayout;
    private JButton jButtonAddCustom, jButtonManageCustom;


    public CustomerListManagement(JFrame oneBackFrame, String branchName) { // הגיע לכאן(=המסך ניהול עובדים הראשי) המסך הראשי של האדמין

        super(oneBackFrame, branchName);
    }

    @Override
    protected void DrawCustomer() throws IOException, ParseException {

        super.DrawCustomer();

        GUISettingForJFrame();

        GUISettingForJPanel();
    }

    private void GUISettingForJFrame() {

        setTitle("CustomerList Management");
    }

    private void GUISettingForJPanel() {

        GUIDefineLayoutAndAddComponentsOnJPanel();

        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout.putConstraint(SpringLayout.WEST, jButtonAddCustom, 10, SpringLayout.WEST, jPanelManage);
        springLayout.putConstraint(SpringLayout.NORTH, jButtonAddCustom, 25, SpringLayout.NORTH, jPanelManage);
        springLayout.putConstraint(SpringLayout.WEST, jButtonManageCustom, 10, SpringLayout.EAST, jButtonAddCustom);
        springLayout.putConstraint(SpringLayout.NORTH, jButtonManageCustom, 25, SpringLayout.NORTH, jPanelManage);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelManage = new JPanel();

        springLayout = new SpringLayout();
        jPanelManage.setLayout(springLayout);
        jPanelManage.setPreferredSize(new Dimension(700, 250));

        jButtonAddCustom = new JButton("ADD CUSTOMER");
        jButtonManageCustom = new JButton("EDIT CUSTOMER");

        jPanelManage.add(jButtonAddCustom);
        jPanelManage.add(jButtonManageCustom);

        getjPanelMain().add(jPanelManage);
    }

    @Override
    protected void InitializeActions() {

        super.InitializeActions();

        jButtonAddCustom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                RegisterCustomer customers = null; // שולח למסך הרישום את המסך הראשי של האדמין ואת המסך של הניהול עובדים
                try {
                    customers = new RegisterCustomer(getJFrame(), getjFramePrev(), true, getBranchName());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                getJFrame().setEnabled(false); // Disable edit the previous window
                customers.DrawRegister();
                customers.InitializeActions();
                customers.setVisible(true);
                customers.setLocationRelativeTo(null);
            }
        });

        jButtonManageCustom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                CustomerEdit customers = new CustomerEdit(getJFrame(), getjFramePrev(), getBranchName()); // שולח למסך הרישום את  המסך הראשי של האדמין ואת המסך של הטבלה
                getJFrame().setEnabled(false); // Disable edit the previous window
                customers.DrawCustomerEdit();
                customers.setVisible(true);
                customers.setLocationRelativeTo(null);
            }
        });
    }

    @Override
    public JFrame getJFrame() {
        return this;
    }

    public static void main(String[] args) throws IOException, ParseException {
        CustomerListManagement customers = new CustomerListManagement(null, null);
        customers.DrawCustomer();
        customers.InitializeActions();
        customers.setVisible(true);
        customers.setLocationRelativeTo(null);
    }
}











