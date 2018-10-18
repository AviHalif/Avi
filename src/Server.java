import Classes.Customer;
import Classes.Employee;
import Classes.Item;
import Classes.PasswordUtils;
import Gui.RegisterEmployee;
import com.sun.net.ssl.internal.ssl.Provider;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.security.Security;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


class Server implements Runnable  {

    //The Port number through which this server will accept client connections
    public static final int PORT = 35786;
    public static final String KEYSTORE = "javax.net.ssl.keyStore";
    public static final String KEYSTORE_FILE_DIRECTORY =  "C:/Users/AviHalif/IdeaProjects/Avi/src/myKeyStore.jks";
    public static final String KEYSTORE_PASSWORD = "javax.net.ssl.keyStorePassword";
    public static final String KEYSTORE_PASSWORD_FILE = "java13579";
    public static final String HANDSHAKE_DEBUG = "javax.net.debug";
    public static final String DEFINITION_HANDSHAKE_DEBUG = "all";
    public static final String MYSQL_CLASS = "com.mysql.jdbc.Driver";

    public String mysql_url, mysql_user, mysql_password, propFileName;
    private static Connection conn;

    private Properties properties;
    private InputStream inStream;

    private SSLSocket sslServerSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private JSONObject jsonObject;

    public Server(SSLSocket sslServerSocket) throws IOException {

        DefineLogAndConfig();

        mysql_url = properties.getProperty("DBUrl");
        mysql_user = properties.getProperty("DBUser");
        mysql_password = properties.getProperty("DBPassword");

        try {
            Class.forName(MYSQL_CLASS);
            conn = DriverManager.getConnection(mysql_url, mysql_user, mysql_password);

            this.sslServerSocket = sslServerSocket;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {

            DefineCommunicationBuffer();

            String serverOperation = ReadAndParseData();

            ChooseServerOperation(serverOperation);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DefineLogAndConfig() throws IOException {

        properties = new Properties();
        propFileName = "config.properties";
        inStream = RegisterEmployee.class.getClassLoader().getResourceAsStream(propFileName);
        properties.load(inStream);
    }

    private void ChooseServerOperation(String serverOperation) {

        switch (serverOperation) {

            case "Log in": {
                LogInEmployee(jsonObject, outputStream);
                break;
            }

            case "Register": {
                RegisterEmployee(jsonObject, outputStream);
                break;
            }

            case "Storage": {
                StorageResponse(jsonObject, outputStream);
                break;
            }

            case "Customer List": {
                CustomerListResponse(outputStream);
                break;
            }

            case "Check employee already exists": {
                CheckIfEmployeeAlreadyExists();
                break;
            }

            case "Customer register": {
                RegisterCustomer(jsonObject, outputStream);
                break;
            }

            case "Is customer in DB": { // Check if the customer id exist the DB
                IsCustomerInDB(jsonObject, outputStream);
                break;
            }

            case "Update Customer": { // Change the values of a specific customer in the DB
                UpdateCustomer(outputStream, jsonObject);
                break;
            }

            case "EmployeeList": {
                EmployeeListResponse(jsonObject, outputStream);
                break;
            }

            case "EmployeeEdit": {
                EditEmployee(jsonObject, outputStream);
                break;
            }

            case "UpdateEmployee": { // Change the values of a specific employee in the DB
                UpdateEmployee(jsonObject, outputStream);
                break;
            }

            case "Check item in storage": {
                CheckIfItemInStorage();
                break;
            }

            case "Sell item": {
                SellItem();
                break;
            }

            case "All Items": {
                ReturnAllKindsOfItems();
                break;
            }

            case "All Photos": {
                ReturnAllKindsOfItemsPhotos();
                break;
            }

            case "Report": {
                ReportResponse();
                break;
            }

            case "All Items Names": {
                ReturnAllNamesOfItems();
                break;
            }

            case "Log out": {
                LogOutEmployee();
                break;
            }
        }
    }

    private void UpdateEmployee(JSONObject jsonObject, DataOutputStream outputStream) {

        try {

            String response = "";
            String empStr = (String) jsonObject.get("employee");
            String anyChangeInPass = (String) jsonObject.get("IsChangePass");
            String anyChangeInPhoto = (String) jsonObject.get("IsChangePhoto");

            ObjectMapper objectMapper = new ObjectMapper();
            Employee employee = objectMapper.readValue(empStr, Employee.class);

            PreparedStatement preparedStmt;

            if(anyChangeInPass.equals("true") && anyChangeInPhoto.equals("true")) {

                preparedStmt = MySqlQueryToUpdatePssAndPhotoBoth(conn,employee);
            }

            else if(anyChangeInPass.equals("false") && anyChangeInPhoto.equals("true")) {

                preparedStmt = MySqlQueryToUpdateWithoutPass(conn,employee);
            }

            else if(anyChangeInPass.equals("false") && anyChangeInPhoto.equals("false")) {

                preparedStmt = MySqlQueryToUpdateWithoutPassAndPhoto(conn,employee);
            }

            else{
                preparedStmt = MySqlQueryToUpdatePssToo(conn,employee);
            }

            try {
                preparedStmt.execute();
            } catch (Exception ex) {
                if (ex.toString().contains("PRIMARY")) {
                    // response = "Employee already exists";/////////////////////////////
                }
            }
            preparedStmt.close();

            SendUpdateEmployeeResponseToClient(response);

        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    private void SendUpdateEmployeeResponseToClient(String response) throws IOException {

        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("Failed", response);
        outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
        outputStream.flush();
    }

    private PreparedStatement MySqlQueryToUpdatePssToo(Connection conn, Employee employee) throws SQLException {

        String query = "update store.employee set emp_name = ?, emp_tel = ?, emp_type = ?, emp_bank = ?, emp_securePass = ?, emp_passSalt = ? where emp_id = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, employee.getEmpName());
        preparedStmt.setString(2, employee.getEmpTel());
        preparedStmt.setString(3, employee.getEmpType());
        preparedStmt.setString(4, employee.getEmpBank());
        preparedStmt.setString(5, employee.getEmpPass());
        preparedStmt.setString(6, employee.getEmpPassSalt());
        preparedStmt.setString(7, employee.getEmpId());

        return preparedStmt;
    }

    private PreparedStatement MySqlQueryToUpdateWithoutPassAndPhoto(Connection conn, Employee employee) throws SQLException {

        String query = "update store.employee set emp_name = ?, emp_tel = ?, emp_type = ?, emp_bank = ? where emp_id = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, employee.getEmpName());
        preparedStmt.setString(2, employee.getEmpTel());
        preparedStmt.setString(3, employee.getEmpType());
        preparedStmt.setString(4, employee.getEmpBank());
        preparedStmt.setString(5, employee.getEmpId());

        return preparedStmt;
    }

    private PreparedStatement MySqlQueryToUpdateWithoutPass(Connection conn, Employee employee) throws SQLException, FileNotFoundException {

        String query = "update store.employee set emp_name = ?, emp_tel = ?, emp_type = ?, emp_bank = ?, emp_photo = ? where emp_id = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, employee.getEmpName());
        preparedStmt.setString(2, employee.getEmpTel());
        preparedStmt.setString(3, employee.getEmpType());
        preparedStmt.setString(4, employee.getEmpBank());
        // חלץ את נתיב התמונה בנפרד
        InputStream inputStream = new FileInputStream(new File(employee.getEmpPhoto()));
        preparedStmt.setBlob(5, inputStream);
        preparedStmt.setString(6, employee.getEmpId());

        return preparedStmt;
    }

    private PreparedStatement MySqlQueryToUpdatePssAndPhotoBoth(Connection conn, Employee employee) throws SQLException, FileNotFoundException {

        String query = "update store.employee set emp_name = ?, emp_tel = ?, emp_type = ?, emp_bank = ?, emp_securePass = ?, emp_passSalt = ?, emp_photo = ? where emp_id = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, employee.getEmpName());
        preparedStmt.setString(2, employee.getEmpTel());
        preparedStmt.setString(3, employee.getEmpType());
        preparedStmt.setString(4, employee.getEmpBank());
        preparedStmt.setString(5, employee.getEmpPass());
        preparedStmt.setString(6, employee.getEmpPassSalt());
        // חלץ את נתיב התמונה בנפרד
        InputStream inputStream = new FileInputStream(new File(employee.getEmpPhoto()));
        preparedStmt.setBlob(7, inputStream);
        preparedStmt.setString(8, employee.getEmpId());

        return preparedStmt;
    }

    private void EditEmployee(JSONObject jsonObject, DataOutputStream outputStream) {

        try {

            Employee employee = new Employee();
            employee.setEmpId((String) jsonObject.get("employeeId"));
            employee.setEmpBranch((String) jsonObject.get("branch name"));

            String response = MySqlDefinitionAndQueryForEditEmployee(employee);

            if(employee.getEmpStatus().equals("OUT")) {

                // כעת אפשר להכניס את נתיב התמונה לגייסון כי הוא סטרינגי
                InsertAllEditEmployeeParametersToJesonAndSendToClient(employee, response);
            }
            else{

                response = "User ID: " + employee.getEmpId() + " is in the terminal now, you can't change details when the user status is online";
                JSONObject jsonObjectResponse = new JSONObject();
                jsonObjectResponse.put("Failed", response);
                outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
                outputStream.flush();
            }
        } catch (Exception ex) {

        }
    }

    private void InsertAllEditEmployeeParametersToJesonAndSendToClient(Employee employee, String response) throws IOException {

        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("Failed", response);
        jsonObjectResponse.put("employee sn", employee.getEmpSn());
        jsonObjectResponse.put("employee name", employee.getEmpName());
        jsonObjectResponse.put("employee tel", employee.getEmpTel());
        jsonObjectResponse.put("employee bank", employee.getEmpBank());
        jsonObjectResponse.put("employee branch", employee.getEmpBranch());
        jsonObjectResponse.put("employee type", employee.getEmpType());
        jsonObjectResponse.put("employee photo", employee.getEmpPhoto());

        outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
        outputStream.flush();
    }

    private String ConvertPhotoBeforeInsertToJson(ResultSet resultSet) throws SQLException, IOException {

        Base64 codec = new Base64();
        String photo64;

        InputStream photois = resultSet.getBinaryStream("emp_photo"); // למשוך את נתיב התמונה מהריזאלט סט
        byte[] bytes = IOUtils.toByteArray(photois);  // להפוך לבתים
        photo64 = codec.encodeBase64String(bytes); // מבתים לסטרינג 64 כדי שאוכל להכניס לגייסון אותם אחרת בparse בקליינט יהיה חריגה

        return photo64;
    }

    private String MySqlDefinitionAndQueryForEditEmployee(Employee employee) throws ClassNotFoundException, SQLException, IOException {

        String response = "";

        Statement statement = conn.createStatement();
        String query = String.format("select emp_sn,emp_name,emp_tel,emp_bank,emp_branch,emp_type,emp_photo,emp_status from store.employee where emp_id='%s' and emp_branch='%s'", employee.getEmpId(),employee.getEmpBranch());
        ResultSet resultSet = statement.executeQuery(query);

        if (resultSet.next()) {

            GetDataFromResultSetToEmployeeObject(employee, resultSet);

            // המרה של הנתיב של הפוטו לפני שייכנס לגייסון כדי שבהמרה עם הפארסר בצד השני הוא ימיר אותו טוב
            employee.setEmpPhoto(ConvertPhotoBeforeInsertToJson(resultSet));

        } else {
            response = "Customer ID: " + employee.getEmpId() + " is not found !!!";
        }

        statement.close();

        return response;
    }

    private void GetDataFromResultSetToEmployeeObject(Employee employee,ResultSet resultSet) throws SQLException {

        employee.setEmpSn(resultSet.getString("emp_sn"));
        employee.setEmpName(resultSet.getString("emp_name"));
        employee.setEmpTel(resultSet.getString("emp_tel"));
        employee.setEmpBank(resultSet.getString("emp_bank"));
        employee.setEmpBranch(resultSet.getString("emp_branch"));
        employee.setEmpType(resultSet.getString("emp_type"));
        employee.setEmpStatus(resultSet.getString("emp_status"));
    }

    private void ReturnAllKindsOfItemsPhotos() {

        try {

            Statement statement = conn.createStatement();

            ResultSet allPhotosOfItems = QueryToDbForAllPhotosOfItems(statement);


            if (allPhotosOfItems.next()) {

                SendAllKindOfItemsPhotosToClient(allPhotosOfItems, outputStream, statement);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void ReturnAllKindsOfItems() {

        try {

            Statement statement = conn.createStatement();

            ResultSet allKindOfItemsFromDB = QueryToDbForAllKindOfItems(jsonObject,statement);

            if (allKindOfItemsFromDB.next()) {

            allKindOfItemsFromDB.beforeFirst(); // להחזיר להתחלה את התשובה מהבסיס נתונים
                //converter of ResultSet into JSONArray : all the specific branch storage
            JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(allKindOfItemsFromDB);

            SendAllKindOfItemsToClient(jsonArrayResponse, outputStream, statement);

            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void ReturnAllNamesOfItems() {

        try {

            Statement statement = conn.createStatement();

            ResultSet allKindOfItemsFromDB = QueryToDbForAllNamesOfItems(jsonObject,statement);

            if (allKindOfItemsFromDB.next()) {

                allKindOfItemsFromDB.beforeFirst(); // להחזיר להתחלה את התשובה מהבסיס נתונים
                //converter of ResultSet into JSONArray : all the specific branch storage
                JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(allKindOfItemsFromDB);

                SendAllKindOfItemsToClient(jsonArrayResponse, outputStream, statement);

            }

        } catch (Exception e) {
            System.out.println(e);

        }
    }

    private void SendAllKindOfItemsPhotosToClient(ResultSet allPhotosOfItems, DataOutputStream outputStream, Statement statement) throws IOException, SQLException, ClassNotFoundException {

        int numberOfPhotos = GetTheNumberOfPhotos(allPhotosOfItems);

        // מהבסיס נתונים נמשך כריזאלט סט ואת המערך הזה עכשיו צריך להמיר בו כל תמונה כדי שיהיה אפשר להכניס אותו לגייסון
        String[] photo64 = new String[numberOfPhotos];

        ConvertResultSetToString(allPhotosOfItems, numberOfPhotos, photo64);

            // כעת אפשר להכניס את המערך תמונות לגייסון כי הוא סטרינגי
        PrepareThePhotoJsonObjectAndSendToClient(photo64, numberOfPhotos);

        statement.close();
    }

    private void PrepareThePhotoJsonObjectAndSendToClient(String[] photo64, int numberOfPhotos) throws IOException {

        JSONObject jsonObject = new JSONObject();

        int j = 1;

        for(int i=0; i < numberOfPhotos; i++){

            jsonObject.put(Integer.toString(j), photo64[i]);
            j++;
        }

        jsonObject.put("Photos", numberOfPhotos);

        outputStream.writeUTF(jsonObject.toString() + "\n");
        outputStream.flush();
    }

    private void ConvertResultSetToString(ResultSet allPhotosOfItems ,int numberOfPhotos, String[] photo64) throws IOException, SQLException {

        Base64 codec = new Base64();
        int i = 0;

        while(allPhotosOfItems.next()){
            InputStream photois = allPhotosOfItems.getBinaryStream("item_photo"); // למשוך את נתיב התמונה מהריזאלט סט
            byte[] bytes = IOUtils.toByteArray(photois);  // להפוך לבתים
            photo64[i++] = codec.encodeBase64String(bytes); // מבתים לסטרינג 64 כדי שאוכל להכניס לגייסון אותם אחרת בparse בקליינט יהיה חריגה
        }
    }

    private int GetTheNumberOfPhotos(ResultSet allPhotosOfItems) throws SQLException {

        // get num of photos
        int numberOfPhotos = 0;
        if (allPhotosOfItems != null) {
            allPhotosOfItems.beforeFirst();
            allPhotosOfItems.last();
            numberOfPhotos = allPhotosOfItems.getRow();
        }

        allPhotosOfItems.beforeFirst(); // תחזיר את ריזאלטסט להתחלה שלו
        return numberOfPhotos;
    }

    private ResultSet QueryToDbForAllPhotosOfItems(Statement statement) throws SQLException {

        String query = String.format("select item_photo from store.all_items");
        ResultSet allKindOfItems = statement.executeQuery(query);

        return allKindOfItems;
    }

    private void SendAllKindOfItemsToClient(JSONArray jsonArrayResponse, DataOutputStream outputStream, Statement statement) throws IOException, SQLException {

        outputStream.writeUTF(jsonArrayResponse.toString() + "\n");
        outputStream.flush();

        statement.close();
    }

    private ResultSet QueryToDbForAllKindOfItems(JSONObject jsonObject, Statement statement) throws ClassNotFoundException, SQLException {

        statement = conn.createStatement();
        String query = String.format("select item_name,item_number,item_price from store.all_items");
        ResultSet allKindOfItems = statement.executeQuery(query);

        return allKindOfItems;
    }

    private ResultSet QueryToDbForAllNamesOfItems(JSONObject jsonObject, Statement statement) throws ClassNotFoundException, SQLException {

        statement = conn.createStatement();
        String query = String.format("select item_name from store.all_items");
        ResultSet allKindOfItems = statement.executeQuery(query);

        return allKindOfItems;
    }

    private void CheckIfEmployeeAlreadyExists() {

        try {
            String response;
            String employeeId = (String) jsonObject.get("employeeId");

            String employeeSN = "";

            Statement statement = conn.createStatement();
            String query = String.format("select emp_sn from store.employee where emp_id='%s'", employeeId);
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                employeeSN = resultSet.getString("emp_sn");
                response = "Customer ID: " + employeeId + " (S/N: " + employeeSN + ") " + " is already exists !!!";
            } else {
                response = "";
            }

            statement.close();

            JSONObject jsonObjectResponse = new JSONObject();
            jsonObjectResponse.put("Failed", response);
            outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
            outputStream.flush();
        } catch (Exception ex) {

        }
    }

    private void LogOutEmployee() {

        try {

            String emp_id = GetIdToLogOutFromClient(jsonObject);

            MySqlDefinitionAndQueryToLogOut(emp_id);

        } catch (Exception ex) {
        }
    }

    private void MySqlDefinitionAndQueryToLogOut(String emp_id) {

        try {

            String query = "update store.employee set emp_status = 'OUT' where emp_id = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, emp_id);
            preparedStmt.execute();
        } catch (Exception ex) {

       }
    }

    private void ReportResponse() {

        try {

            String branch = (String) jsonObject.get("branch");
            String type = (String) jsonObject.get("type");

            MySqlDefinitionAndQueryForItemsReportDetails(branch,type);

        } catch (Exception ex) {

        }
    }

    private void PrepareAndSendJsonReportDataToClient(ResultSet resultSet) throws Exception {

        JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(resultSet);

        outputStream.writeUTF(jsonArrayResponse.toString()+"\n");

        outputStream.flush();
    }

    private void MySqlDefinitionAndQueryForItemsReportDetails(String branch, String type) throws Exception {

        Statement statement = conn.createStatement();
        String query ;

        if(type.equals("all")) {
            query = String.format("select cust_id,item_part_number,item_type,item_size,item_price,sell_time from store.sells where substr(sell_time,1,10) = substr(now(),1,10) and branch_name='%s' ",branch);
        }
        else{
            query = String.format("select cust_id,item_part_number,item_type,item_size,item_price,sell_time from store.sells where substr(sell_time,1,10) = substr(now(),1,10) and branch_name='%s' and item_type in(%s)",branch,type);
        }

        ResultSet resultSet = statement.executeQuery(query);

        PrepareAndSendJsonReportDataToClient(resultSet);

        statement.close();
    }

    private void SellItem() {

        try {

            String response;
            Item item = new Item();

            String employee_type = (String) jsonObject.get("employee type");
            String customerId = (String) jsonObject.get("customer id");

            GetResponseSellItemFromClient(item);

            CheckItemAmountFromDB(item);

            if(!(item.getItemAmount().equals("0"))) {

                response = MySqlDefinitionSellItem(item,customerId);

            }else{
                if (!(employee_type.equals("Shift Manager")))
                    response = "Out of storage!!! Ask your shift manager to renew branch storage";
                else
                    response = "Out of storage!!! Renew this item: " + item.getItemType() + ", " + "Size: " + item.getItemSize();
            }

            JSONObject jsonObjectResponse = new JSONObject();
            jsonObjectResponse.put("Failed", response);
            jsonObjectResponse.put("item amount", item.getItemAmount());
            outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
            outputStream.flush();
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    private String MySqlDefinitionSellItem(Item item, String customerId) throws ClassNotFoundException, SQLException {

        String response = "";

        String query = "update store.storage set item_amount = ? where item_part_number = ? and item_size = ? and item_branch = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);

        // המרה לאינט ואחכ לסטרינג
        Integer intNewValue = Integer.parseInt(item.getItemAmount());
        intNewValue--;
        String strNewValue = String.valueOf(intNewValue);

        preparedStmt.setString(1, strNewValue);
        preparedStmt.setString(2, item.getItemPartNumber());
        preparedStmt.setString(3, item.getItemSize());
        preparedStmt.setString(4, item.getItemBranch());

        try {

            preparedStmt.execute();
            UpdateSellsTable(item, customerId); // פונקצית עזר לעדכון כל מכירה שהיתה

        } catch (Exception ex) {

            if (ex.toString().contains("PRIMARY")) { ///////////////////////////////
                response = "Error to purchase this item!!!";
            }
        }
        preparedStmt.close();

        return response;
    }

    private void GetResponseSellItemFromClient(Item item) {

        item.setItemPartNumber((String) jsonObject.get("item part number"));
        item.setItemUnitPrice((String) jsonObject.get("item price"));
        item.setItemSize((String) jsonObject.get("item size"));
        item.setItemBranch((String) jsonObject.get("item branch"));
        item.setItemType((String) jsonObject.get("item type"));

    }

    private void CheckItemAmountFromDB(Item item) throws SQLException, ClassNotFoundException {

        Statement statement = conn.createStatement();
        String query = String.format("select item_amount from store.storage where item_part_number='%s' and item_size='%s' and item_branch='%s'", item.getItemPartNumber(),item.getItemSize(),item.getItemBranch());
        ResultSet resultSet = statement.executeQuery(query);
        if(resultSet.next()) {
            String itemAmount = resultSet.getString("item_amount");
            item.setItemAmount(itemAmount);
        }
        statement.close();
    }

    private void UpdateSellsTable(Item item,String customerId) {

        String DateAndTime = WhatIsTheCurrentTimeAndDate();

        try {

            PreparedStatement preparedStatement = MySqlDefinitionUpdateSellsTable(item, customerId, DateAndTime);

            try {
                preparedStatement.execute();
            } catch (Exception ex) {
                if (ex.toString().contains("PRIMARY")) {
                    /////////////////////////////////////////////////////////
                }
            }
            outputStream.flush();
        } catch (Exception ex) {/////////////////////////////////
            System.out.print(ex);
        }
    }

    private PreparedStatement MySqlDefinitionUpdateSellsTable(Item item, String customerId, String DateAndTime) throws SQLException, ClassNotFoundException {

        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO store.sells values(?,?,?,?,?,?,?)");
        preparedStatement.setString(1, item.getItemType());
        preparedStatement.setString(2, item.getItemPartNumber());
        preparedStatement.setString(3, item.getItemBranch());
        preparedStatement.setString(4, item.getItemUnitPrice());
        preparedStatement.setString(5, item.getItemSize());
        preparedStatement.setString(6, customerId);
        preparedStatement.setString(7, DateAndTime);

        return preparedStatement;
    }

    private String WhatIsTheCurrentTimeAndDate() {

        java.util.Date date = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String currentTime = sdf.format(date);

        return currentTime;
    }

    private void CheckIfItemInStorage( ) {
        try {

            Statement statement = null;

            Item item = new Item();

            String employee_type = GetResponseItemFromClient(item);

            String response = MySqlDefinitionAndItemQuery(item,statement,employee_type);

            PrepareAndSendJsonDataItemToClient(response,item,statement);

        } catch (Exception ex) {
///////////////////////////////////////
        }
    }

    private String GetResponseItemFromClient(Item item) {

        String employee_type = (String) jsonObject.get("employee type");
        item.setItemPartNumber((String) jsonObject.get("item part number"));
        item.setItemSize((String)jsonObject.get("item size"));
        item.setItemBranch((String) jsonObject.get("item branch"));
        item.setItemType((String) jsonObject.get("item type"));

        return employee_type;
    }

    private void PrepareAndSendJsonDataItemToClient(String response, Item item, Statement statement) throws IOException, SQLException {

        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("Failed", response);
        jsonObjectResponse.put("item type", item.getItemType());
        jsonObjectResponse.put("item size", item.getItemSize());
        jsonObjectResponse.put("item branch", item.getItemBranch());
        jsonObjectResponse.put("item amount", item.getItemAmount());
        jsonObjectResponse.put("item part number", item.getItemPartNumber());
        jsonObjectResponse.put("item unit price", item.getItemUnitPrice());

        outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
        outputStream.flush();

        statement.close();
    }

    private String MySqlDefinitionAndItemQuery(Item item, Statement statement, String employee_type) throws ClassNotFoundException, SQLException {

        statement = conn.createStatement();

        String query = String.format("select item_type,item_size,item_branch,item_amount,item_price,item_part_number from store.storage where item_part_number='%s' and item_size='%s' and item_branch='%s' and item_type='%s'", item.getItemPartNumber(),item.getItemSize(),item.getItemBranch(),item.getItemType());

        ResultSet responseFromDB = statement.executeQuery(query);

        String response = CheckIfItemInDB(item,responseFromDB,employee_type);

        statement.close();

        return response;
    }

    private String CheckIfItemInDB(Item item, ResultSet responseFromDB, String employee_type) throws SQLException {

        String response = "";

        if (responseFromDB.next()) {

            response = InsertOtherParametersFromDBToItem(item,responseFromDB,employee_type);
        }

        else
            response = "No item in your branch!!!";

        return response;
    }

    private String InsertOtherParametersFromDBToItem(Item item, ResultSet responseFromDB, String employee_type) throws SQLException {

        String response = "";

        item.setItemType(responseFromDB.getString("item_type"));
        item.setItemSize(responseFromDB.getString("item_size"));
        item.setItemBranch(responseFromDB.getString("item_branch"));
        item.setItemAmount(responseFromDB.getString("item_amount"));
        item.setItemUnitPrice(responseFromDB.getString("item_price"));
        item.setItemPartNumber(responseFromDB.getString("item_part_number"));

        if(item.getItemAmount().equals("0")) {

            if (!(employee_type.equals("Shift Manager")))
                response = "Out of storage!!! Ask your shift manager to renew branch storage";
            else
                response = "Out of storage!!! Renew this item: " + item.getItemType() + ", " + "Size: " + item.getItemSize();
        }
        return response;
    }

    private String ReadAndParseData() throws IOException, ParseException {

        String receivedMessage = inputStream.readUTF();
        JSONParser jsonParser = new JSONParser();
        jsonObject = (JSONObject) jsonParser.parse(receivedMessage);

        String serverOperation = (String) jsonObject.get("GuiName");

        return serverOperation;
    }

    private void DefineCommunicationBuffer() throws IOException {

        //Create InputStream to receive messages send by the client
        inputStream = new DataInputStream(sslServerSocket.getInputStream());
        //Create OutputStream to send message to client
        outputStream = new DataOutputStream(sslServerSocket.getOutputStream());

    }

    private static void EmployeeListResponse(JSONObject jsonObject,DataOutputStream outputStream) {

        try {

            String branchName = (String) jsonObject.get("branch");

            Statement statement = conn.createStatement();
            String query = String.format("select emp_sn,emp_name,emp_id,emp_tel,emp_bank,emp_type from store.Employee where emp_branch = '%s'", branchName);
            ResultSet branchStorageFromDB = statement.executeQuery(query);

            //converter of ResultSet into JSONArray : all the specific branch storage
            JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(branchStorageFromDB);  // private function
            outputStream.writeUTF(jsonArrayResponse.toString() + "\n");
            outputStream.flush();

            statement.close();
        } catch (Exception e) {
            System.out.println(e);

        }
    }

    private static void UpdateCustomer(DataOutputStream outputStream, JSONObject jsonObject) {

        try {
                String custStr = (String) jsonObject.get("customer");

                String responseFromDB = QueryUpdateCustomerAccordingToID(custStr);

                SendUpdateCustomerAnswerToClient(outputStream,responseFromDB);

        } catch (Exception ex) {

        }
    }

    private static void SendUpdateCustomerAnswerToClient(DataOutputStream outputStream, String responseFromDB) throws IOException {

        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("Failed", responseFromDB);
        outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
        outputStream.flush();
    }

    private static String QueryUpdateCustomerAccordingToID(String custStr){

        String response = "";

        try {
                ObjectMapper objectMapper = new ObjectMapper();
                Customer customer = objectMapper.readValue(custStr, Customer.class);

                String query = "update store.customer set cust_name = ?, cust_tel = ?, cust_type = ? where cust_id = ?";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString(1, customer.getCustName());
                preparedStmt.setString(2, customer.getCustTel());
                preparedStmt.setString(3, customer.getCustType());
                preparedStmt.setString(4, customer.getCustId());

                preparedStmt.execute();

        } catch (Exception ex) {
            if (ex.toString().contains("PRIMARY")) {
                // response = "Customer already exists";/////////////////////////////
            }
        }
        return response;
    }

    private static void IsCustomerInDB(JSONObject jsonObject, DataOutputStream outputStream) {

        try {

            Customer customer = new Customer();

            customer.setCustId((String) jsonObject.get("customerId"));

            String responseFromDB = QueryIsCustomerExsistFromDBAccordingToID(customer);

            SendIsClientInDBAnswerToClient(outputStream,responseFromDB,customer);

        } catch (Exception ex) {

        }
    }

    private static void SendIsClientInDBAnswerToClient(DataOutputStream outputStream,String response, Customer customer) throws IOException {

        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("Failed", response);
        jsonObjectResponse.put("customer id", customer.getCustId());
        jsonObjectResponse.put("customer name", customer.getCustName());
        jsonObjectResponse.put("customer tel", customer.getCustTel());
        jsonObjectResponse.put("customer type", customer.getCustType());

        outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
        outputStream.flush();
    }

    private static String QueryIsCustomerExsistFromDBAccordingToID(Customer customer) throws ClassNotFoundException, SQLException {

        String response = "";

        Statement statement = conn.createStatement();
        String query = String.format("select cust_id,cust_name,cust_tel,cust_type from store.customer where cust_id='%s'", customer.getCustId());
        ResultSet responseFromDB = statement.executeQuery(query);

        if (responseFromDB.next()) {
            customer.setCustName(responseFromDB.getString("cust_name"));
            customer.setCustTel(responseFromDB.getString("cust_tel"));
            customer.setCustType(responseFromDB.getString("cust_type"));
        } else {
            response = "Customer ID: " + customer.getCustId() + " is not found !!!";
        }

        statement.close();

        return response;
    }

    private static void CustomerListResponse(DataOutputStream outputStream) {

        try {

            Statement statement = null;

            ResultSet customersListFromDB = QueryCustomersListFromDBAccordingToBranchName(statement);

            //converter of ResultSet into JSONArray : all the specific branch storage
            JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(customersListFromDB);  // private function

            SendTheCustomersListToClient(jsonArrayResponse,outputStream,statement);

             } catch (Exception e) {
            System.out.println(e);
        }

    }

    private static void SendTheCustomersListToClient(JSONArray jsonArrayResponse,DataOutputStream outputStream,Statement statement) throws IOException, SQLException {

            outputStream.writeUTF(jsonArrayResponse.toString() + "\n");
            outputStream.flush();

            statement.close();
            }

    private static ResultSet QueryCustomersListFromDBAccordingToBranchName(Statement statement) throws ClassNotFoundException, SQLException {

        statement = conn.createStatement();
        String query = "select * from store.Customer";
        ResultSet customersListFromDB = statement.executeQuery(query);

        return customersListFromDB;
    }

    private static void RegisterCustomer(JSONObject jsonObject, DataOutputStream outputStream) {

        try {

            String responseFromDB = QueryRegisterCustomerFromDB(jsonObject);

            SendClientRegisterResponseToClient(responseFromDB,outputStream);

        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    private static void SendClientRegisterResponseToClient(String responseFromDB, DataOutputStream outputStream) throws IOException {

        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("Failed", responseFromDB);
        outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
        outputStream.flush();
    }

    private static String QueryRegisterCustomerFromDB(JSONObject jsonObject) throws ClassNotFoundException, SQLException, IOException {

        String response = "";
        String custStr = (String) jsonObject.get("customer");
        ObjectMapper objectMapper = new ObjectMapper();
        Customer customer = objectMapper.readValue(custStr, Customer.class);

        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO store.customer values(?,?,?,?)");
        preparedStatement.setString(1, customer.getCustId());
        preparedStatement.setString(2, customer.getCustName());
        preparedStatement.setString(3, customer.getCustTel());
        preparedStatement.setString(4, customer.getCustType());

        try {
            preparedStatement.execute();
        } catch (Exception ex) {
            if (ex.toString().contains("PRIMARY")) {
                response = "Error !!! Unsuccess to register new customer ID: " + customer.getCustId();
            }
        }
        preparedStatement.close();

        return response;
    }

    private static void StorageResponse(JSONObject jsonObject, DataOutputStream outputStream) {

        try {

            Statement statement = null;

            ResultSet branchStorageFromDB = QueryStorageFromDBAccordingToBranchName(jsonObject,statement);

            //converter of ResultSet into JSONArray : all the specific branch storage
            JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(branchStorageFromDB);

            SendTheStorageToClient(jsonArrayResponse,outputStream,statement);

        } catch (Exception e) {
            System.out.println(e);

        }

    }

    private static void SendTheStorageToClient(JSONArray jsonArrayResponse,DataOutputStream outputStream,Statement statement) throws IOException, SQLException {

        outputStream.writeUTF(jsonArrayResponse.toString() + "\n");
        outputStream.flush();

        statement.close();
    }

    private static ResultSet QueryStorageFromDBAccordingToBranchName(JSONObject jsonObject, Statement statement) throws ClassNotFoundException, SQLException {

        String branchNum = (String) jsonObject.get("branch");
        statement = conn.createStatement();
        String query = String.format("select item_type, item_size, item_amount, item_part_number from store.Storage where item_branch='%s' ", branchNum);
        ResultSet branchStorageFromDB = statement.executeQuery(query);

        return branchStorageFromDB;
    }

    private static void RegisterEmployee(JSONObject jsonObject, DataOutputStream outputStream) {
        try {

            String empStr = (String) jsonObject.get("employee");

            ObjectMapper objectMapper = new ObjectMapper();
            Employee employee = objectMapper.readValue(empStr, Employee.class);

            String response = MySqlDefinitionAndEmployeeSnQuery(employee);

            SendRegisterEmployeeResponseToClient(employee,response,outputStream);

        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    private static String MySqlDefinitionAndEmployeeSnQuery(Employee employee) throws SQLException, ClassNotFoundException, IOException {

        Statement statement = conn.createStatement();
        String query = "select max(emp_sn) as empSn from store.employee";

        ResultSet resultSet = statement.executeQuery(query);

        if (resultSet.next()) {

            InsertTheNewSnEmployee(resultSet, employee);
        }
        resultSet.close();

        String response = PrepareMySqlQueryToInsertTheNewEmployeeToDB(conn, employee);

        return response;
    }

    private static void SendRegisterEmployeeResponseToClient(Employee employee, String response, DataOutputStream outputStream) throws IOException {

        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("Failed", response);
        jsonObjectResponse.put("emp sn", employee.getEmpSn());
        outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
        outputStream.flush();
    }

    private static String PrepareMySqlQueryToInsertTheNewEmployeeToDB(Connection conn, Employee employee) throws SQLException, FileNotFoundException {

        String response = "";

        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO store.employee values(?,?,?,?,?,?,?,?,?,?,?)");
        preparedStatement.setString(1, employee.getEmpSn());
        preparedStatement.setString(2, employee.getEmpName());
        preparedStatement.setString(3, employee.getEmpId());
        preparedStatement.setString(4, employee.getEmpTel());
        preparedStatement.setString(5, employee.getEmpBank());
        preparedStatement.setString(6, employee.getEmpBranch());
        preparedStatement.setString(7, employee.getEmpType());
        preparedStatement.setString(8, employee.getEmpPass());
        preparedStatement.setString(9, employee.getEmpPassSalt());
        preparedStatement.setString(10, employee.getEmpStatus());

        InputStream inputStream = new FileInputStream(new File(employee.getEmpPhoto()));

        preparedStatement.setBlob(11,inputStream);
        try {
            preparedStatement.execute();
        } catch (Exception ex) {
            if (ex.toString().contains("PRIMARY")) {
                response = "Employee already exists";
            }
        }
        preparedStatement.close();

        return response;
    }

    private static void InsertTheNewSnEmployee(ResultSet resultSet, Employee employee) throws SQLException {

        String maxSn = resultSet.getString("empSn");
        int max = Integer.parseInt(maxSn) + 1;
        maxSn = "" + max;
        employee.setEmpSn(maxSn);
    }

    private static void LogInEmployee(JSONObject jsonObject, DataOutputStream outputStream) {
        try {

            Statement statement = null;

            Employee employee = GetLogInResponseFromClient(jsonObject);

            String response = MySqlDefinitionAndQueryForLogIn(employee,statement);

            PrepareAndSendJsonDataToClient(response,employee,outputStream,statement);

        } catch (Exception ex) {

        }
    }

    private static void PrepareAndSendJsonDataToClient(String response,Employee employee,DataOutputStream outputStream,Statement statement) throws IOException, SQLException {
        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("Failed", response);
        jsonObjectResponse.put("employee type", employee.getEmpType());
        jsonObjectResponse.put("employee name", employee.getEmpName());
        jsonObjectResponse.put("employee sn", employee.getEmpSn());
        jsonObjectResponse.put("employee bank", employee.getEmpBank());
        jsonObjectResponse.put("employee tel", employee.getEmpTel());
        jsonObjectResponse.put("employee photo", employee.getEmpPhoto());

        outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
        outputStream.flush();

        statement.close();
    }

    private static String MySqlDefinitionAndQueryForLogIn(Employee employee,Statement statement) throws ClassNotFoundException, SQLException, IOException {

        statement = conn.createStatement();

        String query = String.format("select emp_type,emp_name,emp_sn,emp_bank,emp_tel,emp_securePass,emp_passSalt,emp_status,emp_photo from store.employee where emp_id='%s' and emp_branch='%s'", employee.getEmpId(),employee.getEmpBranch());

        ResultSet responseFromDB = statement.executeQuery(query);

        String response = CheckIfEmployeeIdPlusBranchExistInDB(employee,responseFromDB);

        statement.close();

        return response;
    }

    private static String CheckIfEmployeeIdPlusBranchExistInDB(Employee employee,ResultSet responseFromDB) throws SQLException, IOException {

        String response = "";

        if (responseFromDB.next()) {

            String emp_status = responseFromDB.getString("emp_status");

            PreparePhotoToEmployeeObject(employee, responseFromDB);

            if(!IsUserAlreadyConnectFromOtherComputer(emp_status)) {

                String inputPasswordFromUser = employee.getEmpPass();
                response = CheckIfInputPasswordIsOk(inputPasswordFromUser, responseFromDB, employee);
                }
            else{
                response =  "EMPLOYEE ID: " + employee.getEmpId() + " - Can not connect. There is already connected somewhere else !!!";
                }
            }

        else{
            response = "Your Employee ID ,Password or Branch name is incorrect !!!";
        }
        return response;
    }

    private static void PreparePhotoToEmployeeObject(Employee employee,ResultSet responseFromDB) throws SQLException, IOException {

        // חילוץ הבלוב של התמונה מהריסאלטסט והפיכתו לסטרינג והכנסה לאמפלויי
        Base64 codec = new Base64();
        String photo64;

        InputStream photois = responseFromDB.getBinaryStream("emp_photo");
        byte[] bytes = IOUtils.toByteArray(photois);
        photo64 = codec.encodeBase64String(bytes);

        employee.setEmpPhoto(photo64);
    }

    private static boolean IsUserAlreadyConnectFromOtherComputer(String employee_status) {

        if(employee_status.equals("IN"))
            return true;
        return false;
    }

    private static String CheckIfInputPasswordIsOk(String inputPasswordFromUser,ResultSet responseFromDB,Employee employee) throws SQLException {

            String response = "";

            employee.setEmpPass(responseFromDB.getString("emp_securePass"));
            employee.setEmpPassSalt(responseFromDB.getString("emp_passSalt"));

            boolean passwordMatch = PasswordUtils.verifyUserPassword(inputPasswordFromUser, employee.getEmpPass(), employee.getEmpPassSalt());

            if(passwordMatch) {
                InsertOtherParametersFromDBToEmployee(employee, responseFromDB);
                ChangeStatusOfEmployeeToIN(employee.getEmpId());
            }
            else
                response = "Your Employee ID ,Password or Branch name is incorrect !!!";
        return response;
    }

    private static void ChangeStatusOfEmployeeToIN(String empId) {

        try {
            String query = "update store.employee set emp_status = 'IN' where emp_id = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, empId);
            preparedStmt.execute();

        } catch (Exception ex) {

        }
    }

    private static void InsertOtherParametersFromDBToEmployee(Employee employee, ResultSet responseFromDB) throws SQLException {

        employee.setEmpType(responseFromDB.getString("emp_type"));
        employee.setEmpName(responseFromDB.getString("emp_name"));
        employee.setEmpSn(responseFromDB.getString("emp_sn"));
        employee.setEmpBank(responseFromDB.getString("emp_bank"));
        employee.setEmpTel(responseFromDB.getString("emp_tel"));
    }

    private static Employee GetLogInResponseFromClient(JSONObject jsonObject) {

        Employee employee = new Employee();

        employee.setEmpId((String) jsonObject.get("employee id"));
        employee.setEmpPass((String)jsonObject.get("employee pass"));
        employee.setEmpBranch((String) jsonObject.get("employee branch"));

        return employee;

    }

    public static JSONArray ConvertResultSetToJsonArray(ResultSet resultSet) throws Exception {

        // Every item open num of rows in the JSONArray according to num of columns

        JSONArray jsonArray = new JSONArray();

        while (resultSet.next()) {

            int total_rows = resultSet.getMetaData().getColumnCount();

            JSONObject obj = new JSONObject();

            for (int i = 0; i < total_rows; i++) {

                obj.put(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));
                jsonArray.add(obj);
            }
        }
        return jsonArray;
    }

    private String GetIdToLogOutFromClient(JSONObject jsonObject) {

        String emp_id = (String) jsonObject.get("employee id");

        return emp_id;

    }

    public static void main(String[] args) throws Exception {

        /*Adding the JSSE (Java Secure Socket Extension) provider which provides SSL and TLS protocols
        and includes functionality for data encryption, server authentication, message integrity,
        and optional client authentication.*/
        Security.addProvider(new Provider());
        //specifying the keystore file which contains the certificate/public key and the private key
        System.setProperty(KEYSTORE, KEYSTORE_FILE_DIRECTORY);
        //specifying the password of the keystore file
        System.setProperty(KEYSTORE_PASSWORD, KEYSTORE_PASSWORD_FILE);
        //This optional and it is just to show the dump of the details of the handshake process
        System.setProperty(HANDSHAKE_DEBUG, DEFINITION_HANDSHAKE_DEBUG);

        //SSLServerSocketFactory establishes the ssl context and and creates SSLServerSocket
        SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        //Create SSLServerSocket using SSLServerSocketFactory established ssl context
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(PORT);

        while (true) {

            //Wait for the SSL client to connect to this server
            SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

            Server server = new Server(sslSocket);
            Thread serverThread = new Thread(server);
            serverThread.start();
        }
    }
}
















