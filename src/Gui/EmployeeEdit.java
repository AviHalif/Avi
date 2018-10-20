package Gui;

import Classes.Employee;
import Client.Client;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Collections;

public class EmployeeEdit extends JFrame {


    private Client client;
    private Employee employee;
    private JPanel jPanelMain;
    private String branchName, id;
    private JSONObject jsonObject;
    private SpringLayout springLayout;
    private JTextField idEmployee_text;
    private JFrame jFramePrev, twoBackFrame;
    private JButton ok_button, cancel_button;
    private JLabel backgroundPhotoLabel, titleLabel;
    private ImageIcon backgroundPhotoTopPanelJPG, titlePhotoJPG, backLogoJPG, okLogoJPG;
    public static final String BACK_PHOTO = "/src/images/back_edit_id.png", CASTRO_ICON = "/src/images/icon.png", TITLE = "/src/images/please_enter_2.png",
                               BACK_BUTTON = "/src/images/back.png", OK = "/src/images/ok.png";
    public static final int PANELֹֹ_WIDTH_ֹSIZE = 800, PANELֹֹ_HEIGHT_ֹSIZE = 400, FRAMEֹֹ_WIDTH_ֹSIZE = 800, FRAMEֹֹ_HEIGHT_ֹSIZE = 400, BUTTON_WIDTH = 100, BUTTON_HEIGHT = 60,
                            JTEXTFIELD_WIDTH = 250, JTEXTFIELD_HEIGHT = 50;


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

        getJFrame().setVisible(false);
    }

    private void DrawEmployeeEditDetailsMenu() {

        EmployeeEditDetails employeeEditDetails = new EmployeeEditDetails(employee.getEmpId(),employee.getEmpSn(),employee.getEmpName(),
                employee.getEmpTel(),employee.getEmpBank(),employee.getEmpBranch(),
                employee.getEmpType(),employee.getEmpPhoto(),
                jFramePrev,twoBackFrame,getJFrame(),branchName);
        employeeEditDetails.setUndecorated(true);
        employeeEditDetails.DrawEmployeeEditDetails();
        employeeEditDetails.InitializeActions();
        employeeEditDetails.setVisible(true);
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

    protected void DrawEmployeeEdit() {

        GUISettingForJFrame();

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

        idEmployee_text = new JTextField();
        idEmployee_text.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        idEmployee_text.setHorizontalAlignment(JTextField.CENTER);
        idEmployee_text.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        idEmployee_text.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        idEmployee_text.setForeground (Color.black);
        idEmployee_text.setOpaque(false);

        GUIPlaceComponentsOnJPanel();

        jPanelMain.add(titleLabel);
        jPanelMain.add(idEmployee_text);
        jPanelMain.add(ok_button);
        jPanelMain.add(cancel_button);
        jPanelMain.add(backgroundPhotoLabel);

        this.setContentPane(jPanelMain);
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout.putConstraint(SpringLayout.WEST, titleLabel, 35, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, titleLabel, 50, SpringLayout.NORTH, jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST, idEmployee_text, 260, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, idEmployee_text, 35, SpringLayout.SOUTH, titleLabel);

        springLayout.putConstraint(SpringLayout.WEST, ok_button, 330, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH, ok_button, 50, SpringLayout.SOUTH, idEmployee_text);

        springLayout.putConstraint(SpringLayout.WEST, cancel_button, 0, SpringLayout.WEST, jPanelMain);
        springLayout.putConstraint(SpringLayout.SOUTH, cancel_button, 0, SpringLayout.SOUTH, jPanelMain);
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

        EmployeeEdit employeeEdit = new EmployeeEdit(null, null, "Tel Aviv");
        employeeEdit.setUndecorated(true);
        employeeEdit.DrawEmployeeEdit();
        employeeEdit.InitializeActions();
        employeeEdit.setVisible(true);
    }
}
