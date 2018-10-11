package Gui;

import Classes.Employee;
import Client.Client;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class EmployeeEdit extends JFrame {

    private Employee employee;
    private String branchName, id;
    private Client client;

    private JSONObject jsonObject;

    private JFrame jFramePrev, twoBackFrame;
    private JPanel jPanelMain;
    private JLabel label_message;
    private SpringLayout springLayout;
    private JTextField idEmployee_text;
    private JButton ok_button, cancel_button;


    public EmployeeEdit(JFrame oneBackFrame, JFrame twoBackFrame, String branchName){// הגיע לכאן המסך הראשי של האדמין ואת המסך של הניהול עובדים

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

        idEmployee_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                id = idEmployee_text.getText();
            }
        });

        getOk_button().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (CheckEmployeeId(id)) {
                    try {

                        client = new Client();

                        PrepareAndSendJsonDataToServer();

                        String response = GetResponseFromServer();

                        if (!response.equals("")) {

                            JOptionPane.showMessageDialog(null, response);
                            idEmployee_text.setText("");
                            client.getSslSocket().close();

                        } else {

                            client.getSslSocket().close();

                            CreateNewEmployeeWIthTheJsonData();

                            DrawEmployeeEditDetailsMenu();
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

    private void CreateNewEmployeeWIthTheJsonData() {

        employee = new Employee();
        employee.setEmpSn((String) jsonObject.get("employee sn"));
        employee.setEmpName((String) jsonObject.get("employee name"));
        employee.setEmpTel((String) jsonObject.get("employee tel"));
        employee.setEmpBank((String) jsonObject.get("employee bank"));
        employee.setEmpBranch((String) jsonObject.get("employee branch"));
        employee.setEmpType((String) jsonObject.get("employee type"));
        employee.setEmpPhoto((String) jsonObject.get("employee photo"));
        employee.setEmpId(id);

        getJFrame().setVisible(false);// העלמת המסך הקטן של הת.ז
    }

    private void DrawEmployeeEditDetailsMenu() {

        EmployeeEditDetails employeeEditDetails = new EmployeeEditDetails(employee.getEmpId(),employee.getEmpSn(),employee.getEmpName(),
                employee.getEmpTel(),employee.getEmpBank(),employee.getEmpBranch(),
                employee.getEmpType(),employee.getEmpPhoto(),
                jFramePrev,twoBackFrame,getJFrame(),branchName);
        employeeEditDetails.DrawEmployeeEditDetails();
        employeeEditDetails.InitializeActions();
        employeeEditDetails.setVisible(true);
        employeeEditDetails.setLocationRelativeTo(null);
        employeeEditDetails.resizePhotoInMenu();
    }

    private String GetResponseFromServer() throws IOException, ParseException {

        String line = client.getInputStream().readUTF();
        JSONParser jsonParser = new JSONParser();
        jsonObject = (JSONObject) jsonParser.parse(line);
        String response = (String) jsonObject.get("Failed");

        return response;
    }

    private void PrepareAndSendJsonDataToServer() throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "EmployeeEdit");
        jsonObject.put("employeeId", id);
        jsonObject.put("branch name", branchName);

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    protected void DrawEmployeeEdit() { // מסך ראשי של אדמין,המסך עם הטבלה

        this.setTitle("Employee Editing");
        this.setSize(300, 200);
        this.setResizable(false);

        jPanelMain = new JPanel();

        springLayout = new SpringLayout();
        jPanelMain.setLayout(springLayout);

        label_message = new JLabel("Please insert employee ID to edit");
        idEmployee_text = new JTextField(12);

        ok_button = new JButton("OK");
        cancel_button = new JButton("CANCEL");

        jPanelMain.add(label_message);
        jPanelMain.add(idEmployee_text);
        jPanelMain.add(ok_button);
        jPanelMain.add(cancel_button);

        springLayout.putConstraint(SpringLayout.WEST, label_message, 35, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, label_message, 25, SpringLayout.NORTH, jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST, idEmployee_text, 70, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, idEmployee_text, 25, SpringLayout.SOUTH, label_message);

        springLayout.putConstraint(SpringLayout.WEST, ok_button, 55, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, ok_button, 25, SpringLayout.SOUTH, idEmployee_text);

        springLayout.putConstraint(SpringLayout.WEST, cancel_button, 35, SpringLayout.EAST, ok_button);
        springLayout.putConstraint(SpringLayout.NORTH, cancel_button, 25, SpringLayout.SOUTH, idEmployee_text);

        this.setContentPane(jPanelMain);
    }

    public JFrame getJFrame() {
        return this;
    }

    private static boolean CheckEmployeeId(String  id){

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

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JButton getOk_button() {
        return ok_button;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public JPanel getjPanelMain() {
        return jPanelMain;
    }

    public static void main(String[] args) {

        EmployeeEdit customers = new EmployeeEdit(null, null, "Tel Aviv"); // שולח למסך הרישום את  המסך הראשי של האדמין ואת המסך של הטבלה
        customers.DrawEmployeeEdit();
        customers.InitializeActions();
        customers.setVisible(true);
        customers.setLocationRelativeTo(null);
    }
}
