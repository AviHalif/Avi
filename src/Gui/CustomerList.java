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

public class CustomerList extends JFrame{

    public static final String BACK_PHOTO = "/src/images/weloveour.png";
    public static final String BACK_BUTTON = "/src/images/back.png";
    public static final String TITLE = "/src/images/customers_title.png";
    public static final int TABLE_COLUMN_SIZE = 600;
    public static final int ROW_HEIGHT = 50;
    public static final int  NUM_OF_COLUMNS = 4;
    public static final int TABLE_PANELֹֹ_WIDTH_ֹSIZE = 1100;
    public static final int TABLE_PANELֹֹ_HEIGHT_ֹSIZE = 350;
    public static final int TABLEֹֹ_WIDTH_ֹSIZE = 1000;
    public static final int TABLEֹֹ_HEIGHT_ֹSIZE = 330;
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
    private JButton jButtonBack;
    private ImageIcon backgroundPhotoJPG, backLogoJPG, titlePhotoJPG;


    public CustomerList(JFrame oneBackFrame, String branchName)  { //  הגיע לכאן(=המסך ניהול עובדים הראשי) המסך הראשי של האדמין או הקופאי

        SetObjectsComponents(branchName);
        SetGUIComponents(oneBackFrame);

        client = new Client();
    }

    private void SetGUIComponents(JFrame oneBackFrame) {

        this.jFramePrev = oneBackFrame;

        jPanelMain = new JPanel();
        jPanelTable = new JPanel();
        jPanelTable.setOpaque(false);

        jPanelTable.setPreferredSize(new Dimension(TABLE_PANELֹֹ_WIDTH_ֹSIZE, TABLE_PANELֹֹ_HEIGHT_ֹSIZE));

        backgroundPhotoJPG = new ImageIcon(getClass().getResource(BACK_PHOTO));
        backgroundPhotoLabel = new JLabel(backgroundPhotoJPG);

        backLogoJPG = new ImageIcon(getClass().getResource(BACK_BUTTON));
        jButtonBack = new JButton(backLogoJPG);
        jButtonBack.setBorderPainted(false);
        jButtonBack.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.black, Color.black));

        titlePhotoJPG = new ImageIcon(getClass().getResource(TITLE));
        titleLabel = new JLabel(titlePhotoJPG);
    }

    private void SetObjectsComponents(String branchName) {

        this.branchName = branchName;
    }

    protected void DrawCustomer() throws IOException, ParseException {

        GUIPlaceComponentsOnJPanel();

        PrepareAndSendJsonCustomersListDataToServer();

        Vector<Vector<String>> dataList = GetCustomersListResponseFromServer();

        DrawTheCustomersTable(dataList);

        client.getSslSocket().close();

        }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout = new SpringLayout();
        jPanelMain.setLayout(springLayout);

        springLayout.putConstraint(SpringLayout.WEST,jPanelTable,220,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,jPanelTable,130,SpringLayout.NORTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,jButtonBack,0,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.SOUTH,jButtonBack,0,SpringLayout.SOUTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,backgroundPhotoLabel,-170,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,backgroundPhotoLabel,-80,SpringLayout.NORTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,titleLabel,390,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,titleLabel,18,SpringLayout.NORTH,jPanelMain);
    }

    private void DrawTheCustomersTable(Vector<Vector<String>> dataList) {

        GUISettingForJFrame();

        GUISettingForJPanel();

        GUISettingForJTable(dataList);

        this.setContentPane(jPanelMain);
    }

    private void GUISettingForJTable(Vector<Vector<String>> dataList) {

        Vector<String> columnNames = new Vector<>();
        columnNames.add("Customer ID");
        columnNames.add("Customer Name");
        columnNames.add("Customer Phone");
        columnNames.add("Customer Type");

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

    private void GUISettingForJPanel() {

        jPanelMain.add(titleLabel);
        jPanelMain.add(jButtonBack);
        jPanelMain.add(jPanelTable);
        jPanelMain.add(backgroundPhotoLabel);

    }

    protected void GUISettingForJFrame() {

        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE,FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setLocation(FRAMEֹֹ_POSITION_X,FRAMEֹֹ_POSITION_Y);
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setContentPane(jPanelMain);
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

    public JPanel getjPanelTable() {
        return jPanelTable;
    }

    public SpringLayout getSpringLayout() {
        return springLayout;
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