package Gui;

import Classes.Employee;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainMenuAdmin extends MainMenuCashier {

    public static final String FRAME_NAME = "SHIFT MANAGER";

    private SpringLayout springLayout;
    private JButton jButtonReport, jButtonEmployee;
    private JPanel jPanelRight;


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
        
        getJFrame().setEnabled(false);
        EmployeeListManagement employeeListManagement = new EmployeeListManagement(getJFrame(),getEmployee().getEmpBranch());

        try {
            employeeListManagement.DrawEmployee();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        
        employeeListManagement.InitializeActions();
        employeeListManagement.setVisible(true);
        employeeListManagement.toFront();
        employeeListManagement.setLocationRelativeTo(null);
    }

    private void DrawTheReportsMenu() {

        getJFrame().setEnabled(false);
        Reports report = new Reports(getJFrame(),getEmployee().getEmpBranch());
        report.DrawReports();
        report.InitializeActions();
        report.setVisible(true);
        report.toFront();
        report.setLocationRelativeTo(null);
    }

    private void DrawTheCustomerMenu() {

        CustomerListManagement customers = new CustomerListManagement(getJFrame(),getEmployee().getEmpBranch());
        setEnabled(false); // Disable edit this window when you open the next window

        try {

            customers.DrawCustomer();

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }


        customers.InitializeActions();
        customers.setVisible(true);
        customers.setLocationRelativeTo(null);
    }

    @Override
    protected void DrawMainMenu() {

        super.DrawMainMenu();

        setTitle(FRAME_NAME);

        GUISettingForJPanel();
    }

    private void GUISettingForJPanel() {

        GUIDefineLayoutAndAddComponentsOnJPanel();
        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout.putConstraint(SpringLayout.WEST,jButtonReport,10,SpringLayout.WEST,jPanelRight);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonReport,25,SpringLayout.NORTH,jPanelRight);

        springLayout.putConstraint(SpringLayout.WEST,jButtonEmployee,10,SpringLayout.WEST,jPanelRight);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonEmployee,25,SpringLayout.NORTH,jButtonReport);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelRight = new JPanel();

        springLayout = new SpringLayout();
        jPanelRight.setLayout(springLayout);
        jPanelRight.setPreferredSize(new Dimension(100, 400));

        jButtonReport = new JButton("Reports");
        jButtonEmployee = new JButton("Employees Management");

        jPanelRight.add(jButtonReport);
        jPanelRight.add(jButtonEmployee);

        getjPanelMain().add(jPanelRight);
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


