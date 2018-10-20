package Gui;

import Client.Client;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Collections;

public class CustomerEdit extends JFrame {

    private Client client;
    private JSONObject jsonObject;
    private JFrame jFramePrev, twoBackFrame;
    private JPanel jPanelMain;
    private SpringLayout springLayout;
    private JTextField idCustomer_text;
    private JButton ok_button, cancel_button;
    private JLabel backgroundPhotoLabel, titleLabel;
    private ImageIcon backgroundPhotoTopPanelJPG, titlePhotoJPG, backLogoJPG, okLogoJPG;
    private String id = "", custId = "", custName = "", custTel = "", custType = "", branchName;
    public static final String BACK_PHOTO = "/src/images/back_edit_id_2.png", CASTRO_ICON = "/src/images/icon.png", TITLE = "/src/images/please_enter_1.png",
                               BACK_BUTTON = "/src/images/back.png", OK = "/src/images/ok.png";
    public static final int PANELֹֹ_WIDTH_ֹSIZE = 800, PANELֹֹ_HEIGHT_ֹSIZE = 400, FRAMEֹֹ_WIDTH_ֹSIZE = 800, FRAMEֹֹ_HEIGHT_ֹSIZE = 400, BUTTON_WIDTH = 100, BUTTON_HEIGHT = 60,
                            JTEXTFIELD_WIDTH = 250, JTEXTFIELD_HEIGHT = 50;


    public CustomerEdit(JFrame oneBackFrame, JFrame twoBackFrame, String branchName) {

        SetGUIComponents(oneBackFrame,twoBackFrame);
        SetObjectsComponents(branchName);

    }

    private void SetObjectsComponents(String branchName) {

        this.branchName = branchName;
    }

    private void SetGUIComponents(JFrame oneBackFrame, JFrame twoBackFrame) {

        this.jFramePrev =  oneBackFrame;
        this.twoBackFrame = twoBackFrame;
    }

    protected void InitializeActions() {

        idCustomer_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                id = idCustomer_text.getText();
            }
        });

        getOk_button().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (CheckCustomerId(id)) {
                    try {
                        client = new Client();
                        PrepareAndSendJsonDataToServer();

                        String response = GetResponseCustomerDetailsFromServer();

                        if (!response.equals("")) {

                            JOptionPane.showMessageDialog(null, response);
                            idCustomer_text.setText("");

                            client.getSslSocket().close();

                        } else {

                            getJFrame().setVisible(false);
                            DrawCustomerEditDetailsMenu();
                            client.getSslSocket().close();

                        }

                    } catch (Exception ex) {
                        System.out.print(ex);
                    }
                }
            }
        });

        cancel_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                getJFrame().setVisible(false);
                jFramePrev.setEnabled(true);
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

    private void DrawCustomerEditDetailsMenu() {

        CustomerEditDetails CustomerEditDetails = new CustomerEditDetails(custId, custName, custTel,custType, jFramePrev,twoBackFrame,getJFrame(),branchName);
        CustomerEditDetails.setUndecorated(true);
        CustomerEditDetails.DrawCustomerEditDetails();
        CustomerEditDetails.InitializeActions();
        CustomerEditDetails.setVisible(true);
    }

    private String GetResponseCustomerDetailsFromServer(){

        String response="";

        try {
            String line = client.getInputStream().readUTF();
            JSONObject jsonObjectResponse;
            JSONParser jsonParser = new JSONParser();
            jsonObjectResponse = (JSONObject) jsonParser.parse(line);
            response = (String) jsonObjectResponse.get("Failed");
            custId = (String) jsonObjectResponse.get("customer id");
            custName = (String) jsonObjectResponse.get("customer name");
            custTel = (String) jsonObjectResponse.get("customer tel");
            custType = (String) jsonObjectResponse.get("customer type");
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }

        return response;
    }

    private void PrepareAndSendJsonDataToServer() throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "Is customer in DB");
        jsonObject.put("customerId", id);

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    protected void DrawCustomerEdit() {

        GUISettingForJFrame();

        GUISettingForJPanel();

        InitializeActions();

        this.setContentPane(jPanelMain);
    }

    private void GUISettingForJPanel() {

        GUIDefineLayoutAndAddComponentsOnJPanel();

        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout.putConstraint(SpringLayout.WEST, titleLabel, 35, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, titleLabel, 50, SpringLayout.NORTH, jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST, idCustomer_text, 260, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, idCustomer_text, 35, SpringLayout.SOUTH, titleLabel);

        springLayout.putConstraint(SpringLayout.WEST, ok_button, 330, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, ok_button, 50, SpringLayout.SOUTH, idCustomer_text);

        springLayout.putConstraint(SpringLayout.WEST, cancel_button, 0, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.SOUTH, cancel_button, 0, SpringLayout.SOUTH, jPanelMain);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelMain = new JPanel();

        springLayout = new SpringLayout();
        jPanelMain.setLayout(springLayout);

        jPanelMain.setPreferredSize(new Dimension(PANELֹֹ_WIDTH_ֹSIZE, PANELֹֹ_HEIGHT_ֹSIZE));

        titlePhotoJPG = new ImageIcon(getClass().getResource(TITLE));
        titleLabel = new JLabel(titlePhotoJPG);

        backgroundPhotoTopPanelJPG = new ImageIcon(getClass().getResource(BACK_PHOTO));
        backgroundPhotoLabel = new JLabel(backgroundPhotoTopPanelJPG);

        backLogoJPG = new ImageIcon(getClass().getResource(BACK_BUTTON));
        cancel_button = new JButton(backLogoJPG);
        cancel_button.setBorderPainted(false);
        cancel_button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.black, Color.black));

        okLogoJPG = new ImageIcon(getClass().getResource(OK));
        ok_button = new JButton(okLogoJPG);
        ok_button.setBorderPainted(true);
        ok_button.setBackground(Color.white);
        ok_button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        ok_button.setBorder(new LineBorder(Color.red));

        idCustomer_text = new JTextField();
        idCustomer_text.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        idCustomer_text.setHorizontalAlignment(JTextField.CENTER);
        idCustomer_text.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        idCustomer_text.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        idCustomer_text.setForeground (Color.black);
        idCustomer_text.setOpaque(false);

        jPanelMain.add(titleLabel);
        jPanelMain.add(idCustomer_text);
        jPanelMain.add(ok_button);
        jPanelMain.add(cancel_button);
        jPanelMain.add(backgroundPhotoLabel);
    }

    private void GUISettingForJFrame() {

        this.setTitle("CASTRO");
        this.setIconImages(Collections.singletonList(Toolkit.getDefaultToolkit().getImage(getClass().getResource(CASTRO_ICON))));
        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE, FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setResizable(false);
    }

    public JFrame getJFrame() {
        return this;
    }

    private static boolean CheckCustomerId(String  id){

        if (!(id.length() <= 9 && id.length()>=1)) {
            JOptionPane.showMessageDialog(null, "ID: Your Id length is incorrect");
            return false;
        }
        if (!(id.matches("[0-9]+"))) {
            JOptionPane.showMessageDialog(null, "ID: Please insert only digits");
            return false;
        }

        return true;
    }

    public JButton getOk_button() {
        return ok_button;
    }

    public JPanel getjPanelMain() {
        return jPanelMain;
    }

    public static void main(String[] args) {

        CustomerEdit customers = new CustomerEdit(null, null, null);
        customers.DrawCustomerEdit();
        customers.setVisible(true);
        customers.setLocationRelativeTo(null);
    }
}
