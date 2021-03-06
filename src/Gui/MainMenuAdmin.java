package Gui;

import Classes.Employee;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainMenuAdmin extends MainMenuCashier {

    private JPanel jPanelRight;
    private SpringLayout springLayout;
    private JButton jButtonReport, jButtonEmployee;
    private ImageIcon  reportsLogoJPG, employeesLogoJPG;
    public static final String BUTTON_EMPLOYEES = "/src/images/employees.png", BUTTON_REPORTS = "/src/images/reports.png";
    public static final int RIGHT_PANELֹֹ_WIDTH_ֹSIZE = 420, RIGHT_PANELֹֹ_HEIGHT_ֹSIZE = 500, EMPLOYESS_BUTTON_WIDTH = 350, EMPLOYESS_BUTTON_HEIGHT = 80, REPORTS_BUTTON_WIDTH = 280,
                            REPORTS_BUTTON_HEIGHT = 80;

    public MainMenuAdmin(Employee employee) {
        super(employee);
    }

    @Override
    protected void InitializeActions() {

        super.InitializeActions();

        getJButtonCustomer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DrawTheCustomerMenu();

            }
        });

        jButtonReport.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                DrawTheReportsMenu();
            }
        });

        jButtonEmployee.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                DrawTheEmployeeMenu();
            }
        });
}

    private void DrawTheEmployeeMenu() {

        EmployeeListManagement employeeListManagement = new EmployeeListManagement(getJFrame(),getEmployee().getEmpBranch());
        getJFrame().setEnabled(false); // Disable edit this window when you open the next window
        employeeListManagement.setUndecorated(true);

        try {
            employeeListManagement.DrawEmployee();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        
        employeeListManagement.InitializeActions();
        employeeListManagement.setVisible(true);
    }

    private void DrawTheReportsMenu() {

        Reports report = new Reports(getJFrame(),getEmployee().getEmpBranch());
        getJFrame().setEnabled(false);
        report.setUndecorated(true);
        report.DrawReports();
        report.InitializeActions();
        report.setVisible(true);
    }

    private void DrawTheCustomerMenu() {

        CustomerListManagement customers = new CustomerListManagement(getJFrame(),getEmployee().getEmpBranch());
        setEnabled(false); // Disable edit this window when you open the next window
        customers.setUndecorated(true);

        try {

            customers.DrawCustomer();

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        customers.InitializeActions();
        customers.setVisible(true);
    }

    @Override
    protected void DrawMainMenu() {

        super.DrawMainMenu();

        GUISettingForJPanel();
    }

    private void GUISettingForJPanel() {

        GUIDefineLayoutAndAddComponentsOnJPanel();
        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout.putConstraint(SpringLayout.WEST,jButtonReport,63,SpringLayout.WEST,jPanelRight);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonReport,260,SpringLayout.NORTH,jPanelRight);

        springLayout.putConstraint(SpringLayout.WEST,jButtonEmployee,28,SpringLayout.WEST,jPanelRight);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonEmployee,40,SpringLayout.SOUTH,jButtonReport);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelRight = new JPanel();
        jPanelRight.setBackground(Color.black);

        springLayout = new SpringLayout();
        jPanelRight.setLayout(springLayout);
        jPanelRight.setPreferredSize(new Dimension(RIGHT_PANELֹֹ_WIDTH_ֹSIZE, RIGHT_PANELֹֹ_HEIGHT_ֹSIZE));

        getjLabelBackPhoto().setVisible(false);

        SetButtonsOnLeftPanel();

        getSpringLayoutPanels().putConstraint(SpringLayout.NORTH, jPanelRight, 200, SpringLayout.NORTH, getjPanelMain());
        getSpringLayoutPanels().putConstraint(SpringLayout.WEST, jPanelRight, 5, SpringLayout.EAST, getjPanelMid());

        getjPanelMain().add(jPanelRight);
    }

    private void SetButtonsOnLeftPanel() {

        reportsLogoJPG = new ImageIcon(getClass().getResource(BUTTON_REPORTS));
        jButtonReport = new JButton(reportsLogoJPG);
        jButtonReport.setBorderPainted(true);
        jButtonReport.setBackground(Color.white);
        jButtonReport.setPreferredSize(new Dimension(REPORTS_BUTTON_WIDTH, REPORTS_BUTTON_HEIGHT));
        jButtonReport.setBorder(new LineBorder(Color.red));
        jPanelRight.add(jButtonReport);

        employeesLogoJPG = new ImageIcon(getClass().getResource(BUTTON_EMPLOYEES));
        jButtonEmployee = new JButton(employeesLogoJPG);
        jButtonEmployee.setBorderPainted(true);
        jButtonEmployee.setBackground(Color.BLACK);
        jButtonEmployee.setPreferredSize(new Dimension(EMPLOYESS_BUTTON_WIDTH, EMPLOYESS_BUTTON_HEIGHT));
        jButtonEmployee.setBorder(new LineBorder(Color.red));
        jPanelRight.add(jButtonEmployee);
    }

    @Override
    public JFrame getJFrame() {
        return this;
    }

    public static void main(String[] args) {

        Employee employee = new Employee("0123456789", "sdfsd", "sdfsdf", "sdfsdf",
                "sdfsdf", "sdfsdf", "sdfsf", "sdfsdf");

        MainMenuAdmin mainMenuAdmin = new MainMenuAdmin(employee);
        mainMenuAdmin.DrawMainMenu();
        mainMenuAdmin.InitializeActions();
        mainMenuAdmin.setVisible(true);
        mainMenuAdmin.setLocationRelativeTo(null);
    }

}


