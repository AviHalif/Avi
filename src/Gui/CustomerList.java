package Gui;

import Client.Client;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Vector;

public class CustomerList extends JFrame{

    public static final String BUTTON_BACK_NAME = "BACK";

    private String branchName;
    private Client client;

    private JSONObject jsonObject;
    private JSONArray jsonObjectResponseArray;
    private JSONParser jsonParser;

    private JFrame jFramePrev;
    private JPanel jPanelMain, jPanelTable;
    private JTable table;
    private JButton jButtonBack;

    public CustomerList(JFrame oneBackFrame, String branchName)  { //  הגיע לכאן(=המסך ניהול עובדים הראשי) המסך הראשי של האדמין או הקופאי

        SetObjectsComponents(branchName);
        SetGUIComponents(oneBackFrame);

        client = new Client();
    }

    private void SetGUIComponents(JFrame oneBackFrame) {

        this.jFramePrev = oneBackFrame;
    }

    private void SetObjectsComponents(String branchName) {

        this.branchName = branchName;
    }

    protected void DrawCustomer() throws IOException, ParseException {

        PrepareAndSendJsonCustomersListDataToServer();

        Vector<Vector<String>> dataList = GetCustomersListResponseFromServer();

        DrawTheCustomersTable(dataList);

        client.getSslSocket().close();

        }

    private void DrawTheCustomersTable(Vector<Vector<String>> dataList) {

        GUISettingForJFrame();

        GUISettingForJPanel();

        GUISettingForJTable(dataList);

        this.setContentPane(jPanelMain);
    }

    private void GUISettingForJTable(Vector<Vector<String>> dataList) {

        Vector<String> columnNames = new Vector<>();
        columnNames.add("CustomerList ID");
        columnNames.add("CustomerList Name");
        columnNames.add("CustomerList Phone");
        columnNames.add("CustomerList Type");

        jPanelTable = new JPanel();
        jPanelTable.setPreferredSize(new Dimension(700, 500));

        table = new JTable(dataList, columnNames);
        JScrollPane tableContainer = new JScrollPane(table);

        jPanelTable.add(tableContainer, BorderLayout.CENTER);
        jPanelMain.add(jPanelTable);
        jPanelMain.add(jButtonBack);

        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false); // Disable moving columns position
        table.setEnabled(false);
    }

    private void GUISettingForJPanel() {

        jPanelMain = new JPanel();
        jButtonBack = new JButton(BUTTON_BACK_NAME);
    }

    private void GUISettingForJFrame() {

        this.setTitle("CASTRO - CUSTOMERS LIST");
        this.setSize(800,800);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    protected void InitializeActions() {

        getjButtonBack().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getJFrame().setVisible(false);
                getjFramePrev().setEnabled(true);
                getjFramePrev().toFront();
            }
        });


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                getjFramePrev().setEnabled(true);
                getjFramePrev().toFront();
            }
        });
    }

    private Vector<Vector<String>> GetCustomersListResponseFromServer() throws ParseException, IOException {

        String line = client.getInputStream().readUTF();
        jsonParser = new JSONParser();
        jsonObjectResponseArray = (JSONArray) jsonParser.parse(line);

        Vector<Vector<String>> dataList = SeparateJsonArrayToSinglesAndConvertToStrings();

        return dataList;
    }

    private Vector<Vector<String>> SeparateJsonArrayToSinglesAndConvertToStrings() {

        // Separate JsonArray to singles Jsons, convert to Strings and insert into the list
        Vector<Vector<String>> dataList = new Vector<>();

        int jsonArraySize = jsonObjectResponseArray.size();
        int i = 0;

        while (i < jsonArraySize) {
            Vector<String> data = new Vector<>();
            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("cust_id"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("cust_name"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("cust_tel"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("cust_type"));

            dataList.add(data);
        }
        return dataList;
    }

    private void PrepareAndSendJsonCustomersListDataToServer() throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "Customer List");
        jsonObject.put("branch", branchName);

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();

    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public JFrame getJFrame() {
        return this;
    }

    public JFrame getjFramePrev() {
        return this.jFramePrev;
    }

    public JButton getjButtonBack() {
        return jButtonBack;
    }

    public JPanel getjPanelMain() {
        return jPanelMain;
    }

    public static void main(String[] args) throws IOException, ParseException {

        JFrame prevFrame = null;
        CustomerList customer = new CustomerList(prevFrame,"Tel Aviv");
        customer.DrawCustomer();
        customer.setVisible(true);
        customer.setLocationRelativeTo(null);
    }
}