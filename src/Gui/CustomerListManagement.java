package Gui;

import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class CustomerListManagement extends CustomerList {

    private ImageIcon addLogoJPG, updateLogoJPG;
    private JButton jButtonAddCustom, jButtonManageCustom;
    public static final int BUTTON_WIDTH = 390, BUTTON_HEIGHT = 50;
    public static final String ADD_CUSTOMER_BUTTON = "/src/images/add_new_cust.png", UPDATE_CUSTOMER_BUTTON = "/src/images/update_cust.png";


    public CustomerListManagement(JFrame oneBackFrame, String branchName) {

        super(oneBackFrame, branchName);
    }

    @Override
    protected void DrawCustomer() throws IOException, ParseException {

        GUIDefineLayoutAndAddComponentsOnJPanel();

        super.DrawCustomer();

        GUISettingForJFrame();

        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

        getSpringLayout().putConstraint(SpringLayout.WEST, jButtonAddCustom, 320, SpringLayout.WEST, getjPanelMain());
        getSpringLayout().putConstraint(SpringLayout.SOUTH, jButtonAddCustom, -70, SpringLayout.SOUTH, getjPanelMain());

        getSpringLayout().putConstraint(SpringLayout.WEST, jButtonManageCustom, 100, SpringLayout.EAST, jButtonAddCustom);
        getSpringLayout().putConstraint(SpringLayout.SOUTH, jButtonManageCustom, -70, SpringLayout.SOUTH,  getjPanelMain());
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        SetButtonsOnPanel();
        getjPanelMain().add(jButtonAddCustom);
        getjPanelMain().add(jButtonManageCustom);
    }

    private void SetButtonsOnPanel() {

        addLogoJPG = new ImageIcon(getClass().getResource(ADD_CUSTOMER_BUTTON));
        jButtonAddCustom = new JButton(addLogoJPG);
        jButtonAddCustom.setBorderPainted(true);
        jButtonAddCustom.setBackground(Color.BLACK);
        jButtonAddCustom.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        jButtonAddCustom.setBorder(new LineBorder(Color.red));

        updateLogoJPG = new ImageIcon(getClass().getResource(UPDATE_CUSTOMER_BUTTON));
        jButtonManageCustom = new JButton(updateLogoJPG);
        jButtonManageCustom.setBorderPainted(true);
        jButtonManageCustom.setBackground(Color.white);
        jButtonManageCustom.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        jButtonManageCustom.setBorder(new LineBorder(Color.red));
    }

    @Override
    protected void InitializeActions() {

        super.InitializeActions();

        jButtonAddCustom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                RegisterCustomer customers = null;
                try {
                    customers = new RegisterCustomer(getJFrame(), getjFramePrev(), true, getBranchName());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                getJFrame().setEnabled(false); // Disable edit the previous window
                customers.setUndecorated(true);
                customers.DrawRegister();
                customers.InitializeActions();
                customers.setVisible(true);
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
        customers.setUndecorated(true);
        customers.DrawCustomer();
        customers.InitializeActions();
        customers.setVisible(true);
    }
}











