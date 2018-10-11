package Gui;

import Client.Client;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import static java.time.LocalDate.now;

public class Reports extends JFrame {

    public static final String CASTRO_ICON = "/src/images/icon.png";
    public static final String FRAME_NAME = "CASTRO - REPORTS";
    public static final String BUTTON_REPORT_ALL = "DAILY REPORT ALL";
    public static final String BUTTON_SPECIFIC_REPORT = "SPECIFIC_REPORT";
    public static final String BUTTON_CHOOSE_FILTERS = "CHOOSE FILTERS";
    public static final String BUTTON_CANCEL = "Cancel";

    public static final int FRAMEֹֹ_WIDTH_ֹSIZE = 1279;
    public static final int FRAMEֹֹ_HEIGHT_ֹSIZE = 565;
    public static final int FRAMEֹֹ_POSITION_X = 133;
    public static final int FRAMEֹֹ_POSITION_Y = 250;

    private String branch;
    private String[] itemNames;

    private Client client;
    private JSONObject jsonObject;
    private JSONArray jsonArrayResponse;
    private JSONParser jsonParser;

    private JFrame prevFrame;
    private JPanel jPanelData;
    private SpringLayout springLayout;
    private JButton jButtonReportAll, jButtonSpecificReport, jButtonChooseFilters, jButtonCancel;
    private Checkbox[] checkArray;

    public Reports(JFrame prevFrame, String branch) {

        SetGUIComponents(prevFrame);

        SetObjectsComponents(branch);
    }

    private void SetObjectsComponents(String branch) {

        this.branch = branch;
    }

    private void SetGUIComponents(JFrame prevFrame) {

        this.prevFrame = prevFrame;
    }

    protected void DrawReports() {

        GUISettingForJFrame();

        GUISettingForJPanel();
    }

    private void GUISettingForJPanel() {

        GUIAddComponentsOnJPanel();

        GUIDrawTheNamesOfItems();

        this.add(jPanelData);

    }

    private void GUIDrawTheNamesOfItems() {

        try {
            client = new Client();

            PrepareAndSendJsonDataToServerAskingForAllItemsDetails();

            GetAllItemsNamesFromServer();

            client.getSslSocket().close();

            checkArray = new Checkbox[jsonArrayResponse.size()];

            for (int i = 0; i < jsonArrayResponse.size(); i++) {
                checkArray[i] = new Checkbox(itemNames[i]);
                checkArray[i].setBounds(100,100, 50,50);
                checkArray[i].setEnabled(false);
                jPanelData.add( checkArray[i]);
            }

        }catch (Exception ex){

            System.out.println(ex);
        }
    }

    private void GUIAddComponentsOnJPanel() {

        jPanelData = new JPanel();
        springLayout = new SpringLayout();

        jButtonReportAll = new JButton(BUTTON_REPORT_ALL);
        jButtonSpecificReport = new JButton(BUTTON_SPECIFIC_REPORT);
        jButtonCancel = new JButton(BUTTON_CANCEL);
        jButtonChooseFilters = new JButton(BUTTON_CHOOSE_FILTERS);

        jButtonSpecificReport.setVisible(false);

        jPanelData.add(jButtonReportAll);
        jPanelData.add(jButtonSpecificReport);
        jPanelData.add(jButtonCancel);
        jPanelData.add(jButtonChooseFilters);
    }

    private void GUISettingForJFrame() {

        this.setTitle(FRAME_NAME);
        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE, FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setLocation(FRAMEֹֹ_POSITION_X,FRAMEֹֹ_POSITION_Y);
        this.setIconImages(Collections.singletonList(Toolkit.getDefaultToolkit().getImage(getClass().getResource(CASTRO_ICON))));
        this.setResizable(false);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void InitializeActions() {

        jButtonSpecificReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    String type = CheckFilterFieldsFromCheckBoxes();

                    if(type.equals(""))
                        JOptionPane.showMessageDialog(null, "Please choose filters before !!! ");

                    else {

                        client = new Client();

                        PrepareAndSendJsonSpecificItemsReportsDataToServer(type);

                        GetSpecificItemsReportsResponseFromServer();

                        client.getSslSocket().close();

                        CreateDocument(type);
                    }

                }catch (Exception ex){

                }
            }
        });

        jButtonChooseFilters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    for (int i = 0; i < itemNames.length; i++) {
                        checkArray[i].setEnabled(true);
                        checkArray[i].setState(false);
                    }
                    jButtonChooseFilters.setVisible(false);
                    jButtonSpecificReport.setVisible(true);
                    }

        });

        jButtonReportAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                GUIChangeStatusOfCheckboxesAndButtonsByPressReportALL();

                try {
                    client = new Client();

                    PrepareAndSendJsonAllItemsReportsDataToServer();

                    GetAllItemsReportsResponseFromServer();

                    client.getSslSocket().close();

                    CreateDocument("all");

                }catch (Exception ex){

                }
            }
        });

        jButtonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setVisible(false);
                prevFrame.setEnabled(true);
                prevFrame.toFront();
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                prevFrame.setEnabled(true);
                prevFrame.toFront();
            }
        });
    }

    private void GetSpecificItemsReportsResponseFromServer() throws IOException, ParseException {

        String line = client.getInputStream().readUTF();
        JSONParser jsonParser = new JSONParser();
        jsonArrayResponse = (JSONArray) jsonParser.parse(line);
    }

    private void PrepareAndSendJsonSpecificItemsReportsDataToServer(String type) throws IOException {

        jsonObject = new JSONObject();
        jsonObject.put("GuiName", "Report");
        jsonObject.put("branch", branch);
        jsonObject.put("type", type);//all or specific item

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    private void GetAllItemsReportsResponseFromServer() throws IOException, ParseException {

        String line = client.getInputStream().readUTF();
        JSONParser jsonParser = new JSONParser();
        jsonArrayResponse = (JSONArray) jsonParser.parse(line);
    }

    private void PrepareAndSendJsonAllItemsReportsDataToServer() throws IOException {

        jsonObject = new JSONObject();
        jsonObject.put("GuiName", "Report");
        jsonObject.put("branch", branch);
        jsonObject.put("type", "all");

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    private void GUIChangeStatusOfCheckboxesAndButtonsByPressReportALL() {

        jButtonSpecificReport.setVisible(false);
        jButtonChooseFilters.setVisible(true);

        for (int i = 0; i < itemNames.length; i++) {
            checkArray[i].setState(true);
            checkArray[i].setEnabled(false);
        }

    }

    private  void GetAllItemsNamesFromServer() throws ParseException, IOException {

        String stringOfAllKindOfItems = client.getInputStream().readUTF();
        jsonParser = new JSONParser();
        jsonArrayResponse = (JSONArray) jsonParser.parse(stringOfAllKindOfItems);
        SeparateItemsNamesJsonArrayToSinglesAndConvertToStrings();

    }

    private void SeparateItemsNamesJsonArrayToSinglesAndConvertToStrings() {

        int jsonArraySize = jsonArrayResponse.size();
        int i = 0;

        itemNames = new String[jsonArraySize];

        while (i < jsonArraySize) {

            jsonObject = (JSONObject)jsonArrayResponse.get(i);
            this.itemNames[i] = (String)jsonObject.get("item_name");

            i++;
        }
    }

    private void PrepareAndSendJsonDataToServerAskingForAllItemsDetails() throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "All Items Names");

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    private String CheckFilterFieldsFromCheckBoxes() {

        String type = "";

        int numOfItems = itemNames.length;

        for (int i = 0; i < numOfItems; i++) { // מספר המוצרים השונים בבסיס נתונים - יש לקרוא מגודל של מערך

            if (checkArray[i].getState() && type.equals(""))
                type = type + "'" + checkArray[i].getLabel() + "'";

            else if(checkArray[i].getState() && !(type.equals("")))
                type = type + ",'" + checkArray[i].getLabel() + "'";
        }
        return type;
    }

    private void CreateDocument(String type) throws IOException {

        Vector<Vector<String>> dataList = SeparateItemsSellsDetailsJsonArrayToSinglesAndConvertToStrings();

        XWPFDocument document = new XWPFDocument();

        XWPFTable table = SettingForTheWordReport(type, document);

        FillTheTableInTheWordReport(table, dataList);

        CreateTotalProfitInReport(dataList, document);

        DefineTheDefaultNameAndOutputTheWordFile(document);
    }

    private void DefineTheDefaultNameAndOutputTheWordFile( XWPFDocument document) throws IOException {

        Date date = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String currentTime = sdf.format(date);

        FileOutputStream out = new FileOutputStream(new File(currentTime + ".docx"));
        document.write(out);
        out.close();

        File file = new File(currentTime + ".docx");
        Desktop desktop = Desktop.getDesktop();
        if(file.exists()) desktop.open(file);
    }

    private void CreateTotalProfitInReport(Vector<Vector<String>> dataList, XWPFDocument document) {

        XWPFTable table2 = document.createTable();
        XWPFTableRow tableRowTwo = table2.getRow(0);
        int sumPrice=0;

        for(int j =0; j<dataList.size();j++){
            sumPrice += Integer.parseInt(dataList.get(j).get(4));
        }
        tableRowTwo.getCell(0).setText("This is the total profit today: "+sumPrice);
    }

    private void FillTheTableInTheWordReport(XWPFTable table, Vector<Vector<String>> dataList) {

        for(int j =0; j<dataList.size();j++){

            XWPFTableRow tempTableRow = table.createRow();

            for(int k =0;k<dataList.get(j).size();k++) {

                tempTableRow.getCell(k).setText(dataList.get(j).get(k));
            }
        }
    }

    private XWPFTable SettingForTheWordReport(String type, XWPFDocument document) throws IOException {

        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);

        CTP ctpHeader = CTP.Factory.newInstance();
        CTR ctrHeader = ctpHeader.addNewR();
        CTText ctHeader = ctrHeader.addNewT();
        String headerText = "Daily Report: " + now() + " for branch name: " + branch +" - filter by : "+ type + " items";
        ctHeader.setStringValue(headerText);

        XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
        XWPFParagraph[] parsHeader = new XWPFParagraph[1];
        parsHeader[0] = headerParagraph;
        policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);

        //create table
        XWPFTable table = document.createTable();

        DefineColumnsNames(table);

        return table;
    }

    private void DefineColumnsNames( XWPFTable table) {

        //create first row
        XWPFTableRow tableRowOne = table.getRow(0);

        tableRowOne.getCell(0).setText("Customer ID");
        tableRowOne.addNewTableCell().setText("Item Id");
        tableRowOne.addNewTableCell().setText("Type");
        tableRowOne.addNewTableCell().setText("Size");
        tableRowOne.addNewTableCell().setText("Price");
        tableRowOne.addNewTableCell().setText("Date");
    }

    private Vector<Vector<String>> SeparateItemsSellsDetailsJsonArrayToSinglesAndConvertToStrings() {

        JSONObject jsonObject;
        Vector<Vector<String>> dataList = new Vector<>();

        int jsonArraySize = jsonArrayResponse.size();
        int i = 0;

        while (i < jsonArraySize) {

            Vector<String> data = new Vector<>();

            jsonObject = (JSONObject)jsonArrayResponse.get(i++);
            data.add((String)jsonObject.get("cust_id"));

            jsonObject = (JSONObject)jsonArrayResponse.get(i++);
            data.add((String)jsonObject.get("item_part_number"));

            jsonObject = (JSONObject)jsonArrayResponse.get(i++);
            data.add((String)jsonObject.get("item_type"));

            jsonObject = (JSONObject)jsonArrayResponse.get(i++);
            data.add((String)jsonObject.get("item_size"));

            jsonObject = (JSONObject)jsonArrayResponse.get(i++);
            data.add((String)jsonObject.get("item_price"));

            jsonObject = (JSONObject)jsonArrayResponse.get(i++);
            data.add((String)jsonObject.get("sell_time"));

            dataList.add(data);
        }

        return dataList;
    }

    public static void main (String[]args){

        Reports report = new Reports(null,"Jerusalem");
        report.DrawReports();
        report.InitializeActions();
        report.setVisible(true);
        report.setLocationRelativeTo(null);
    }
}
