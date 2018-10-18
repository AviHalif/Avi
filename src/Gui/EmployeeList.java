package Gui;

import Client.Client;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Vector;

public class EmployeeList extends JFrame {

    public static final String BACK_PHOTO = "/src/images/employees_back.png";
    public static final String BACK_BUTTON = "/src/images/back.png";
    public static final String TITLE_TEL_AVIV = "/src/images/employess_title_Tel_Aviv.png";
    public static final String TITLE_JERUSALEM = "/src/images/employess_title_Jerusalem.png";
    public static final int TABLE_COLUMN_SIZE = 600;
    public static final int ROW_HEIGHT = 50;
    public static final int  NUM_OF_COLUMNS = 6;
    public static final int TABLE_PANELֹֹ_WIDTH_ֹSIZE = 1200;
    public static final int TABLE_PANELֹֹ_HEIGHT_ֹSIZE = 350;
    public static final int TABLEֹֹ_WIDTH_ֹSIZE = 1100;
    public static final int TABLEֹֹ_HEIGHT_ֹSIZE = 350;
    public static final int FRAMEֹֹ_POSITION_X = 7;
    public static final int FRAMEֹֹ_POSITION_Y = 230;
    public static final int FRAMEֹֹ_WIDTH_ֹSIZE = 1520;
    public static final int FRAMEֹֹ_HEIGHT_ֹSIZE = 630;

    private String branchName;
    private Client client;
    private JSONObject jsonObject;
    private JSONArray jsonObjectResponseArray;
    private JSONParser jsonParser;

    private JFrame jFramePrev;
    private JPanel jPanelMain, jPanelTable;
    private SpringLayout springLayout;
    private JTable table;
    private JLabel backgroundPhotoLabel, titleLabel;
    private ImageIcon backgroundPhotoJPG, backLogoJPG, titlePhotoJerusalemJPG, titlePhotoTelAvivJPG;
    private JButton jButtonBack;


    public EmployeeList(JFrame oneBackFrame, String branchName){ //  הגיע לכאן(=המסך ניהול עובדים הראשי) המסך הראשי של האדמין או הקופאי

        this.branchName = branchName;

        SetGUIComponents(oneBackFrame);

        client = new Client();
    }

    private void SetGUIComponents(JFrame oneBackFrame) {

        this.jFramePrev = oneBackFrame;

        jPanelMain = new JPanel();

        SetButtonsOnPanel();

        jPanelTable = new JPanel();
        jPanelTable.setOpaque(false);

        GUISetTitle();

        this.setContentPane(jPanelMain);
    }

    protected void DrawEmployee() throws IOException, ParseException {

        PrepareAndSendJsonDataToServer();

        Vector<Vector<String>> dataList = GetResponseFromServer();

        DrawTheEmployeesTable(dataList);

        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout = new SpringLayout();
        jPanelMain.setLayout(springLayout);

        springLayout.putConstraint(SpringLayout.WEST,jPanelTable,160,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,jPanelTable,160,SpringLayout.NORTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,jButtonBack,0,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.SOUTH,jButtonBack,0,SpringLayout.SOUTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,titleLabel,160,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,titleLabel,25,SpringLayout.NORTH,jPanelMain);

    }

    private void DrawTheEmployeesTable(Vector<Vector<String>> dataList) {

        GUISettingForJFrame();

        jPanelTable.setPreferredSize(new Dimension(TABLE_PANELֹֹ_WIDTH_ֹSIZE, TABLE_PANELֹֹ_HEIGHT_ֹSIZE));
        jPanelMain.add(jPanelTable);

        backgroundPhotoJPG = new ImageIcon(getClass().getResource(BACK_PHOTO));
        backgroundPhotoLabel = new JLabel(backgroundPhotoJPG);
        jPanelMain.add(backgroundPhotoLabel);

        Vector<String> columnNames = new Vector<>();
        columnNames.add("SN");
        columnNames.add("Name");
        columnNames.add("ID");
        columnNames.add("PHONE");
        columnNames.add("BANK");
        columnNames.add("TYPE");

        // Insert the Strings list into the table with the columns name
        table = new JTable(dataList, columnNames){
            @Override // Set values position in the center of the columns
            public Component prepareRenderer(TableCellRenderer renderer, int row,
                                             int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                return comp;
            }
        };

        JScrollPane tableContainer = new JScrollPane(table);

        jPanelTable.add(tableContainer, BorderLayout.CENTER);

        GUISetTable(tableContainer);

    }

    private void GUISetTable(JScrollPane tableContainer) {

        setColumnWidths(table,TABLE_COLUMN_SIZE,NUM_OF_COLUMNS);
        table.setRowHeight(ROW_HEIGHT);
        table.setFont(new Font("Urban Sketch", Font.BOLD, 25));
        table.setForeground(Color.black);
        Dimension d = table.getPreferredSize();
        d.width = TABLEֹֹ_WIDTH_ֹSIZE;
        d.height = TABLEֹֹ_HEIGHT_ֹSIZE;
        table.setPreferredScrollableViewportSize(d);
        tableContainer.setPreferredSize(new Dimension(d));

        JTableHeader anHeader = table.getTableHeader();
        anHeader.setForeground(new Color(0).white);
        anHeader.setBackground(new Color(0).black);
        table.getTableHeader().setFont(new Font("Urban Sketch", Font.PLAIN, 27));

        table.setOpaque(false); // אם יש שקיפות אז אי אפשר לסמן שורה שלמה בכחול
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        tableContainer.setOpaque(false);
        tableContainer.getViewport().setOpaque(false);

        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false); // Disable moving columns position
        table.setDefaultEditor(Object.class, null);
    }

    private void setColumnWidths(JTable table, int width, int numOfColumns) {

        TableColumnModel columnModel = table.getColumnModel();

        for (int i = 0; i < numOfColumns; i++) {

            columnModel.getColumn(i).setMaxWidth(width);
        }
    }

    private void GUISetTitle() {

        titlePhotoJerusalemJPG = new ImageIcon(getClass().getResource(TITLE_JERUSALEM));
        titlePhotoTelAvivJPG = new ImageIcon(getClass().getResource(TITLE_TEL_AVIV));

        if(branchName.equals("Tel Aviv")) {
            titleLabel = new JLabel(titlePhotoTelAvivJPG);
        }else{
            titleLabel = new JLabel(titlePhotoJerusalemJPG);
        }

        jPanelMain.add(titleLabel);
    }

    private void SetButtonsOnPanel() {

        backLogoJPG = new ImageIcon(getClass().getResource(BACK_BUTTON));
        jButtonBack = new JButton(backLogoJPG);
        jButtonBack.setBorderPainted(false);
        jButtonBack.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.black, Color.black));
        jPanelMain.add(jButtonBack);
    }

    private void GUISettingForJFrame() {

        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE,FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setLocation(FRAMEֹֹ_POSITION_X,FRAMEֹֹ_POSITION_Y);
        this.setResizable(false);

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

    public SpringLayout getSpringLayout() {
        return springLayout;
    }

    public JTable getTable() {
        return table;
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
