package Gui;

import Classes.Customer;
import Classes.Item;
import Client.Client;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

public class ItemsPurchaseCatalog extends JFrame {

    private Item item;
    private int itemIdx;
    private Client client;
    private byte[][] photos;
    private Customer customer;
    private JFrame jFramePrev;
    private InputStream inStream;
    private JSONParser jsonParser;
    private Properties properties;
    private JSONObject jsonObject;
    private JTextField jTextCustId;
    private JComboBox jComboBoxSize;
    private JSONArray jsonObjectResponseArray;
    private DefaultComboBoxModel comboBoxModel;
    private String[] itemPrices, partNumber, itemName;
    private String propFileName, empBranchName, empType;
    private JPanel mainPanel, topPanel, downPanel, customerDetailsPanel;
    private static Logger logger = Logger.getLogger(ItemsPurchaseCatalog.class.getName());
    private SpringLayout mainPanelLayout, downPanelLayout, topPanelLayout, customerDetailsLayout;
    private JButton jButtonBeforeBuyItem, jButtonBack, jButtonNext, jButtonPrevious, jButtonCompletePurchase, jButtonBackToCatalog, jButtonCheckCustomerId, jButtonAddNewCustomer;
    private ImageIcon itemPhoto, previousLogoJPG, nextLogoJPG, titlePhotoJPG, label_partNum, label_itemName, label_price, backLogoJPG, label_size,
                      beforeBuyLogoJPG, checkCustomerLogoJPG, addCustLogoJPG, backCatalogLogoJPG, completeLogoJPG, label_name, label_tel;
    private JLabel jLabelPrice, jLabelPartNumber, jLabelItemName, jLabelCustTel, jLabelCustName, itemLabel,
                   titleLabel, jLabePartNum, jLabeItemName, jLabePrice, jLabeSize, jLabelname, jLabeltel;
    public static final String LABEL_TEL = "/src/images/tel.png", LABEL_NAME = "/src/images/name.png", COMPLETE = "/src/images/complete.png", BACK_CATALOG = "/src/images/back_catalog.png",
                              CHECK_CUSTOMER = "/src/images/check_cust_sell.png", ADD_NEW_CUSTOMER = "/src/images/add_new_cust_sell.png", BEFORE_BUY = "/src/images/before_buy.png",
                              BUTTON_BACK = "/src/images/butt_back.png", TITLE = "/src/images/items_catalog_title.png", NEXT = "/src/images/next.png", PREVIOUS = "/src/images/prev.png",
                              LABEL_PRICE = "/src/images/unit_price.png", LABEL_SIZE = "/src/images/size.png", LABEL_PART_NUMBER = "/src/images/part_number.png",
                              LABEL_ITEM_NAME = "/src/images/item.png", SIZE1 = "S", SIZE2 = "M", SIZE3 = "L", SIZE4 = "XL", DEFAULT_SIZE = "S";
    public static final int NUM_OF_COLUMNS_IN_ALL_ITEMS_TABLE = 3, FRAMEֹֹ_POSITION_X = 7, FRAMEֹֹ_POSITION_Y = 230, FRAMEֹֹ_WIDTH_ֹSIZE = 1520, FRAMEֹֹ_HEIGHT_ֹSIZE = 630, BUTTON_WIDTH = 650,
                            BUTTON_WIDTH2 = 200, BUTTON_HEIGHT = 100, BUTTON_WIDTH3 = 200, BUTTON_HEIGHT3 = 100, BUTTON_WIDTH4 = 390, BUTTON_HEIGHT4 = 50, BUTTON_WIDTH5 = 350, BUTTON_HEIGHT5 = 50,
                            COMBO_WIDTH = 100, COMBO_HEIGHT = 40, JTEXTFIELD_WIDTH = 200, JTEXTFIELD_HEIGHT = 40;


    public ItemsPurchaseCatalog(JFrame oneBackFrame, String branchName, String empType) throws IOException { //  הגיע לכאן(=המסך ניהול עובדים הראשי) המסך הראשי של הקאשר

        SetGUIComponents(oneBackFrame);

        SetObjectsComponents(branchName,empType);

        DefineLogAndConfig();
    }

    private void DefineLogAndConfig() throws IOException {

        properties = new Properties();
        propFileName = "config.properties";
        inStream = ItemsPurchaseCatalog.class.getClassLoader().getResourceAsStream(propFileName);
        properties.load(inStream);

        String log4jConfigFile = System.getProperty("user.dir")+ File.separator+"src"+
                File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
    }

    private void SetGUIComponents(JFrame oneBackFrame) {

        this.jFramePrev = oneBackFrame;
    }

    private void SetObjectsComponents(String branchName, String empType) {

        itemIdx = 0;
        this.empBranchName = branchName;
        this.empType = empType;
        item = new Item();
        customer = new Customer();
        item.setItemBranch(branchName);
    }

    protected void DrawCatalog() {

        GUISettingForJPanel();
        GUISettingForJFrame();
        GetAllItemsDetailsAndTheirPhotosFromDB();
    }

    private void GUISettingForJPanel() {

        GUIDefineLayoutAndAddComponentsOnJPanel();
        GUIPlaceComponentsOnJPanel();
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        mainPanel = new JPanel();
        mainPanelLayout = new SpringLayout();
        mainPanel.setLayout(mainPanelLayout);
        mainPanel.setPreferredSize(new Dimension(FRAMEֹֹ_WIDTH_ֹSIZE - 100, FRAMEֹֹ_HEIGHT_ֹSIZE - 100));

        GUIAddComponentsInTopPanel();
        GUIAddComponentsInDownPanel();
        GUIAddComponentsInCustomerDetailsPanel();

        mainPanel.add(topPanel);
        mainPanel.add(downPanel);
    }

    private void GUIAddComponentsInTopPanel() {

        topPanel = new JPanel();

        topPanel.setPreferredSize(new Dimension(1520, 525));
        topPanel.setBackground(Color.black);
        topPanelLayout = new SpringLayout();
        topPanel.setLayout(topPanelLayout);

        titlePhotoJPG = new ImageIcon(getClass().getResource(TITLE));
        titleLabel = new JLabel(titlePhotoJPG);

        label_partNum = new ImageIcon(getClass().getResource(LABEL_PART_NUMBER));
        jLabePartNum = new JLabel(label_partNum);

        label_itemName = new ImageIcon(getClass().getResource(LABEL_ITEM_NAME));
        jLabeItemName = new JLabel(label_itemName);

        label_price = new ImageIcon(getClass().getResource(LABEL_PRICE));
        jLabePrice = new JLabel(label_price);

        label_size = new ImageIcon(getClass().getResource(LABEL_SIZE));
        jLabeSize = new JLabel(label_size);

        itemLabel = new JLabel();

        Font font = new Font("Urban Sketch", Font.PLAIN, 30);

        jLabelPrice = new JLabel();
        jLabelPrice.setFont(font);
        jLabelPrice.setForeground (Color.white);

        jLabelPartNumber = new JLabel();
        jLabelPartNumber.setFont(font);
        jLabelPartNumber.setForeground (Color.white);

        jLabelItemName = new JLabel();
        jLabelItemName.setFont(font);
        jLabelItemName.setForeground (Color.white);

        GUIDefineAndAddComboToTopPanel();

        beforeBuyLogoJPG = new ImageIcon(getClass().getResource(BEFORE_BUY));
        jButtonBeforeBuyItem = new JButton(beforeBuyLogoJPG);
        jButtonBeforeBuyItem.setBorderPainted(true);
        jButtonBeforeBuyItem.setOpaque(false);
        jButtonBeforeBuyItem.setBackground(Color.white);
        jButtonBeforeBuyItem.setPreferredSize(new Dimension(BUTTON_WIDTH3, BUTTON_HEIGHT3));
        jButtonBeforeBuyItem.setBorder(new LineBorder(Color.red));

        topPanel.add(jLabeSize);
        topPanel.add(jLabePrice);
        topPanel.add(jLabeItemName);
        topPanel.add(jLabePartNum);
        topPanel.add(titleLabel);
        topPanel.add(jButtonBeforeBuyItem);
        topPanel.add(jLabelPrice);
        topPanel.add(jLabelPartNumber);
        topPanel.add(jLabelItemName);
        topPanel.add(jComboBoxSize);
        topPanel.add(itemLabel);
    }

    private void GUIAddComponentsInDownPanel() {

        downPanel = new JPanel();

        downPanel.setPreferredSize(new Dimension(1520, 100));
        downPanel.setBackground(Color.white);
        downPanelLayout = new SpringLayout();
        downPanel.setLayout(downPanelLayout);

        previousLogoJPG = new ImageIcon(getClass().getResource(PREVIOUS));
        jButtonPrevious = new JButton(previousLogoJPG);
        jButtonPrevious.setBorderPainted(true);
        jButtonPrevious.setBackground(Color.white);
        jButtonPrevious.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        jButtonPrevious.setBorder(new LineBorder(Color.red));

        nextLogoJPG = new ImageIcon(getClass().getResource(NEXT));
        jButtonNext = new JButton(nextLogoJPG);
        jButtonNext.setBorderPainted(true);
        jButtonNext.setBackground(Color.black);
        jButtonNext.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        jButtonNext.setBorder(new LineBorder(Color.red));

        backLogoJPG = new ImageIcon(getClass().getResource(BUTTON_BACK));
        jButtonBack = new JButton(backLogoJPG);
        jButtonBack.setBorderPainted(true);
        jButtonBack.setOpaque(false);
        jButtonBack.setBackground(Color.white);
        jButtonBack.setPreferredSize(new Dimension(BUTTON_WIDTH2, BUTTON_HEIGHT));
        jButtonBack.setBorder(new LineBorder(Color.red));

        downPanel.add(jButtonBack);
        downPanel.add(jButtonNext);
        downPanel.add(jButtonPrevious);
    }

    private void GUIAddComponentsInCustomerDetailsPanel() {

        customerDetailsPanel = new JPanel();
        customerDetailsPanel.setPreferredSize(new Dimension(450, 525));
        customerDetailsPanel.setBackground(Color.white);
        customerDetailsLayout = new SpringLayout();
        customerDetailsPanel.setLayout(customerDetailsLayout);
        customerDetailsPanel.setVisible(false);

        GUIDefineAndAddLabelsToCustomerDetailsPanel();
        GUIDefineAndAddButtonsToCustomerDetailsPanel();
        GUIDefineAndAddTextFieldAndAreaToCustomerDetailsPanel();

        topPanel.add(customerDetailsPanel);
    }

    private void GUIDefineAndAddLabelsToCustomerDetailsPanel() {

        label_name = new ImageIcon(getClass().getResource(LABEL_NAME));
        jLabelname = new JLabel(label_name);
        jLabelname.setVisible(false);

        label_tel = new ImageIcon(getClass().getResource(LABEL_TEL));
        jLabeltel= new JLabel(label_tel);
        jLabeltel.setVisible(false);


        Font font = new Font("Urban Sketch", Font.PLAIN, 30);

        jLabelCustTel = new JLabel();
        jLabelCustTel.setFont(font);
        jLabelCustTel.setForeground (Color.black);

        jLabelCustName = new JLabel();
        jLabelCustName.setFont(font);
        jLabelCustName.setForeground (Color.black);

        topPanel.add(jLabelname);
        topPanel.add(jLabeltel);
        customerDetailsPanel.add(jLabelCustTel);
        customerDetailsPanel.add(jLabelCustName);
    }

    private void GUIDefineAndAddButtonsToCustomerDetailsPanel() {

        checkCustomerLogoJPG = new ImageIcon(getClass().getResource(CHECK_CUSTOMER));
        jButtonCheckCustomerId = new JButton(checkCustomerLogoJPG);
        jButtonCheckCustomerId.setBorderPainted(true);
        jButtonCheckCustomerId.setBackground(Color.BLACK);
        jButtonCheckCustomerId.setPreferredSize(new Dimension(BUTTON_WIDTH4, BUTTON_HEIGHT4));
        jButtonCheckCustomerId.setBorder(new LineBorder(Color.red));

        addCustLogoJPG = new ImageIcon(getClass().getResource(ADD_NEW_CUSTOMER));
        jButtonAddNewCustomer = new JButton(addCustLogoJPG);
        jButtonAddNewCustomer.setBorderPainted(true);
        jButtonAddNewCustomer.setBackground(Color.white);
        jButtonAddNewCustomer.setPreferredSize(new Dimension(BUTTON_WIDTH4, BUTTON_HEIGHT4));
        jButtonAddNewCustomer.setBorder(new LineBorder(Color.red));

        backCatalogLogoJPG = new ImageIcon(getClass().getResource(BACK_CATALOG));
        jButtonBackToCatalog = new JButton(backCatalogLogoJPG);
        jButtonBackToCatalog.setBorderPainted(true);
        jButtonBackToCatalog.setBackground(Color.white);
        jButtonBackToCatalog.setPreferredSize(new Dimension(BUTTON_WIDTH5, BUTTON_HEIGHT5));
        jButtonBackToCatalog.setBorder(new LineBorder(Color.red));

        completeLogoJPG = new ImageIcon(getClass().getResource(COMPLETE));
        jButtonCompletePurchase = new JButton(completeLogoJPG);
        jButtonCompletePurchase.setBorderPainted(true);
        jButtonCompletePurchase.setBackground(Color.white);
        jButtonCompletePurchase.setPreferredSize(new Dimension(BUTTON_WIDTH4, BUTTON_HEIGHT4));
        jButtonCompletePurchase.setBorder(new LineBorder(Color.red));

        jButtonCompletePurchase.setEnabled(false);
        jButtonCheckCustomerId.setEnabled(false);
        jButtonAddNewCustomer.setEnabled(true);

        customerDetailsPanel.add(jButtonBackToCatalog);
        customerDetailsPanel.add(jButtonCheckCustomerId);
        customerDetailsPanel.add(jButtonCompletePurchase);
        customerDetailsPanel.add(jButtonAddNewCustomer);
    }

    private void GUIDefineAndAddTextFieldAndAreaToCustomerDetailsPanel() {

        jTextCustId = new JTextField();
        jTextCustId.setPreferredSize(new Dimension(JTEXTFIELD_WIDTH,JTEXTFIELD_HEIGHT));
        jTextCustId.setHorizontalAlignment(JTextField.CENTER);
        jTextCustId.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        jTextCustId.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        jTextCustId.setForeground (Color.black);
        jTextCustId.setOpaque(false);

        customerDetailsPanel.add(jTextCustId);
    }

    private void GUIDefineAndAddComboToTopPanel() {

        comboBoxModel = new DefaultComboBoxModel();
        jComboBoxSize = new JComboBox(comboBoxModel);

        jComboBoxSize.setPreferredSize(new Dimension(COMBO_WIDTH,COMBO_HEIGHT));
        jComboBoxSize.setFont(new Font("Urban Sketch", Font.BOLD, 30));
        jComboBoxSize.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        jComboBoxSize.setOpaque(false);

        comboBoxModel.addElement(SIZE1);
        comboBoxModel.addElement(SIZE2);
        comboBoxModel.addElement(SIZE3);
        comboBoxModel.addElement(SIZE4);

        item.setItemSize(DEFAULT_SIZE); // Default value for size field
    }

    private void GUIPlaceComponentsOnJPanel() {

        GUIPlaceComponentsInTopPanel();
        GUIPlaceComponentsInDownPanel();
        GUIPlaceComponentsInCustomerDetailsPanel();

        GUIPlacePanelsOnMainPanel();
    }

    private void GUIPlaceComponentsInTopPanel() {

        topPanelLayout.putConstraint(SpringLayout.EAST, customerDetailsPanel, 0, SpringLayout.EAST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, customerDetailsPanel, 0, SpringLayout.NORTH, topPanel);

        topPanelLayout.putConstraint(SpringLayout.WEST, titleLabel, 400, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, titleLabel, 0, SpringLayout.NORTH, topPanel);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabeSize, 350, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabeSize, 15, SpringLayout.SOUTH, titleLabel);

        topPanelLayout.putConstraint(SpringLayout.WEST, jComboBoxSize, 10, SpringLayout.EAST, jLabeSize);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jComboBoxSize, 140, SpringLayout.NORTH, topPanel);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabePartNum, 215, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabePartNum, 50, SpringLayout.SOUTH, jLabeSize);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelPartNumber, 450, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelPartNumber, 110, SpringLayout.SOUTH, titleLabel);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabeItemName, 345, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabeItemName, 75, SpringLayout.SOUTH, jLabelPartNumber);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelItemName, 450, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelItemName, 115, SpringLayout.NORTH, jLabelPartNumber);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabePrice, 260, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabePrice, 65, SpringLayout.SOUTH, jLabelItemName);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelPrice, 450, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelPrice, 105, SpringLayout.NORTH, jLabelItemName);

        topPanelLayout.putConstraint(SpringLayout.WEST, jButtonBeforeBuyItem, 650, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jButtonBeforeBuyItem, 200, SpringLayout.NORTH, topPanel);

        topPanelLayout.putConstraint(SpringLayout.WEST, itemLabel, 200, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, itemLabel, 0, SpringLayout.NORTH, topPanel);
    }

    private void GUIPlaceComponentsInDownPanel() {

       downPanelLayout.putConstraint(SpringLayout.EAST, jButtonPrevious, 0, SpringLayout.EAST, downPanel);
       downPanelLayout.putConstraint(SpringLayout.NORTH, jButtonPrevious, 0, SpringLayout.NORTH, downPanel);

        downPanelLayout.putConstraint(SpringLayout.WEST, jButtonNext, 0, SpringLayout.WEST, downPanel);
        downPanelLayout.putConstraint(SpringLayout.NORTH, jButtonNext, 0, SpringLayout.NORTH, downPanel);

        downPanelLayout.putConstraint(SpringLayout.WEST, jButtonBack, 10, SpringLayout.EAST, jButtonNext);
        downPanelLayout.putConstraint(SpringLayout.NORTH, jButtonBack, 0, SpringLayout.NORTH, downPanel);
    }

    private void GUIPlaceComponentsInCustomerDetailsPanel() {

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jTextCustId, 120, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jTextCustId, 40, SpringLayout.NORTH, customerDetailsPanel);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jButtonCheckCustomerId, 20, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jButtonCheckCustomerId, 30, SpringLayout.SOUTH, jTextCustId);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jButtonAddNewCustomer, 20, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jButtonAddNewCustomer, 30, SpringLayout.SOUTH, jButtonCheckCustomerId);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelname, 880, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelname, 240, SpringLayout.NORTH, topPanel);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jLabelCustName, 50, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jLabelCustName, 35, SpringLayout.SOUTH, jButtonAddNewCustomer);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabeltel, 880, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabeltel, -5, SpringLayout.SOUTH, jLabelname);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jLabelCustTel, 50, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jLabelCustTel, 40, SpringLayout.SOUTH, jLabelCustName);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jButtonCompletePurchase, 25, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.SOUTH, jButtonCompletePurchase, -10, SpringLayout.NORTH, jButtonBackToCatalog);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jButtonBackToCatalog, 45, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.SOUTH, jButtonBackToCatalog, -10, SpringLayout.SOUTH, customerDetailsPanel);

    }

    private void GUIPlacePanelsOnMainPanel() {

        mainPanelLayout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST, mainPanel);
        mainPanelLayout.putConstraint(SpringLayout.NORTH, topPanel, 0, SpringLayout.NORTH, mainPanel);

        mainPanelLayout.putConstraint(SpringLayout.WEST, downPanel, 0, SpringLayout.WEST, mainPanel);
        mainPanelLayout.putConstraint(SpringLayout.SOUTH, downPanel, 0, SpringLayout.SOUTH, mainPanel);
    }

    private void GUISettingForJFrame() {

        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE,FRAMEֹֹ_HEIGHT_ֹSIZE);
        this.setLocation(FRAMEֹֹ_POSITION_X,FRAMEֹֹ_POSITION_Y);
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.setContentPane(mainPanel);
    }

    private void GetAllItemsDetailsAndTheirPhotosFromDB() {

        try {

            client = new Client();

            PrepareAndSendJsonDataToServerAskingForAllItemsDetails();

            GetAllItemsNamesFromServer();

            client.getSslSocket().close();

            client = new Client();

            PrepareAndSendJsonDataToServerAskingForAllItemsPhotos();

            GetAllItemsPhotosFromServer();

            client.getSslSocket().close();

            InitThePurchaseCatalogMenu();

        }catch (Exception ex){

            logger.error(ex);
        }
    }

    private void InitThePurchaseCatalogMenu() {

        itemPhoto = new ImageIcon(photos[0]); // you can read straight from byte array
        itemLabel = new JLabel(itemPhoto);
        topPanel.add(itemLabel);

        jLabelPartNumber.setText(partNumber[0]);

        jLabelPrice.setText(itemPrices[0] + " $");

        jLabelItemName.setText(itemName[0]);

        this.setVisible(true);
    }

    private void PrepareAndSendJsonDataToServerAskingForAllItemsPhotos(){

        try {
            jsonObject = new JSONObject();

            jsonObject.put("GuiName", "All Photos");

            client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
            client.getOutputStream().flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private void GetAllItemsPhotosFromServer()  {

        try{
            String stringOfAllItemsPhotos = client.getInputStream().readUTF();
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArrayResponse = (JSONArray) jsonParser.parse(stringOfAllItemsPhotos);

            Vector<Vector<String>> data = ParseJsonArrayToVector(jsonArrayResponse);

            ConvertStringItemsPhotosToBlob(data);
        }
        catch (Exception ex){}
    }

    private  void GetAllItemsNamesFromServer() {

        try {
            String stringOfAllKindOfItems = client.getInputStream().readUTF();
            jsonParser = new JSONParser();
            jsonObjectResponseArray = (JSONArray) jsonParser.parse(stringOfAllKindOfItems);
            SeparateJsonArrayToSinglesAndConvertToStrings();
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private void ConvertStringItemsPhotosToBlob(Vector<Vector<String>> data)  {

        this.photos= new byte[data.size()][];
        byte[] decoded;

        try {
            for(int i =0;i<data.size();i++) {

                BufferedImage originalImage = ImageIO.read(new File(data.get(i).get(0)));
                int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
                BufferedImage resizeImageJpg = resizeImage(originalImage, type);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                ImageIO.write(resizeImageJpg, "png", baos);
                decoded= baos.toByteArray();
                photos[i] = decoded;
            }
        }
        catch (Exception ex){logger.error(ex);}
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type){
        final int IMG_WIDTH = 422;
        final int IMG_HEIGHT = 600;
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();

        return resizedImage;
    }

    private Vector<Vector<String>> ParseJsonArrayToVector(JSONArray jsonArrayResponse) {
        Vector<Vector<String>> dataList = new Vector<>();
        JSONObject jsonObject;

        int jsonArraySize = jsonArrayResponse.size();
        int i = 0;

        while (i < jsonArraySize) {

            Vector<String> data = new Vector<>();

            jsonObject = (JSONObject) jsonArrayResponse.get(i++);
            data.add((String) jsonObject.get("item_path"));

            dataList.add(data);
        }
        return dataList;
    }

    private void SeparateJsonArrayToSinglesAndConvertToStrings()  {

        int jsonArraySize = jsonObjectResponseArray.size();
        int i = 0,j = 0;

        itemPrices = new String[jsonArraySize/NUM_OF_COLUMNS_IN_ALL_ITEMS_TABLE];

        partNumber = new String[jsonArraySize/NUM_OF_COLUMNS_IN_ALL_ITEMS_TABLE];

        itemName = new String[jsonArraySize/NUM_OF_COLUMNS_IN_ALL_ITEMS_TABLE];

        while (j < jsonArraySize) {

            jsonObject = (JSONObject)jsonObjectResponseArray.get(j++);
            this.itemName[i] = (String)jsonObject.get("item_name");

            jsonObject = (JSONObject)jsonObjectResponseArray.get(j++);
            this.partNumber[i] = (String)jsonObject.get("item_number");

            jsonObject = (JSONObject)jsonObjectResponseArray.get(j++);
            this.itemPrices[i] = (String)jsonObject.get("item_price");

            i++;
        }
    }

    private void PrepareAndSendJsonDataToServerAskingForAllItemsDetails() {

        try {
            jsonObject = new JSONObject();

            jsonObject.put("GuiName", "All Items");

            client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
            client.getOutputStream().flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    protected void InitializeActions() {

        jTextCustId.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                jButtonCheckCustomerId.setEnabled(true);
                jButtonAddNewCustomer.setEnabled(true);
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        jComboBoxSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                item.setItemSize((String) jComboBoxSize.getSelectedItem());
            }
        });

        jButtonCompletePurchase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    client = new Client();

                    PrepareAndSendJsonItemSellToServer();

                    String response = GetSellResponseFromServer();

                    client.getSslSocket().close();

                    if (!response.equals("")) {
                        JOptionPane.showMessageDialog(null, response);

                    } else {
                        JOptionPane.showMessageDialog(null, "Customer Id : " + customer.getCustId() + " bought item part number :" + item.getItemPartNumber() + " successfully");
                        logger.info("Purchase successfully - Item type : " + item.getItemType() + " Size :" + item.getItemSize());
                    }

                } catch (Exception ex) {
                    logger.error(ex);
                }

                DrawThePrevPanel();
            }
        });

        jButtonAddNewCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                jTextCustId.setText("");

                DrawAddNewCustomerMenu();
            }
        });

        jButtonBackToCatalog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                jLabelname.setVisible(false);
                jLabeltel.setVisible(false);
                jTextCustId.setEnabled(true);
                jTextCustId.setText("");
                jButtonCheckCustomerId.setEnabled(false);
                jButtonAddNewCustomer.setEnabled(true);
                customerDetailsPanel.setVisible(false);
                jButtonCompletePurchase.setEnabled(false);
                jButtonBeforeBuyItem.setEnabled(true);
                jComboBoxSize.setEnabled(true);
                jButtonNext.setEnabled(true);
                jButtonPrevious.setEnabled(true);
            }
        });

        jButtonBeforeBuyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    client = new Client();
                    PrepareAndSendJsonDataToServer();

                    String responseFromServer = GetResponseFromServer();

                    client.getSslSocket().close();

                    if (!responseFromServer.equals("")) {
                        JOptionPane.showMessageDialog(null, responseFromServer);

                    } else {

                        ChangeDownPanelToCustomerDetailsPanel();
                    }
                } catch (Exception ex) {
                    logger.error(ex);
                }
            }
        });

        jButtonBack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getJFrame().setVisible(false);
                jFramePrev.setEnabled(true);
                jFramePrev.toFront();
            }
        });

        jButtonNext.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (itemIdx == 9)
                    itemIdx = -1;

                itemIdx++;

                DrawTheNextPhotoItem();

                DrawTheNextPriceItem();

                DrawTheNextPartNumberItem();

                DrawTheNextNameItem();
            }
        });

        jButtonCheckCustomerId.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String inputId = jTextCustId.getText();

                if(CheckCustomerId(inputId)){

                    try {

                        client = new Client();

                        PrepareAndSendCustomerJsonDataToServer(inputId);

                        String responseFromServer = GetResponseCustomerFromServer();

                        client.getSslSocket().close();

                        if (!responseFromServer.equals("")) {
                            JOptionPane.showMessageDialog(null, responseFromServer);
                            jTextCustId.setText("");

                        } else {
                            ChangeTheGUIComponentsToMakePurchase();
                        }

                    } catch (Exception ex) {
                        logger.error(ex);
                    }

                }

                else
                    getjTextCustId().setText("");
            }
        });

        jButtonPrevious.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (itemIdx == 0)
                    itemIdx = 10;

                itemIdx--;

                DrawTheNextPhotoItem();

                DrawTheNextPriceItem();

                DrawTheNextPartNumberItem();

                DrawTheNextNameItem();
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

    private void ChangeTheGUIComponentsToMakePurchase() {

        jLabelname.setVisible(true);
        jLabeltel.setVisible(true);

        jLabelCustName.setText(customer.getCustName());
        jLabelCustTel.setText(customer.getCustTel());

        jLabelCustName.setVisible(true);
        jLabelCustTel.setVisible(true);

        jButtonAddNewCustomer.setEnabled(false);
        jLabelCustName.setEnabled(false);
        jLabelCustTel.setEnabled(false);
        jTextCustId.setEnabled(false);
        jButtonCheckCustomerId.setEnabled(false);

        jButtonCompletePurchase.setEnabled(true);
    }

    private void DrawTheNextNameItem() {

        jLabelItemName.setVisible(false);
        jLabelItemName.setText(itemName[itemIdx]);
        topPanel.add(jLabelItemName);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabeItemName, 345, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabeItemName, 75, SpringLayout.SOUTH, jLabelPartNumber);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelItemName, 450, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelItemName, 115, SpringLayout.NORTH, jLabelPartNumber);

        jLabelItemName.setVisible(true);
    }

    private void DrawTheNextPartNumberItem() {

        jLabelPartNumber.setVisible(false);
        jLabelPartNumber.setText(partNumber[itemIdx]);
        topPanel.add(jLabelPartNumber);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabePartNum, 216, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabePartNum, 50, SpringLayout.SOUTH, jLabeSize);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelPartNumber, 450, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelPartNumber, 110, SpringLayout.SOUTH, titleLabel);

        jLabelPartNumber.setVisible(true);
    }

    private void DrawTheNextPriceItem() {

        jLabelPrice.setVisible(false);
        jLabelPrice.setText(itemPrices[itemIdx] + " $");
        topPanel.add(jLabelPrice);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabePrice, 260, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabePrice, 65, SpringLayout.SOUTH, jLabelItemName);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelPrice, 450, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelPrice, 105, SpringLayout.NORTH, jLabelItemName);

        jLabelPrice.setVisible(true);
    }

    private void DrawTheNextPhotoItem() {

        itemLabel.setVisible(false);
        itemPhoto = new ImageIcon(photos[itemIdx]);
        itemLabel = new JLabel(itemPhoto);
        topPanel.add(itemLabel);
        itemLabel.setVisible(true);
    }

    private void ChangeDownPanelToCustomerDetailsPanel() {

        customerDetailsPanel.setVisible(true);

        jButtonPrevious.setEnabled(false);
        jButtonNext.setEnabled(false);

        jLabelCustTel.setVisible(false);
        jLabelCustName.setVisible(false);

        jButtonBeforeBuyItem.setEnabled(false);
        jComboBoxSize.setEnabled(false);
    }

    private void DrawAddNewCustomerMenu(){

        try {
            RegisterCustomer customers = new RegisterCustomer(getJFrame(), getjFramePrev(), false, empBranchName);
            getJFrame().setEnabled(false); // Disable edit the previous window
            customers.setUndecorated(true);
            customers.DrawRegister();
            customers.InitializeActions();
            customers.setVisible(true);

            jButtonCheckCustomerId.setEnabled(false);
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private void DrawThePrevPanel() {

        jLabelname.setVisible(false);
        jLabeltel.setVisible(false);

        jTextCustId.setEnabled(true);
        jTextCustId.setText("");

        customerDetailsPanel.setVisible(false);

        downPanel.setVisible(true);

        jButtonCompletePurchase.setEnabled(false);
        jButtonAddNewCustomer.setEnabled(true);
        jButtonBeforeBuyItem.setEnabled(true);
        jComboBoxSize.setEnabled(true);
        jButtonNext.setEnabled(true);
        jButtonPrevious.setEnabled(true);
    }

    private String GetSellResponseFromServer()  {

        String response= "";

        try {
            String line = client.getInputStream().readUTF();
            JSONObject jsonObjectResponse;
            JSONParser jsonParser = new JSONParser();
            jsonObjectResponse = (JSONObject) jsonParser.parse(line);
            item.setItemAmount((String) jsonObjectResponse.get("item amount"));
            response = (String) jsonObjectResponse.get("Failed");

            return response;
        }
        catch (Exception ex){
            logger.error(ex);
        }

        return response;
    }

    private void PrepareAndSendJsonItemSellToServer(){

        try {
            jsonObject = new JSONObject();
            jsonObject.put("GuiName", "Sell item");
            jsonObject.put("item part number", empType);
            jsonObject.put("item part number", partNumber[itemIdx]);
            jsonObject.put("item size", jComboBoxSize.getSelectedItem());
            jsonObject.put("item branch", item.getItemBranch());
            jsonObject.put("item type", item.getItemType());
            jsonObject.put("item price", item.getItemUnitPrice());
            jsonObject.put("customer id", customer.getCustId());
            client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
            client.getOutputStream().flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private String GetResponseCustomerFromServer() {

        String response = "";

        try {
            String line = client.getInputStream().readUTF();
            JSONObject jsonObjectResponse;
            JSONParser jsonParser = new JSONParser();
            jsonObjectResponse = (JSONObject) jsonParser.parse(line);
            response = (String) jsonObjectResponse.get("Failed");
            customer.setCustId((String) jsonObjectResponse.get("customer id"));
            customer.setCustName((String) jsonObjectResponse.get("customer name"));
            customer.setCustTel((String) jsonObjectResponse.get("customer tel"));
            customer.setCustType((String) jsonObjectResponse.get("customer type"));

            return response;
        }
        catch (Exception ex){
            logger.error(ex);
        }
        return response;
    }

    private void PrepareAndSendCustomerJsonDataToServer(String inputId) {

        try {
            jsonObject = new JSONObject();

            jsonObject.put("GuiName", "Is customer in DB");
            jsonObject.put("customerId", inputId);

            client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
            client.getOutputStream().flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private String GetResponseFromServer() {

        String responseFromServer = "";

        try {
            String line = client.getInputStream().readUTF();

            JSONObject jsonObjectResponse;
            JSONParser jsonParser = new JSONParser();
            jsonObjectResponse = (JSONObject) jsonParser.parse(line);
            responseFromServer = (String) jsonObjectResponse.get("Failed");
            item.setItemType((String) jsonObjectResponse.get("item type"));
            item.setItemSize((String) jsonObjectResponse.get("item size"));
            item.setItemBranch((String) jsonObjectResponse.get("item branch"));
            item.setItemAmount((String) jsonObjectResponse.get("item amount"));
            item.setItemUnitPrice((String) jsonObjectResponse.get("item unit price"));
            item.setItemPartNumber((String) jsonObjectResponse.get("item part number"));

            return responseFromServer;
        }
        catch (Exception ex){
            logger.error(ex);
        }

        return responseFromServer;
    }

    private void PrepareAndSendJsonDataToServer() {

        try {
            jsonObject = new JSONObject();
            jsonObject.put("GuiName", "Check item in storage");
            jsonObject.put("employee type", empType);
            jsonObject.put("item part number", partNumber[itemIdx]);
            jsonObject.put("item size", jComboBoxSize.getSelectedItem());
            jsonObject.put("item branch", item.getItemBranch());
            jsonObject.put("item type", itemName[itemIdx]);
            client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
            client.getOutputStream().flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
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

    public JFrame getjFramePrev() {
        return jFramePrev;
    }

    public JTextField getjTextCustId() {
        return jTextCustId;
    }

    public static void main(String[] args) throws IOException {

        JFrame prevFrame = null;
        ItemsPurchaseCatalog catalog = new ItemsPurchaseCatalog(prevFrame,"Jerusalem","Shift Manager");
        catalog.DrawCatalog();
        catalog.setVisible(true);
        catalog.setLocationRelativeTo(null);
    }
}
