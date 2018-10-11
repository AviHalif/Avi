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

public class EmployeeList extends JFrame {

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

    public EmployeeList(JFrame oneBackFrame, String branchName){ //  הגיע לכאן(=המסך ניהול עובדים הראשי) המסך הראשי של האדמין או הקופאי

        this.jFramePrev = oneBackFrame;
        this.branchName = branchName;

        client = new Client();
    }

    protected void DrawEmployee() throws IOException, ParseException {

        PrepareAndSendJsonDataToServer();

        Vector<Vector<String>> dataList = GetResponseFromServer();

        DrawTheEmployeesTable(dataList);
    }

    private void DrawTheEmployeesTable(Vector<Vector<String>> dataList) {

        Vector<String> columnNames = new Vector<>();
        columnNames.add("SN");
        columnNames.add("Name");
        columnNames.add("ID");
        columnNames.add("PHONE");
        columnNames.add("BANK");
        columnNames.add("BRANCH");

        this.setTitle("Employees list");
        this.setSize(800,800);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        jButtonBack = new JButton(BUTTON_BACK_NAME);

        jPanelMain = new JPanel();
        jPanelTable = new JPanel();

        jPanelTable.setPreferredSize(new Dimension(700, 500));

        this.setContentPane(jPanelMain);
        jPanelMain.add(jPanelTable);
        jPanelMain.add(jButtonBack);

        table = new JTable(dataList, columnNames);
        JScrollPane tableContainer = new JScrollPane(table);
        jPanelTable.add(tableContainer, BorderLayout.CENTER);
        jPanelMain.add(jPanelTable);
        this.setContentPane(jPanelMain);

        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false); // Disable moving columns position
        table.setEnabled(false);
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

    private Vector<Vector<String>> GetResponseFromServer() throws ParseException, IOException {


        String line = client.getInputStream().readUTF();
        jsonParser = new JSONParser();
        jsonObjectResponseArray = (JSONArray) jsonParser.parse(line);

        client.getSslSocket().close();

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
            data.add((String)jsonObject.get("emp_sn"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("emp_name"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("emp_id"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("emp_tel"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("emp_bank"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("emp_type"));

            dataList.add(data);
        }
        return dataList;
    }

    private void PrepareAndSendJsonDataToServer() throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "EmployeeList");
        jsonObject.put("branch", branchName);

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    public JFrame getJFrame() {
        return this;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONParser getJsonParser() {
        return jsonParser;
    }

    public void setJsonParser(JSONParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    public JPanel getjPanelMain() {
        return jPanelMain;
    }

    public JButton getjButtonBack() {
        return jButtonBack;
    }

    public JFrame getjFramePrev() {
        return jFramePrev;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

}
