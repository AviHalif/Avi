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

public class Storage extends JFrame {

    public static final String FRAME_NAME = "CASTRO - BRANCH STORAGE";
    public static final String COLUMN_TYPE = "Item Type";
    public static final String COLUMN_SIZE = "Item Size";
    public static final String COLUMN_BRANCH = "Item Branch";
    public static final String COLUMN_AMOUNT = "Item Amount";
    public static final String COLUMN_PRICE = "Price";
    public static final String COLUMN_PART_NUMBER = "Part Number";
    public static final String BUTTON_BACK_NAME = "BACK";

    public static final int TABLE_PANELֹֹ_WIDTH_ֹSIZE = 700;
    public static final int TABLE_PANELֹֹ_HEIGHT_ֹSIZE = 800;
    public static final int FRAMEֹֹ_WIDTH_ֹSIZE = 800;
    public static final int FRAMEֹֹ_HEIGHT_ֹSIZE = 800;

    private String branchName;
    private Vector<String> columnNames;
    private Client client;

    private JSONObject jsonObject;
    private JSONArray jsonObjectResponseArray;
    private JSONParser jsonParser;

    private JPanel jPanelMain;
    private JPanel jPanelTable;
    private JTable table;
    private JButton jButtonBack;


    public Storage(String branchName){

        client = new Client();

        SetObjectsComponents(branchName);
        SetGUIComponents();
    }

    private void SetObjectsComponents(String branchName) {

        this.branchName = branchName;
    }

    private void SetGUIComponents() {

        jPanelMain = new JPanel();
        jPanelTable = new JPanel();
        jPanelTable.setPreferredSize(new Dimension(TABLE_PANELֹֹ_WIDTH_ֹSIZE, TABLE_PANELֹֹ_HEIGHT_ֹSIZE));

        jButtonBack = new JButton(BUTTON_BACK_NAME);
    }

    private void InitializeActions(JFrame prevFrame) {

        jButtonBack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getJFrame().setVisible(false);
                prevFrame.setEnabled(true);
                prevFrame.toFront();
            }
        });


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                prevFrame.setEnabled(true);
                prevFrame.toFront();
            }
        });

    }

    protected void DrawStorage(JFrame prevFrame) throws IOException, ParseException {

        GUISettingForJFrame();

        GUISettingForJPanel();

        PrepareAndSendJsonStorageDataToServer();

        Vector<Vector<String>> dataList = GetStorageResponseFromServer();

        client.getSslSocket().close();

        DrawTheStorageTable(dataList);

        InitializeActions(prevFrame);
    }

    private void GUISettingForJPanel() {

        jPanelMain.add(jButtonBack);
        jPanelMain.add(jPanelTable);
    }

    private void DrawTheStorageTable(Vector<Vector<String>> dataList) {

        columnNames = new Vector<>();
        columnNames.add(COLUMN_TYPE);
        columnNames.add(COLUMN_SIZE);
        columnNames.add(COLUMN_BRANCH);
        columnNames.add(COLUMN_AMOUNT);
        columnNames.add(COLUMN_PRICE);
        columnNames.add(COLUMN_PART_NUMBER);

        // Insert the Strings list into the table with the columns name
        table = new JTable(dataList, columnNames);
        JScrollPane tableContainer = new JScrollPane(table);
        jPanelTable.add(tableContainer, BorderLayout.CENTER);

        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false); // Disable moving columns position
        table.setEnabled(false);
    }

    private Vector<Vector<String>> SeparateJsonArrayToSinglesAndConvertToStrings() {

        // Separate JsonArray to singles Jsons, convert to Strings and insert into the list
        Vector<Vector<String>> dataList = new Vector<>();

        int jsonArraySize = jsonObjectResponseArray.size();
        int i = 0;

        while (i < jsonArraySize) {
            Vector<String> data = new Vector<>();

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("item_type"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("item_size"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("item_branch"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("item_amount"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("item_price"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("item_part_number"));


            dataList.add(data);
        }
        return dataList;
    }

    private void GUISettingForJFrame() {

        this.setTitle(FRAME_NAME);
        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE,FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setContentPane(jPanelMain);
    }

    private Vector<Vector<String>> GetStorageResponseFromServer() throws IOException, ParseException {

        String line = client.getInputStream().readUTF();

        jsonParser = new JSONParser();
        jsonObjectResponseArray = (JSONArray) jsonParser.parse(line);

        Vector<Vector<String>> dataList = SeparateJsonArrayToSinglesAndConvertToStrings();

        return dataList;
    }

    private void PrepareAndSendJsonStorageDataToServer() throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "Storage");
        jsonObject.put("branch", branchName);

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    public JFrame getJFrame() {
        return this;
    }

    public JPanel getjPanelMain() {
        return jPanelMain;
    }

    public static void main(String[] args) throws IOException, ParseException {

        JFrame prevFrame = null;
        Storage storage = new Storage("Tel Aviv");
        storage.DrawStorage(prevFrame);
        storage.setVisible(true);
        storage.setLocationRelativeTo(null);
    }

}



