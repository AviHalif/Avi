package Gui;

import Classes.Employee;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainMenuCashier extends MainMenuSeller {

    public static final String FRAME_NAME = "CASHIER";

    private JPanel jPanelMid;
    private SpringLayout springLayout;
    private JButton jButtonCustomer, jButtonSell;


    public MainMenuCashier(Employee employee) {
         super(employee);
    }

    public JButton getJButtonCustomer() {
        return jButtonCustomer;
    }

    public JButton getJButtonSell() {
        return jButtonSell;
    }

    @Override
    protected void InitializeActions() {

        super.InitializeActions();

        getJButtonSell().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                try {
                    DrawTheSellMenu();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        if(!(this instanceof MainMenuAdmin)) { // תתייחס לפונקציה הזאת רק אם אתה לא ממופע של אדמין כי עבור אדמין יש לו כבר פונקציה שממשת מופע עבור אותו כפתור

            getJButtonCustomer().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    DrawTheCustomersMenu();

                }
            });
        }
    }

    private void DrawTheCustomersMenu() {

        CustomerList customers = new CustomerList(getJFrame(), getEmployee().getEmpBranch());

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

    private void DrawTheSellMenu() throws IOException {

        getJFrame().setEnabled(false);

        ItemsPurchaseCatalog catalog = new ItemsPurchaseCatalog(getJFrame(),getEmployee().getEmpBranch(),getEmployee().getEmpType());

        catalog.DrawCatalog();
        catalog.InitializeActions();
        catalog.setVisible(true);
        catalog.setLocationRelativeTo(null);
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

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelMid = new JPanel();

        springLayout = new SpringLayout();
        jPanelMid.setLayout(springLayout);
        jPanelMid.setPreferredSize(new Dimension(300, 400));

        jButtonCustomer = new JButton("Customers");
        jPanelMid.add(jButtonCustomer);

        jButtonSell = new JButton("Sell item");
        jPanelMid.add(jButtonSell);

        getjPanelMain().add(jPanelMid);
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout.putConstraint(SpringLayout.WEST,jButtonCustomer,10,SpringLayout.WEST,jPanelMid);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonCustomer,25,SpringLayout.NORTH,jPanelMid);

        springLayout.putConstraint(SpringLayout.WEST,jButtonSell,10,SpringLayout.WEST,jPanelMid);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonSell,25,SpringLayout.NORTH,jButtonCustomer);
    }

    @Override
    public JFrame getJFrame(){
        return this;
    }

    public static void main(String[] args) {

        Employee employee = new Employee("0123456789", "sdfsd", "sdfsdf", "sdfsdf",
                "sdfsdf", "sdfsdf", "sdfsf", "sdfsdf");

        MainMenuCashier mainMenuCashier = new MainMenuCashier(employee);
        mainMenuCashier.DrawMainMenu();
        mainMenuCashier.InitializeActions();
        mainMenuCashier.setVisible(true);
        mainMenuCashier.setLocationRelativeTo(null);
    }
}
