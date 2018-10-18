package Gui;

import Classes.Employee;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainMenuCashier extends MainMenuSeller {

    public static final String BUTTON_CUSTOMERS = "/src/images/customers.png";
    public static final String BUTTON_SELL_ITEM = "/src/images/sell_item.png";
    public static final int CUSTOMER_BUTTON_WIDTH = 350;
    public static final int CUSTOMER_BUTTON_HEIGHT = 80;
    public static final int SELL_BUTTON_WIDTH = 280;
    public static final int SELL_BUTTON_HEIGHT = 80;
    public static final int MID_PANELֹֹ_WIDTH_ֹSIZE = 420;
    public static final int MID_PANELֹֹ_HEIGHT_ֹSIZE = 400;

    private JPanel jPanelLeft;
    private SpringLayout springLayout;
    private JButton jButtonCustomer, jButtonSell;
    private ImageIcon customerLogoJPG, sellLogoJPG;


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

    private void DrawTheSellMenu() throws IOException {

        ItemsPurchaseCatalog catalog = new ItemsPurchaseCatalog(getJFrame(),getEmployee().getEmpBranch(),getEmployee().getEmpType());
        getJFrame().setEnabled(false);
        catalog.setUndecorated(true);
        catalog.DrawCatalog();
        catalog.InitializeActions();
        catalog.setVisible(true);
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

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelLeft = new JPanel();
        jPanelLeft.setBackground(Color.black);

        springLayout = new SpringLayout();
        jPanelLeft.setLayout(springLayout);
        jPanelLeft.setPreferredSize(new Dimension(MID_PANELֹֹ_WIDTH_ֹSIZE, MID_PANELֹֹ_HEIGHT_ֹSIZE));

        getjLabelBackLeftPhoto().setVisible(false);

        SetButtonsOnLeftPanel();

        getSpringLayoutPanels().putConstraint(SpringLayout.SOUTH, jPanelLeft, 0, SpringLayout.SOUTH, getjPanelMain());
        getSpringLayoutPanels().putConstraint(SpringLayout.EAST, jPanelLeft, -5, SpringLayout.WEST, getjPanelMid());
    }

    private void SetButtonsOnLeftPanel() {

        customerLogoJPG = new ImageIcon(getClass().getResource(BUTTON_CUSTOMERS));
        jButtonCustomer = new JButton(customerLogoJPG);
        jButtonCustomer.setBorderPainted(true);
        jButtonCustomer.setBackground(Color.white);
        jButtonCustomer.setPreferredSize(new Dimension(CUSTOMER_BUTTON_WIDTH, CUSTOMER_BUTTON_HEIGHT));
        jButtonCustomer.setBorder(new LineBorder(Color.red));
        jPanelLeft.add(jButtonCustomer);


        sellLogoJPG = new ImageIcon(getClass().getResource(BUTTON_SELL_ITEM));
        jButtonSell = new JButton(sellLogoJPG);
        jButtonSell.setBorderPainted(true);
        jButtonSell.setBackground(Color.BLACK);
        jButtonSell.setPreferredSize(new Dimension(SELL_BUTTON_WIDTH, SELL_BUTTON_HEIGHT));
        jButtonSell.setBorder(new LineBorder(Color.red));
        jPanelLeft.add(jButtonSell);
        getjPanelMain().add(jPanelLeft);

    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout.putConstraint(SpringLayout.WEST,jButtonCustomer,65,SpringLayout.WEST,jPanelLeft);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonCustomer,150,SpringLayout.NORTH,jPanelLeft);

        springLayout.putConstraint(SpringLayout.WEST,jButtonSell,105,SpringLayout.WEST,jPanelLeft);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonSell,50,SpringLayout.SOUTH,jButtonCustomer);
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
