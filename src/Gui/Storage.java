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

public class Storage extends JFrame {


    private JTable table;
    private Client client;
    private String branchName;
    private JPanel jPanelMain;
    private JPanel jPanelTable;
    private JButton jButtonBack;
    private JSONParser jsonParser;
    private JSONObject jsonObject;
    private SpringLayout springLayout;
    private Vector<String> columnNames;
    private JSONArray jsonObjectResponseArray;
    private JLabel backgroundPhotoLabel, titleLabel;
    private ImageIcon backgroundPhotoJPG, backLogoJPG, titlePhotoTelAvivJPG, titlePhotoJerusalemJPG;
    public static final int TABLE_COLUMN_SIZE = 600, ROW_HEIGHT = 50, NUM_OF_COLUMNS = 4, TABLE_PANELֹֹ_WIDTH_ֹSIZE = 1000, TABLE_PANELֹֹ_HEIGHT_ֹSIZE = 600, FRAMEֹֹ_POSITION_X = 7,
                            FRAMEֹֹ_POSITION_Y = 230, FRAMEֹֹ_WIDTH_ֹSIZE = 1520, FRAMEֹֹ_HEIGHT_ֹSIZE = 630;
    public static final String COLUMN_TYPE = "Item Type", COLUMN_SIZE = "Item Size", COLUMN_AMOUNT = "Item Amount", COLUMN_PART_NUMBER = "Part Number",
                               BACK_PHOTO = "/src/images/backPhotoStorage.png", BACK_BUTTON = "/src/images/back.png", TITLE_TEL_AVIV = "/src/images/title_branch_storage_telaviv.png",
                               TITLE_JERUSALEM = "/src/images/title_branch_storage_Jerusalem.png";


    public Storage(String branchName){

        client = new Client();

        SetObjectsComponents(branchName);
        SetGUIComponents();
        SetTheTitleBranchName();
    }

    private void SetTheTitleBranchName() {

        titlePhotoJerusalemJPG = new ImageIcon(getClass().getResource(TITLE_JERUSALEM));
        titlePhotoTelAvivJPG = new ImageIcon(getClass().getResource(TITLE_TEL_AVIV));

        if(branchName.equals("Tel Aviv")) {
            titleLabel = new JLabel(titlePhotoTelAvivJPG);
        }else{
            titleLabel = new JLabel(titlePhotoJerusalemJPG);
        }

        jPanelMain.add(titleLabel);
    }

    private void SetObjectsComponents(String branchName) {

        this.branchName = branchName;
    }

    private void SetGUIComponents() {

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

        GUIPlaceComponentsOnJPanel();

        PrepareAndSendJsonStorageDataToServer();

        Vector<Vector<String>> dataList = GetStorageResponseFromServer();

        client.getSslSocket().close();

        DrawTheStorageTable(dataList);

        InitializeActions(prevFrame);
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout = new SpringLayout();
        jPanelMain.setLayout(springLayout);


        springLayout.putConstraint(SpringLayout.WEST,jPanelTable,30,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,jPanelTable,110,SpringLayout.NORTH,jPanelMain);

        springLayout.putConstraint(SpringLayout.WEST,jButtonBack,450,SpringLayout.WEST,jPanelMain);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonBack,570,SpringLayout.NORTH,jPanelMain);
    }

    private void GUISettingForJPanel() {

        jPanelMain.add(jButtonBack);
        jPanelMain.add(jPanelTable);
        jPanelMain.add(backgroundPhotoLabel);
    }

    private void DrawTheStorageTable(Vector<Vector<String>> dataList) {

        columnNames = new Vector<>();
        columnNames.add(COLUMN_TYPE);
        columnNames.add(COLUMN_SIZE);
        columnNames.add(COLUMN_AMOUNT);
        columnNames.add(COLUMN_PART_NUMBER);

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
        table.setFont(new Font("Urban Sketch", Font.BOLD, 20));
        table.setForeground(Color.black);
        Dimension d = table.getPreferredSize();
        d.width = 1000;
        d.height = 430;
        table.setPreferredScrollableViewportSize(d);
        tableContainer.setPreferredSize(new Dimension(d));

        JTableHeader anHeader = table.getTableHeader();
        anHeader.setForeground(new Color(0).white);
        anHeader.setBackground(new Color(0).black);
        table.getTableHeader().setFont(new Font("Urban Sketch", Font.PLAIN, 25));

        table.setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        tableContainer.setOpaque(false);
        tableContainer.getViewport().setOpaque(false);

        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false); // Disable moving columns position
        table.setDefaultEditor(Object.class, null);
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
            data.add((String)jsonObject.get("item_amount"));

            jsonObject = (JSONObject)jsonObjectResponseArray.get(i++);
            data.add((String)jsonObject.get("item_part_number"));

            dataList.add(data);
        }
        return dataList;
    }

    private void GUISettingForJFrame() {

        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE,FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setLocation(FRAMEֹֹ_POSITION_X,FRAMEֹֹ_POSITION_Y);
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

    public static void setColumnWidths(JTable table, int width, int numOfColumns) {

        TableColumnModel columnModel = table.getColumnModel();

        for (int i = 0; i < numOfColumns; i++) {

                columnModel.getColumn(i).setMaxWidth(width);
            }

        }

    public static void main(String[] args) throws IOException, ParseException {

        JFrame prevFrame = null;
        Storage storage = new Storage("Tel Aviv");
        storage.DrawStorage(prevFrame);
        storage.setVisible(true);
        storage.setLocationRelativeTo(null);
    }

}



