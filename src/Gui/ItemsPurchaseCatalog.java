package Gui;

import Classes.Customer;
import Classes.Item;
import Client.Client;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ItemsPurchaseCatalog extends JFrame {

    private static Logger logger = Logger.getLogger(ItemsPurchaseCatalog.class.getName());

    public static final String LABEL_PRICE = "Unit Price :";
    public static final String LABEL_PART_NUMBER = "Part Number :";
    public static final String LABEL_ITEM_NAME = "Item Name :";
    public static final String BUTTON_BACK = "BACK";
    public static final String BUTTON_BUY = "BUY";
    public static final String DEFAULT_BRANCH_NAME = "NEXT ITEM";
    public static final String BUTTON_PREVIOUS_ITEM = "PREVIOUS ITEM";
    public static final String SIZE1 = "S";
    public static final String SIZE2 = "M";
    public static final String SIZE3 = "L";
    public static final String SIZE4 = "XL";
    public static final String DEFAULT_SIZE = "S";
    public static final int NUM_OF_COLUMNS_IN_ALL_ITEMS_TABLE = 3;

    public static final int FRAMEֹֹ_WIDTH_ֹSIZE = 1000;
    public static final int FRAMEֹֹ_HEIGHT_ֹSIZE = 800;

    private Item item;
    private Customer customer;
    private String empBranchName, empType, propFileName;
    private int itemIdx, howManyKindOfItems;
    private byte[][] photos;
    private String[] itemPrices, partNumber, itemName;

    private Properties properties;
    private InputStream inStream;

    private JSONObject jsonObject;
    private JSONArray jsonObjectResponseArray;
    private JSONParser jsonParser;

    private Client client;

    private JFrame jFramePrev;
    private JPanel mainPanel, topPanel, downPanel, customerDetailsPanel;

    private SpringLayout mainPanelLayout, downPanelLayout, topPanelLayout, customerDetailsLayout;

    private JTextArea customerNotices;
    private JLabel jLabelPrice, jLabelPartNumber, jLabelItemName, jLabelCustTel, jLabelCustType, jLabelCustName, jLabelDiscountPrice, itemLabel;
    private ImageIcon itemPhoto;

    private JButton jButtonBeforeBuyItem, jButtonBack, jButtonNext, jButtonPrevious, jButtonCompletePurchase, jButtonBackToCatalog, jButtonCheckCustomerId, jButtonAddNewCustomer;

    private JTextField jTextCustId;
    private JComboBox jComboBoxSize;
    private DefaultComboBoxModel comboBoxModel;

    public ItemsPurchaseCatalog(JFrame oneBackFrame, String branchName, String empType) throws IOException { //  הגיע לכאן(=המסך ניהול עובדים הראשי) המסך הראשי של הקאשר

        SetGUIComponents(oneBackFrame);

        SetObjectsComponents(branchName,empType);

        DefineLogAndConfig();
    }

    private void DefineLogAndConfig() throws IOException {

        properties = new Properties();
        propFileName = "config.properties";
        inStream = RegisterEmployee.class.getClassLoader().getResourceAsStream(propFileName);
        properties.load(inStream);
        String LogPath = properties.getProperty("LogPath");
        FileHandler fileHandler = new FileHandler(LogPath);
        logger.addHandler(fileHandler);
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        fileHandler.setFormatter(simpleFormatter);
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
        mainPanel.setBackground(Color.YELLOW);
        mainPanel.setPreferredSize(new Dimension(FRAMEֹֹ_WIDTH_ֹSIZE - 100, FRAMEֹֹ_HEIGHT_ֹSIZE - 100));

        GUIAddComponentsInTopPanel();
        GUIAddComponentsInDownPanel();
        GUIAddComponentsInCustomerDetailsPanel();
    }

    private void GUIAddComponentsInTopPanel() {

        topPanel = new JPanel();
        mainPanel.add(topPanel);
        topPanel.setPreferredSize(new Dimension(900, 300));
        topPanel.setBackground(Color.GREEN);
        topPanelLayout = new SpringLayout();
        topPanel.setLayout(topPanelLayout);

        itemLabel = new JLabel();
        jLabelPrice = new JLabel(LABEL_PRICE);
        jLabelPartNumber = new JLabel(LABEL_PART_NUMBER);
        jLabelItemName = new JLabel(LABEL_ITEM_NAME);
        jButtonBeforeBuyItem = new JButton(BUTTON_BUY);
        jButtonBack = new JButton(BUTTON_BACK);

        GUIDefineAndAddComboToTopPanel();

        topPanel.add(jButtonBack);
        topPanel.add(jButtonBeforeBuyItem);
        topPanel.add(jLabelPrice);
        topPanel.add(jLabelPartNumber);
        topPanel.add(jLabelItemName);
        topPanel.add(jComboBoxSize);
        topPanel.add(itemLabel);
    }

    private void GUIAddComponentsInDownPanel() {

        downPanel = new JPanel();
        mainPanel.add(downPanel);
        downPanel.setPreferredSize(new Dimension(800, 300));
        downPanel.setBackground(Color.BLUE);
        downPanelLayout = new SpringLayout();
        downPanel.setLayout(downPanelLayout);

        jButtonNext = new JButton(DEFAULT_BRANCH_NAME);
        jButtonPrevious = new JButton(BUTTON_PREVIOUS_ITEM);

        downPanel.add(jButtonNext);
        downPanel.add(jButtonPrevious);
    }

    private void GUIAddComponentsInCustomerDetailsPanel() {

        customerDetailsPanel = new JPanel();
        customerDetailsPanel.setPreferredSize(new Dimension(800, 300));
        customerDetailsPanel.setBackground(Color.RED);
        customerDetailsLayout = new SpringLayout();
        downPanel.setLayout(customerDetailsLayout);

        GUIDefineAndAddLabelsToCustomerDetailsPanel();
        GUIDefineAndAddButtonsToCustomerDetailsPanel();
        GUIDefineAndAddTextFieldAndAreaToCustomerDetailsPanel();
    }

    private void GUIDefineAndAddLabelsToCustomerDetailsPanel() {

        jLabelCustTel = new JLabel("tellllllll"); /////////// אפשר להוריד את מה שכתוב בפניםםם!!!
        jLabelCustName = new JLabel("name");
        jLabelCustType = new JLabel("Typeeeee");
        jLabelDiscountPrice = new JLabel("Discount Price");

        customerDetailsPanel.add(jLabelCustTel);
        customerDetailsPanel.add(jLabelCustType);
        customerDetailsPanel.add(jLabelDiscountPrice);
        customerDetailsPanel.add(jLabelCustName);
    }

    private void GUIDefineAndAddButtonsToCustomerDetailsPanel() {

        jButtonBackToCatalog = new JButton("BACK TO CATALOG");
        jButtonCheckCustomerId = new JButton("CHECK CUSTOMER ID");
        jButtonCompletePurchase = new JButton("COMPLETE PURCHASE");
        jButtonAddNewCustomer = new JButton("ADD NEW CUSTOMER");

        jButtonCompletePurchase.setEnabled(false);
        jButtonCheckCustomerId.setEnabled(false);
        jButtonAddNewCustomer.setEnabled(true);

        customerDetailsPanel.add(jButtonBackToCatalog);
        customerDetailsPanel.add(jButtonCheckCustomerId);
        customerDetailsPanel.add(jButtonCompletePurchase);
        customerDetailsPanel.add(jButtonAddNewCustomer);
    }

    private void GUIDefineAndAddTextFieldAndAreaToCustomerDetailsPanel() {

        customerNotices = new JTextArea("Texttttt Area");
        jTextCustId = new JTextField(15);

        customerDetailsPanel.add(customerNotices);
        customerDetailsPanel.add(jTextCustId);
    }

    private void GUIDefineAndAddComboToTopPanel() {

        comboBoxModel = new DefaultComboBoxModel();
        jComboBoxSize = new JComboBox(comboBoxModel);

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

        topPanelLayout.putConstraint(SpringLayout.WEST, jButtonBeforeBuyItem, 100, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jButtonBeforeBuyItem, 100, SpringLayout.NORTH, topPanel);

        topPanelLayout.putConstraint(SpringLayout.WEST, jComboBoxSize, 50, SpringLayout.EAST, jButtonBeforeBuyItem);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jComboBoxSize, 100, SpringLayout.NORTH, topPanel);


        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelPartNumber, 50, SpringLayout.EAST, jComboBoxSize);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelPartNumber, 100, SpringLayout.NORTH, topPanel);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelItemName, 50, SpringLayout.EAST, jLabelPartNumber);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelItemName, 100, SpringLayout.NORTH, topPanel);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelPrice, 50, SpringLayout.EAST, jLabelItemName);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelPrice, 100, SpringLayout.NORTH, topPanel);

        topPanelLayout.putConstraint(SpringLayout.WEST, jButtonBack, 100, SpringLayout.WEST, topPanel);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jButtonBack, 50, SpringLayout.SOUTH, jButtonBeforeBuyItem);


        topPanelLayout.putConstraint(SpringLayout.WEST, itemLabel, 50, SpringLayout.EAST, jButtonBack);
        topPanelLayout.putConstraint(SpringLayout.NORTH, itemLabel, 50, SpringLayout.SOUTH, jComboBoxSize);
    }

    private void GUIPlaceComponentsInDownPanel() {

        downPanelLayout.putConstraint(SpringLayout.WEST, jButtonPrevious, 100, SpringLayout.WEST, downPanel);
        downPanelLayout.putConstraint(SpringLayout.NORTH, jButtonPrevious, 100, SpringLayout.NORTH, downPanel);

        downPanelLayout.putConstraint(SpringLayout.WEST, jButtonNext, 100, SpringLayout.EAST, downPanel);
        downPanelLayout.putConstraint(SpringLayout.NORTH, jButtonNext, 100, SpringLayout.SOUTH, jButtonPrevious);
    }

    private void GUIPlaceComponentsInCustomerDetailsPanel() {

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jTextCustId, 100, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jTextCustId, 100, SpringLayout.NORTH, customerDetailsPanel);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jLabelCustType, 100, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jComboBoxSize, 100, SpringLayout.SOUTH, jTextCustId);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jLabelCustTel, 50, SpringLayout.EAST, jLabelCustType);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jLabelCustTel, 100, SpringLayout.SOUTH, jTextCustId);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jLabelDiscountPrice, 300, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.SOUTH, jLabelDiscountPrice, 200, SpringLayout.SOUTH, customerDetailsPanel);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jLabelCustName, 50, SpringLayout.EAST, jLabelDiscountPrice);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jLabelCustName, 100, SpringLayout.SOUTH, jTextCustId);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, customerNotices, 100, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, customerNotices, 50, SpringLayout.SOUTH, jLabelCustType);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jButtonBackToCatalog, 100, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jButtonBackToCatalog, 50, SpringLayout.SOUTH, jTextCustId);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jButtonCheckCustomerId, 100, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jButtonBackToCatalog, 50, SpringLayout.SOUTH, jButtonBackToCatalog);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jButtonCompletePurchase, 100, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jButtonCompletePurchase, 50, SpringLayout.SOUTH, jButtonCheckCustomerId);

        customerDetailsLayout.putConstraint(SpringLayout.WEST, jButtonAddNewCustomer, 100, SpringLayout.WEST, customerDetailsPanel);
        customerDetailsLayout.putConstraint(SpringLayout.NORTH, jButtonAddNewCustomer, 50, SpringLayout.SOUTH, jButtonCompletePurchase);
    }

    private void GUIPlacePanelsOnMainPanel() {

        mainPanelLayout.putConstraint(SpringLayout.WEST, topPanel, 50, SpringLayout.WEST, mainPanel);
        mainPanelLayout.putConstraint(SpringLayout.NORTH, topPanel, 100, SpringLayout.NORTH, mainPanel);

        mainPanelLayout.putConstraint(SpringLayout.WEST, downPanel, 50, SpringLayout.WEST, mainPanel);
        mainPanelLayout.putConstraint(SpringLayout.SOUTH, downPanel, 50, SpringLayout.SOUTH, mainPanel);

        mainPanelLayout.putConstraint(SpringLayout.WEST, customerDetailsPanel, 50, SpringLayout.WEST, mainPanel);
        mainPanelLayout.putConstraint(SpringLayout.SOUTH, customerDetailsPanel, 50, SpringLayout.SOUTH, mainPanel);
    }

    private void GUISettingForJFrame() {

        this.setSize(FRAMEֹֹ_WIDTH_ֹSIZE, FRAMEֹֹ_HEIGHT_ֹSIZE);
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

            System.out.println(ex);
        }
    }

    private void InitThePurchaseCatalogMenu() {

        Base64 codec = new Base64();
        // חלץ את הבאס64 מהגייסון ותמיר כל אחד מהם לסטרינג
        String encodedPhotos = (String) jsonObject.get("1"); // קח מתוך הגייסון את הסטרינג64 של התמונה
        // תהפוך מ base64 לבתים(45,34,34,22,78)
        byte[] decoded = codec.decode(encodedPhotos);

        itemPhoto = new ImageIcon(decoded); // you can read straight from byte array
        itemLabel = new JLabel(itemPhoto);
        topPanel.add(itemLabel);

        jLabelPartNumber.setText(LABEL_PART_NUMBER + partNumber[0]);

        jLabelPrice.setText(LABEL_PRICE + itemPrices[0] + "$");

        jLabelItemName.setText(LABEL_ITEM_NAME + itemName[0]);

        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    private void PrepareAndSendJsonDataToServerAskingForAllItemsPhotos() throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "All Photos");

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    private void GetAllItemsPhotosFromServer() throws IOException, ParseException {

        String stringOfAllItemsPhotos = client.getInputStream().readUTF();
        JSONParser jsonParser = new JSONParser();
        jsonObject = (JSONObject) jsonParser.parse(stringOfAllItemsPhotos);
        ConvertStringItemsPhotosToBlob();

    }

    private  void GetAllItemsNamesFromServer() throws ParseException, IOException{

        String stringOfAllKindOfItems = client.getInputStream().readUTF();
        jsonParser = new JSONParser();
        jsonObjectResponseArray = (JSONArray) jsonParser.parse(stringOfAllKindOfItems);
        SeparateJsonArrayToSinglesAndConvertToStrings();
    }

    private void ConvertStringItemsPhotosToBlob()  {

        byte[] decoded;
        String encodedPhotos;
        Base64 codec = new Base64();
        this.howManyKindOfItems = Integer.parseInt((jsonObject.get("Photos")).toString());

        this.photos = new byte[howManyKindOfItems][];

        int i = 0;

        for(int j=0; j < howManyKindOfItems; j++) {
            // חלץ את הבאס64 מהגייסון ותמיר כל אחד מהם לסטרינג
            i++;
            encodedPhotos = (String) jsonObject.get(Integer.toString(i)); // קח מתוך הגייסון את הסטרינג64 של התמונה
            // תהפוך מ base64 לבתים(45,34,34,22,78)
            decoded = codec.decode(encodedPhotos);
            photos[j] = decoded;
        }
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

    private void PrepareAndSendJsonDataToServerAskingForAllItemsDetails() throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "All Items");

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
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
                            System.out.print(ex);
                        }

                      //  ////////////// לצייר מחדש את הפנאל הקודם - מופיע גם בלחיצה על כפתור לחזרה לקטלוג
                        DrawThePrevPanel();
                 }
        });

        jButtonAddNewCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                jTextCustId.setText("");

                try {
                    DrawAddNewCustomerMenu();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        jButtonBackToCatalog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                jTextCustId.setEnabled(true);
                jTextCustId.setText("");
                jButtonCheckCustomerId.setEnabled(false);
                jButtonAddNewCustomer.setEnabled(true);

                customerDetailsPanel.setVisible(false);

                mainPanel.add(downPanel);

                mainPanelLayout.putConstraint(SpringLayout.WEST, downPanel, 50, SpringLayout.WEST, mainPanel);
                mainPanelLayout.putConstraint(SpringLayout.SOUTH, downPanel, 50, SpringLayout.SOUTH, mainPanel);

                downPanel.setVisible(true);

                jButtonCompletePurchase.setEnabled(false);
                jButtonBeforeBuyItem.setEnabled(true);
                jComboBoxSize.setEnabled(true);
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
                        // החלפת מסך תתרחש רק כאשר המוצר זמין במלאי
                        ChangeDownPanelToCustomerDetailsPanel();
                    }
                } catch (Exception ex) {
                    System.out.print(ex);
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

                // ציור מחדש של התמונה
                DrawTheNextPhotoItem();

                // ציור מחדש של מחיר
                DrawTheNextPriceItem();

                // ציור מחדש של מספר פריט
                DrawTheNextPartNumberItem();

                // ציור מחדש של שם המוצר
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
                        System.out.print(ex);
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

                // ציור מחדש של התמונה
                DrawTheNextPhotoItem();

                // ציור מחדש של מחיר
                DrawTheNextPriceItem();

                // ציור מחדש של מספר פריט
                DrawTheNextPartNumberItem();

                // ציור מחדש של שם המוצר
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

        jLabelCustType.setText(customer.getCustType());
        jLabelCustName.setText(customer.getCustName());
        jLabelCustTel.setText(customer.getCustTel());

        String priceBeforeDiscount = itemPrices[itemIdx];

        //String priceAfterDiscount = customer.PriceAfterDiscount(priceBeforeDiscount);

        //jLabelDiscountPrice.setText(priceAfterDiscount);

        jLabelCustType.setVisible(true);
        jLabelCustName.setVisible(true);
        jLabelCustTel.setVisible(true);
        customerNotices.setVisible(true);
        jLabelDiscountPrice.setVisible(true);

        jButtonAddNewCustomer.setEnabled(false);
        jLabelCustType.setEnabled(false);
        jLabelCustName.setEnabled(false);
        jLabelCustTel.setEnabled(false);
        jTextCustId.setEnabled(false);
        customerNotices.setEnabled(false);
        jLabelDiscountPrice.setEnabled(false);
        jButtonCheckCustomerId.setEnabled(false);

        jButtonCompletePurchase.setEnabled(true);
    }

    private void DrawTheNextNameItem() {

        jLabelItemName.setVisible(false);
        jLabelItemName.setText(LABEL_ITEM_NAME + itemName[itemIdx]);
        topPanel.add(jLabelItemName);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelItemName, 50, SpringLayout.EAST, jLabelPartNumber);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelItemName, 100, SpringLayout.NORTH, topPanel);

        jLabelItemName.setVisible(true);
    }

    private void DrawTheNextPartNumberItem() {

        jLabelPartNumber.setVisible(false);
        jLabelPartNumber.setText(LABEL_PART_NUMBER + partNumber[itemIdx]);
        topPanel.add(jLabelPartNumber);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelPartNumber, 50, SpringLayout.EAST, jComboBoxSize);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelPartNumber, 100, SpringLayout.NORTH, topPanel);

        jLabelPartNumber.setVisible(true);
    }

    private void DrawTheNextPriceItem() {

        jLabelPrice.setVisible(false);
        jLabelPrice.setText(LABEL_PRICE + itemPrices[itemIdx] + "$");
        topPanel.add(jLabelPrice);

        topPanelLayout.putConstraint(SpringLayout.WEST, jLabelPrice, 50, SpringLayout.EAST, jLabelItemName);
        topPanelLayout.putConstraint(SpringLayout.NORTH, jLabelPrice, 100, SpringLayout.NORTH, topPanel);

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

        downPanel.setVisible(false);

        mainPanel.add(customerDetailsPanel);

        mainPanelLayout.putConstraint(SpringLayout.WEST, customerDetailsPanel, 50, SpringLayout.WEST, mainPanel);
        mainPanelLayout.putConstraint(SpringLayout.SOUTH, customerDetailsPanel, 50, SpringLayout.SOUTH, mainPanel);

        jLabelCustTel.setVisible(false);
        jLabelCustType.setVisible(false);
        jLabelCustName.setVisible(false);
        jLabelDiscountPrice.setVisible(false);
        customerNotices.setVisible(false);


        customerDetailsPanel.setVisible(true);
        jButtonBeforeBuyItem.setEnabled(false);
        jComboBoxSize.setEnabled(false);
    }

    private void DrawAddNewCustomerMenu() throws IOException {

        RegisterCustomer customers = new RegisterCustomer(getJFrame(), getjFramePrev(), false, empBranchName);
        getJFrame().setEnabled(false); // Disable edit the previous window
        customers.DrawRegister();
        customers.InitializeActions();
        customers.setVisible(true);
        customers.setLocationRelativeTo(null);

        jButtonCheckCustomerId.setEnabled(false);
    }

    private void DrawThePrevPanel() {

        jTextCustId.setEnabled(true);
        jTextCustId.setText("");

        customerDetailsPanel.setVisible(false);

        mainPanel.add(downPanel);

        mainPanelLayout.putConstraint(SpringLayout.WEST, downPanel, 50, SpringLayout.WEST, mainPanel);
        mainPanelLayout.putConstraint(SpringLayout.SOUTH, downPanel, 50, SpringLayout.SOUTH, mainPanel);

        downPanel.setVisible(true);

        jButtonCompletePurchase.setEnabled(false);
        jButtonAddNewCustomer.setEnabled(true);
        jButtonBeforeBuyItem.setEnabled(true);
        jComboBoxSize.setEnabled(true);
    }

    private String GetSellResponseFromServer() throws IOException, ParseException {

        String line = client.getInputStream().readUTF();
        JSONObject jsonObjectResponse;
        JSONParser jsonParser = new JSONParser();
        jsonObjectResponse = (JSONObject) jsonParser.parse(line);
        item.setItemAmount((String) jsonObjectResponse.get("item amount"));
        String response = (String) jsonObjectResponse.get("Failed");

        return response;
    }

    private void PrepareAndSendJsonItemSellToServer() throws IOException {

        jsonObject = new JSONObject(); // ללכת עם שם הסניף,שם המוצר,מס פריט,מידה,מחיר,תז הקונה,
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

    private String GetResponseCustomerFromServer() throws IOException, ParseException {

        String line = client.getInputStream().readUTF();
        JSONObject jsonObjectResponse;
        JSONParser jsonParser = new JSONParser();
        jsonObjectResponse = (JSONObject) jsonParser.parse(line);
        String response = (String) jsonObjectResponse.get("Failed");
        customer.setCustId((String) jsonObjectResponse.get("customer id"));
        customer.setCustName((String) jsonObjectResponse.get("customer name"));
        customer.setCustTel((String) jsonObjectResponse.get("customer tel"));
        customer.setCustType((String) jsonObjectResponse.get("customer type"));

        return response;
    }

    private void PrepareAndSendCustomerJsonDataToServer(String inputId) throws IOException {

        jsonObject = new JSONObject();

        jsonObject.put("GuiName", "Is customer in DB");
        jsonObject.put("customerId", inputId);

        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
    }

    private String GetResponseFromServer() throws IOException, ParseException {

        String line = client.getInputStream().readUTF();

        JSONObject jsonObjectResponse;
        JSONParser jsonParser = new JSONParser();
        jsonObjectResponse = (JSONObject) jsonParser.parse(line);
        String responseFromServer = (String) jsonObjectResponse.get("Failed");
        item.setItemType((String) jsonObjectResponse.get("item type"));
        item.setItemSize((String) jsonObjectResponse.get("item size"));
        item.setItemBranch((String) jsonObjectResponse.get("item branch"));
        item.setItemAmount((String) jsonObjectResponse.get("item amount"));
        item.setItemUnitPrice((String) jsonObjectResponse.get("item unit price"));
        item.setItemPartNumber((String) jsonObjectResponse.get("item part number"));

        return responseFromServer;
    }

    private void PrepareAndSendJsonDataToServer() throws IOException {

        jsonObject = new JSONObject(); // ללכת עם שם הסניף,מס פריט,מידה
        jsonObject.put("GuiName", "Check item in storage");
        jsonObject.put("employee type",empType);
        jsonObject.put("item part number", partNumber[itemIdx]);
        jsonObject.put("item size", jComboBoxSize.getSelectedItem());
        jsonObject.put("item branch", item.getItemBranch());
        jsonObject.put("item type", itemName[itemIdx]);
        client.getOutputStream().writeUTF(jsonObject.toString() + "\n");
        client.getOutputStream().flush();
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
