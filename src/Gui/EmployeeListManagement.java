package Gui;

import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class EmployeeListManagement extends EmployeeList {

    public static final String ADD_EMPLOYEE_BUTTON = "/src/images/add_new_emp.png";
    public static final String UPDATE_EMPLOYEE_BUTTON = "/src/images/update_emp.png";
    public static final int BUTTON_WIDTH = 390;
    public static final int BUTTON_HEIGHT = 50;


    private JButton jButtonAddEmployee, jButtonManageEmployee;
    private ImageIcon addLogoJPG, updateLogoJPG;

    public EmployeeListManagement(JFrame oneBackFrame, String branchName) { // הגיע לכאן(=המסך ניהול עובדים הראשי) המסך הראשי של האדמין

        super(oneBackFrame, branchName);
    }

    @Override
    protected void DrawEmployee() throws IOException, ParseException {

        GUIDefineLayoutAndAddComponentsOnJPanel();

        super.DrawEmployee();

        GUIPlaceComponentsOnJPanel();

        GUISettingForJFrame();
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        SetButtonsOnPanel();

        getjPanelMain().add(jButtonAddEmployee);
        getjPanelMain().add(jButtonManageEmployee);
    }


    private void GUIPlaceComponentsOnJPanel() {

        getSpringLayout().putConstraint(SpringLayout.WEST, jButtonAddEmployee, 350, SpringLayout.WEST, getjPanelMain());
        getSpringLayout().putConstraint(SpringLayout.NORTH, jButtonAddEmployee, -90, SpringLayout.SOUTH, getjPanelMain());
        getSpringLayout().putConstraint(SpringLayout.WEST, jButtonManageEmployee, 50, SpringLayout.EAST, jButtonAddEmployee);
        getSpringLayout().putConstraint(SpringLayout.NORTH, jButtonManageEmployee, -90, SpringLayout.SOUTH, getjPanelMain());
    }


    private void SetButtonsOnPanel() {

        addLogoJPG = new ImageIcon(getClass().getResource(ADD_EMPLOYEE_BUTTON));
        jButtonAddEmployee = new JButton(addLogoJPG);
        jButtonAddEmployee.setBorderPainted(true);
        jButtonAddEmployee.setBackground(Color.BLACK);
        jButtonAddEmployee.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        jButtonAddEmployee.setBorder(new LineBorder(Color.red));

        updateLogoJPG = new ImageIcon(getClass().getResource(UPDATE_EMPLOYEE_BUTTON));
        jButtonManageEmployee = new JButton(updateLogoJPG);
        jButtonManageEmployee.setBorderPainted(true);
        jButtonManageEmployee.setBackground(Color.white);
        jButtonManageEmployee.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        jButtonManageEmployee.setBorder(new LineBorder(Color.red));
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
                    employee.setUndecorated(true);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                getJFrame().setEnabled(false); // Disable edit the previous window
                employee.DrawRegister();
                employee.InitializeActions();
                employee.setVisible(true);
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


