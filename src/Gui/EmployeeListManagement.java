package Gui;

import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class EmployeeListManagement extends EmployeeList {

    private JPanel jPanelManage;
    private SpringLayout springLayout;
    private JButton jButtonAddEmployee, jButtonManageEmployee;

    public EmployeeListManagement(JFrame oneBackFrame, String branchName) { // הגיע לכאן(=המסך ניהול עובדים הראשי) המסך הראשי של האדמין

        super(oneBackFrame, branchName);
    }

    @Override
    protected void DrawEmployee() throws IOException, ParseException {

        super.DrawEmployee();

        GUISettingForJFrame();

        GUISettingForJPanel();
    }

    private void GUISettingForJPanel() {

        GUIDefineLayoutAndAddComponentsOnJPanel();

        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout.putConstraint(SpringLayout.WEST, jButtonAddEmployee, 10, SpringLayout.WEST, jPanelManage);
        springLayout.putConstraint(SpringLayout.NORTH, jButtonAddEmployee, 15, SpringLayout.NORTH, jPanelManage);
        springLayout.putConstraint(SpringLayout.WEST, jButtonManageEmployee, 10, SpringLayout.EAST, jButtonAddEmployee);
        springLayout.putConstraint(SpringLayout.NORTH, jButtonManageEmployee, 15, SpringLayout.NORTH, jPanelManage);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelManage = new JPanel();

        springLayout = new SpringLayout();
        jPanelManage.setLayout(springLayout);
        jPanelManage.setPreferredSize(new Dimension(700, 250));

        jButtonAddEmployee = new JButton("ADD EMPLOYEE");
        jButtonManageEmployee = new JButton("EDIT EMPLOYEE");

        jPanelManage.add(jButtonAddEmployee);
        jPanelManage.add(jButtonManageEmployee);

        getjPanelMain().add(jPanelManage);
    }

    private void GUISettingForJFrame() {

        setTitle("EmployeesList Management");
    }

    @Override
    protected void InitializeActions() {

        super.InitializeActions();

        jButtonAddEmployee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                RegisterEmployee employee = null; // שולח למסך הרישום את המסך הראשי של האדמין ואת המסך של הניהול עובדים
                try {
                    employee = new RegisterEmployee(getJFrame(), getjFramePrev(), getBranchName());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                getJFrame().setEnabled(false); // Disable edit the previous window
                employee.DrawRegister();
                employee.InitializeActions();
                employee.setVisible(true);
                employee.setLocationRelativeTo(null);
            }
        });

        jButtonManageEmployee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                EmployeeEdit customers = new EmployeeEdit(getJFrame(), getjFramePrev(), getBranchName()); // שולח למסך הרישום את  המסך הראשי של האדמין ואת המסך של הטבלה
                getJFrame().setEnabled(false); // Disable edit the previous window
                customers.DrawEmployeeEdit();
                customers.InitializeActions();
                customers.setVisible(true);
                customers.setLocationRelativeTo(null);
            }
        });
    }

        public static void main(String[] args) throws IOException, ParseException {

            EmployeeListManagement employeeListManagement = new EmployeeListManagement(null,"Tel Aviv");
            employeeListManagement.DrawEmployee();
            employeeListManagement.InitializeActions();
            employeeListManagement.setVisible(true);
            employeeListManagement.toFront();
            employeeListManagement.setLocationRelativeTo(null);
        }
    }


