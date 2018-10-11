package Gui;

import Client.Client;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class CustomerEdit extends JFrame {

    private String id = "", custId = "", custName = "", custTel = "", custType = "", branchName;

    private Client client;
    private JSONObject jsonObject;

    private JFrame jFramePrev, twoBackFrame;
    private JPanel jPanelMain;
    private JLabel label_message;
    private SpringLayout springLayout;
    private JTextField idCustomer_text;
    private JButton ok_button, cancel_button;

    public CustomerEdit(JFrame oneBackFrame, JFrame twoBackFrame, String branchName) {// הגיע לכאן המסך הראשי של האדמין ואת המסך של הניהול עובדים

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

                            getJFrame().setVisible(false);// העלמת המסך הקטן של הת.ז

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

        CustomerEditDetails CustomerEditDetails = new CustomerEditDetails(custId, custName, custTel,custType,
                                                                          jFramePrev,twoBackFrame,getJFrame(),branchName);
        // שולח את החלונית הקטנה להזנת ת.ז לעדכון, המסך הראשי של האדמין ואת המסך של הניהול עובדים
        CustomerEditDetails.DrawCustomerEditDetails();
        CustomerEditDetails.InitializeActions();
        CustomerEditDetails.setVisible(true);
        CustomerEditDetails.setLocationRelativeTo(null);
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

    protected void DrawCustomerEdit() { // מסך ראשי של אדמין,המסך עם הטבלה

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

        springLayout.putConstraint(SpringLayout.WEST, label_message, 35, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, label_message, 25, SpringLayout.NORTH, jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST, idCustomer_text, 70, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, idCustomer_text, 25, SpringLayout.SOUTH, label_message);

        springLayout.putConstraint(SpringLayout.WEST, ok_button, 55, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, ok_button, 25, SpringLayout.SOUTH, idCustomer_text);

        springLayout.putConstraint(SpringLayout.WEST, cancel_button, 35, SpringLayout.EAST, ok_button);
        springLayout.putConstraint(SpringLayout.NORTH, cancel_button, 25, SpringLayout.SOUTH, idCustomer_text);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelMain = new JPanel();

        springLayout = new SpringLayout();
        jPanelMain.setLayout(springLayout);

        label_message = new JLabel("Please insert customer ID to edit");
        idCustomer_text = new JTextField(12);

        ok_button = new JButton("OK");
        cancel_button = new JButton("CANCEL");

        jPanelMain.add(label_message);
        jPanelMain.add(idCustomer_text);
        jPanelMain.add(ok_button);
        jPanelMain.add(cancel_button);
    }

    private void GUISettingForJFrame() {

        this.setTitle("Customer Editing");
        this.setSize(300, 200);
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

        CustomerEdit customers = new CustomerEdit(null, null, null); // שולח למסך הרישום את  המסך הראשי של האדמין ואת המסך של הטבלה
        customers.DrawCustomerEdit();
        customers.setVisible(true);
        customers.setLocationRelativeTo(null);
    }
}
