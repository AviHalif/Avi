import Classes.Customer;
import Classes.Employee;
import Classes.Item;
import Classes.PasswordUtils;
import com.sun.net.ssl.internal.ssl.Provider;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.util.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    private static Logger logger = Logger.getLogger(Server.class.getName());
    private static Connection conn;
    private Properties properties;
    private InputStream inStream;
    private String propFileName;
    private SSLSocket sslServerSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private JSONObject jsonObject;

    public String mysql_url, mysql_user, mysql_password,mysql_class;

    //The Port number through which this server will accept client connections
    public static final int PORT = 35786;
    public static final String KEYSTORE = "javax.net.ssl.keyStore";
    public static final String KEYSTORE_FILE_DIRECTORY =  "C:/Users/AviHalif/IdeaProjects/Avi/src/myKeyStore.jks";
    public static final String KEYSTORE_PASSWORD = "javax.net.ssl.keyStorePassword";
    public static final String KEYSTORE_PASSWORD_FILE = "java13579";
    public static final String HANDSHAKE_DEBUG = "javax.net.debug";
    public static final String DEFINITION_HANDSHAKE_DEBUG = "all";


    public Server(SSLSocket sslServerSocket) throws IOException {

        DefineLogAndConfig();

        System.out.println ("Accepted from " + sslServerSocket.getInetAddress().getHostAddress());

        mysql_url = properties.getProperty("DBUrl");
        mysql_user = properties.getProperty("DBUser");
        mysql_password = properties.getProperty("DBPassword");
        mysql_class = properties.getProperty("Class.forName");

        try {
            Class.forName(mysql_class);
            conn = DriverManager.getConnection(mysql_url, mysql_user, mysql_password);

            this.sslServerSocket = sslServerSocket;

            DefineCommunicationBuffer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {

            String serverOperation = ReadAndParseData();

            ChooseServerOperation(serverOperation);

        } catch (Exception e) {
            logger.error(e);
        }

        finally {
            System.out.println("Disconected");
        }
    }

    private void DefineLogAndConfig() throws IOException {

        properties = new Properties();
        propFileName = "config.properties";
        inStream = Server.class.getClassLoader().getResourceAsStream(propFileName);
        properties.load(inStream);

        String log4jConfigFile = System.getProperty("user.dir")+ File.separator+"src"+ File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
    }

    private void ChooseServerOperation(String serverOperation) {

        switch (serverOperation) {

            case "CheckOnlineUsers":{
                OnlineUsersResponse();
                break;
            }

            case "Log in": {
                LogInEmployee(jsonObject, outputStream);
                break;
            }

            case "Register": { // Register new employee
                RegisterEmployee(jsonObject, outputStream);
                break;
            }

            case "Storage": { // Return the storage of a specific branch
                StorageResponse(jsonObject, outputStream);
                break;
            }

            case "Customer List": { // Return all the customers list
                CustomerListResponse(outputStream);
                break;
            }

            case "Check employee already exists": {
                CheckIfEmployeeAlreadyExists();
                break;
            }

            case "Customer register": { // Register new customer
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

            case "EmployeeList": {  // Return all the employee list
                EmployeeListResponse(jsonObject, outputStream);
                break;
            }

            case "EmployeeEdit": {
                EditEmployee(jsonObject, outputStream);
                break;
            }

            case "UpdateEmployee": { // Change the values of a specific employee in the DB
                UpdateEmployee(jsonObject);
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
                ReturnPhotosLocation();
                break;
            }

            case "Report": { // Return a relevant parameters for report
                ReportResponse();
                break;
            }

            case "All Items Names": { // Return all the items list
                ReturnAllNamesOfItems();
                break;
            }

            case "Log out": {
                LogOutEmployee();
                break;
            }
        }
    }

    private void ReturnPhotosLocation() {
        try {
            Statement statement = conn.createStatement();

            String query = String.format("select item_path from store.all_items");
            ResultSet allKindOfItems = statement.executeQuery(query);

            JSONArray jsonArray = new JSONArray();

            outputStream.writeUTF(ResultSetJson(allKindOfItems,jsonArray) + "\n");
            outputStream.flush();
            statement.close();

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void OnlineUsersResponse(){
        try {
            String empId = (String) jsonObject.get("Me");
            Statement statement = conn.createStatement();
            String query = String.format("select emp_name,emp_sn from store.employee where emp_status = 'IN' and emp_id<>'%s'",empId);
            ResultSet resultSet = statement.executeQuery(query);

            JSONArray jsonArray = new JSONArray();
            outputStream.writeUTF(ResultSetJson(resultSet,jsonArray).toString()+"\n");
            outputStream.flush();

            statement.close();
        }
        catch (Exception ex){
        }
    }

    private void UpdateEmployee(JSONObject jsonObject ) {

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

            preparedStmt.execute();

            preparedStmt.close();

            SendUpdateEmployeeResponseToClient(response);

        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private void SendUpdateEmployeeResponseToClient(String response) {

        try {
            JSONObject jsonObjectResponse = new JSONObject();
            jsonObjectResponse.put("Failed", response);
            outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
            outputStream.flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
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

    private PreparedStatement MySqlQueryToUpdateWithoutPass(Connection conn, Employee employee) throws SQLException {

        String query = "update store.employee set emp_name = ?, emp_tel = ?, emp_type = ?, emp_bank = ?, emp_photo = ? where emp_id = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, employee.getEmpName());
        preparedStmt.setString(2, employee.getEmpTel());
        preparedStmt.setString(3, employee.getEmpType());
        preparedStmt.setString(4, employee.getEmpBank());
        try {
            InputStream inputStream = new FileInputStream(new File(employee.getEmpPhoto()));
            preparedStmt.setBlob(5, inputStream);
        }
        catch (Exception ex){
            logger.error(ex);
        }

        preparedStmt.setString(6, employee.getEmpId());

        return preparedStmt;
    }

    private PreparedStatement MySqlQueryToUpdatePssAndPhotoBoth(Connection conn, Employee employee) throws SQLException {

        String query = "update store.employee set emp_name = ?, emp_tel = ?, emp_type = ?, emp_bank = ?, emp_securePass = ?, emp_passSalt = ?, emp_photo = ? where emp_id = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, employee.getEmpName());
        preparedStmt.setString(2, employee.getEmpTel());
        preparedStmt.setString(3, employee.getEmpType());
        preparedStmt.setString(4, employee.getEmpBank());
        preparedStmt.setString(5, employee.getEmpPass());
        preparedStmt.setString(6, employee.getEmpPassSalt());

        try {
            InputStream inputStream = new FileInputStream(new File(employee.getEmpPhoto()));
        }
        catch (Exception ex){
            logger.error(ex);
        }

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

                InsertAllEditEmployeeParametersToJesonAndSendToClient(employee, response);
            }
            else{

                response = "User ID: " + employee.getEmpId() + " is in the terminal now, you can't change details when the user status is online";
                JSONObject jsonObjectResponse = new JSONObject();
                jsonObjectResponse.put("Failed", response);
                outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
                outputStream.flush();
            }
        }
        catch (Exception ex) {
            logger.error(ex);
        }
    }

    private void InsertAllEditEmployeeParametersToJesonAndSendToClient(Employee employee, String response) {

        try {
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
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private String ConvertPhotoBeforeInsertToJson(ResultSet resultSet) {

        Base64 codec = new Base64();
        String photo64 = "";

        try {
            InputStream photois = resultSet.getBinaryStream("emp_photo");
            byte[] bytes = IOUtils.toByteArray(photois);
            photo64 = codec.encodeBase64String(bytes);

            return photo64;
        }
        catch (Exception ex){
            logger.error(ex);
        }

        return  photo64;
    }

    private String MySqlDefinitionAndQueryForEditEmployee(Employee employee) {

        String response = "";

        try {
            Statement statement = conn.createStatement();
            String query = String.format("select emp_sn,emp_name,emp_tel,emp_bank,emp_branch,emp_type,emp_photo,emp_status from store.employee where emp_id='%s' and emp_branch='%s'", employee.getEmpId(), employee.getEmpBranch());
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {

                GetDataFromResultSetToEmployeeObject(employee, resultSet);
                employee.setEmpPhoto(ConvertPhotoBeforeInsertToJson(resultSet));

            } else {
                response = "Customer ID: " + employee.getEmpId() + " is not found !!!";
            }

            statement.close();

            return response;
        }
        catch (Exception ex){
            logger.error(ex);
        }
        return response;
    }

    private void GetDataFromResultSetToEmployeeObject(Employee employee,ResultSet resultSet) {

        try {
            employee.setEmpSn(resultSet.getString("emp_sn"));
            employee.setEmpName(resultSet.getString("emp_name"));
            employee.setEmpTel(resultSet.getString("emp_tel"));
            employee.setEmpBank(resultSet.getString("emp_bank"));
            employee.setEmpBranch(resultSet.getString("emp_branch"));
            employee.setEmpType(resultSet.getString("emp_type"));
            employee.setEmpStatus(resultSet.getString("emp_status"));
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private void ReturnAllKindsOfItems() {

        try {

            ResultSet allKindOfItemsFromDB = QueryToDbForAllKindOfItems();

            if (allKindOfItemsFromDB.next()) {

                allKindOfItemsFromDB.beforeFirst();

                //converter of ResultSet into JSONArray : all the specific branch storage
                JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(allKindOfItemsFromDB);

                SendAllKindOfItemsToClient(jsonArrayResponse, outputStream);

            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void ReturnAllNamesOfItems() {

        try {

            ResultSet allKindOfItemsFromDB = QueryToDbForAllNamesOfItems();

            if (allKindOfItemsFromDB.next()) {

                allKindOfItemsFromDB.beforeFirst();

                //converter of ResultSet into JSONArray : all the specific branch storage
                JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(allKindOfItemsFromDB);

                SendAllKindOfItemsToClient(jsonArrayResponse, outputStream);
            }

        } catch (Exception e) {
            logger.error(e);

        }
    }

    private void SendAllKindOfItemsToClient(JSONArray jsonArrayResponse, DataOutputStream outputStream){

        try {
            outputStream.writeUTF(jsonArrayResponse.toString() + "\n");
            outputStream.flush();

        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private ResultSet QueryToDbForAllKindOfItems() throws SQLException {

        Statement statement = conn.createStatement();
        String query = String.format("select item_name,item_number,item_price from store.all_items");
        ResultSet allKindOfItems = statement.executeQuery(query);

        return allKindOfItems;
    }

    private ResultSet QueryToDbForAllNamesOfItems() throws SQLException {

        Statement statement = conn.createStatement();
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
        }
        catch (Exception ex) {
            logger.error(ex);
        }
    }

    private void LogOutEmployee() {

        try {

            String emp_id = GetIdToLogOutFromClient(jsonObject);

            MySqlDefinitionAndQueryToLogOut(emp_id);

        }
        catch (Exception ex) {
            logger.error(ex);
        }
    }

    private void MySqlDefinitionAndQueryToLogOut(String emp_id) {

        try {

            String query = "update store.employee set emp_status = 'OUT' where emp_id = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, emp_id);
            preparedStmt.execute();
        }
        catch (Exception ex) {
            logger.error(ex);
        }
    }

    private void ReportResponse() {

        try {

            String branch = (String) jsonObject.get("branch");
            String type = (String) jsonObject.get("type");

            MySqlDefinitionAndQueryForItemsReportDetails(branch,type);

        }
        catch (Exception ex) {
            logger.error(ex);
        }
    }

    private void PrepareAndSendJsonReportDataToClient(ResultSet resultSet) {

        try{
            JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(resultSet);

            outputStream.writeUTF(jsonArrayResponse.toString()+"\n");

            outputStream.flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private void MySqlDefinitionAndQueryForItemsReportDetails(String branch, String type)   {

        try {
            Statement statement = conn.createStatement();
            String query;

            if (type.equals("all")) {
                query = String.format("select cust_id,item_part_number,item_type,item_size,item_price,sell_time from store.sells where substr(sell_time,1,10) = substr(now(),1,10) and branch_name='%s' ", branch);
            } else {
                query = String.format("select cust_id,item_part_number,item_type,item_size,item_price,sell_time from store.sells where substr(sell_time,1,10) = substr(now(),1,10) and branch_name='%s' and item_type in(%s)", branch, type);
            }

            ResultSet resultSet = statement.executeQuery(query);

            PrepareAndSendJsonReportDataToClient(resultSet);

            statement.close();
        }
        catch (Exception ex){
            logger.error(ex);
        }
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
            logger.error(ex);
        }
    }

    private String MySqlDefinitionSellItem(Item item, String customerId) {

        String response = "";

        try{
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

            preparedStmt.execute();
            UpdateSellsTable(item, customerId); // פונקצית עזר לעדכון כל מכירה שהיתה

            preparedStmt.close();

            return response;
        }
        catch (Exception ex) {

            if (ex.toString().contains("PRIMARY")) {
                response = "Error to purchase this item!!!";
            }
            else
                logger.error(ex);
        }

        return response;
    }

    private void GetResponseSellItemFromClient(Item item) {

        item.setItemPartNumber((String) jsonObject.get("item part number"));
        item.setItemUnitPrice((String) jsonObject.get("item price"));
        item.setItemSize((String) jsonObject.get("item size"));
        item.setItemBranch((String) jsonObject.get("item branch"));
        item.setItemType((String) jsonObject.get("item type"));

    }

    private void CheckItemAmountFromDB(Item item) {

        try {
            Statement statement = conn.createStatement();
            String query = String.format("select item_amount from store.storage where item_part_number='%s' and item_size='%s' and item_branch='%s'", item.getItemPartNumber(), item.getItemSize(), item.getItemBranch());
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String itemAmount = resultSet.getString("item_amount");
                item.setItemAmount(itemAmount);
            }
            statement.close();
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private void UpdateSellsTable(Item item,String customerId) {

        String DateAndTime = WhatIsTheCurrentTimeAndDate();

        try {

            PreparedStatement preparedStatement = MySqlDefinitionUpdateSellsTable(item, customerId, DateAndTime);

            try {
                preparedStatement.execute();
            } catch (Exception ex) {
                if (ex.toString().contains("PRIMARY")) {
                }
            }
            outputStream.flush();
        }
        catch (Exception ex) {
            logger.error(ex);
        }
    }

    private PreparedStatement MySqlDefinitionUpdateSellsTable(Item item, String customerId, String DateAndTime) throws SQLException {

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

            Item item = new Item();

            String employee_type = GetResponseItemFromClient(item);

            String response = MySqlDefinitionAndItemQuery(item, employee_type);

            PrepareAndSendJsonDataItemToClient(response,item);

        } catch (Exception ex) {
            logger.error(ex);
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

    private void PrepareAndSendJsonDataItemToClient(String response, Item item) {

        try {
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

        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private String MySqlDefinitionAndItemQuery(Item item, String employee_type) throws  SQLException {

        Statement statement = conn.createStatement();

        String query = String.format("select item_type,item_size,item_branch,item_amount,item_price,item_part_number from store.storage where item_part_number='%s' and item_size='%s' and item_branch='%s' and item_type='%s'", item.getItemPartNumber(),item.getItemSize(),item.getItemBranch(),item.getItemType());

        ResultSet responseFromDB = statement.executeQuery(query);

        String response = CheckIfItemInDB(item,responseFromDB,employee_type);

        statement.close();

        return response;
    }

    private String CheckIfItemInDB(Item item, ResultSet responseFromDB, String employee_type)  {

        String response = "";

        try {
            if (responseFromDB.next()) {

                response = InsertOtherParametersFromDBToItem(item, responseFromDB, employee_type);
            }
            else
                response = "No item in your branch!!!";
        }
        catch (Exception ex){
            logger.error(ex);
        }

        return response;
    }

    private String InsertOtherParametersFromDBToItem(Item item, ResultSet responseFromDB, String employee_type) {

        String response = "";

        try {
            item.setItemType(responseFromDB.getString("item_type"));
            item.setItemSize(responseFromDB.getString("item_size"));
            item.setItemBranch(responseFromDB.getString("item_branch"));
            item.setItemAmount(responseFromDB.getString("item_amount"));
            item.setItemUnitPrice(responseFromDB.getString("item_price"));
            item.setItemPartNumber(responseFromDB.getString("item_part_number"));

            if (item.getItemAmount().equals("0")) {

                if (!(employee_type.equals("Shift Manager")))
                    response = "Out of storage!!! Ask your shift manager to renew branch storage";
                else
                    response = "Out of storage!!! Renew this item: " + item.getItemType() + ", " + "Size: " + item.getItemSize();
            }
        }
        catch (Exception ex){
            logger.error(ex);
        }

        return response;
    }

    private String ReadAndParseData() {

        String serverOperation = "";

        try {
            String receivedMessage = inputStream.readUTF();
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(receivedMessage);

            serverOperation = (String) jsonObject.get("GuiName");

            return serverOperation;
        }
        catch (Exception ex){
            logger.error(ex);
        }
        return serverOperation;
    }

    private void DefineCommunicationBuffer()  {

        try {
            //Create InputStream to receive messages send by the client
            inputStream = new DataInputStream(sslServerSocket.getInputStream());
            //Create OutputStream to send message to client
            outputStream = new DataOutputStream(sslServerSocket.getOutputStream());
        }
        catch (Exception ex){
            logger.error(ex);
        }

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
            logger.error(e);

        }
    }

    private static void UpdateCustomer(DataOutputStream outputStream, JSONObject jsonObject) {

        try {
            String custStr = (String) jsonObject.get("customer");

            String responseFromDB = QueryUpdateCustomerAccordingToID(custStr);

            SendUpdateCustomerAnswerToClient(outputStream,responseFromDB);

        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private static void SendUpdateCustomerAnswerToClient(DataOutputStream outputStream, String responseFromDB)  {

        try {
            JSONObject jsonObjectResponse = new JSONObject();
            jsonObjectResponse.put("Failed", responseFromDB);
            outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
            outputStream.flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
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
                response = "Customer already exists";
            }
            else
                logger.error(ex);
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
            logger.error(ex);
        }
    }

    private static void SendIsClientInDBAnswerToClient(DataOutputStream outputStream,String response, Customer customer)  {

        try {
            JSONObject jsonObjectResponse = new JSONObject();
            jsonObjectResponse.put("Failed", response);
            jsonObjectResponse.put("customer id", customer.getCustId());
            jsonObjectResponse.put("customer name", customer.getCustName());
            jsonObjectResponse.put("customer tel", customer.getCustTel());
            jsonObjectResponse.put("customer type", customer.getCustType());

            outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
            outputStream.flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private static String QueryIsCustomerExsistFromDBAccordingToID(Customer customer)  {

        String response = "";

        try {
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
        }
        catch (Exception ex){
            logger.error(ex);
        }

        return response;
    }

    private static void CustomerListResponse(DataOutputStream outputStream) {

        try {

            ResultSet customersListFromDB = QueryCustomersListFromDBAccordingToBranchName();

            //converter of ResultSet into JSONArray : all the specific branch storage
            JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(customersListFromDB);  // private function

            SendTheCustomersListToClient(jsonArrayResponse,outputStream);

        } catch (Exception e) {
            logger.error(e);
        }

    }

    private static void SendTheCustomersListToClient(JSONArray jsonArrayResponse,DataOutputStream outputStream) {

        try {
            outputStream.writeUTF(jsonArrayResponse.toString() + "\n");
            outputStream.flush();

        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private static ResultSet QueryCustomersListFromDBAccordingToBranchName() throws SQLException {

        Statement statement = conn.createStatement();
        String query = "select * from store.Customer";
        ResultSet customersListFromDB = statement.executeQuery(query);

        return customersListFromDB;
    }

    private static void RegisterCustomer(JSONObject jsonObject, DataOutputStream outputStream) {

        try {

            String responseFromDB = QueryRegisterCustomerFromDB(jsonObject);

            SendClientRegisterResponseToClient(responseFromDB,outputStream);

        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private static void SendClientRegisterResponseToClient(String responseFromDB, DataOutputStream outputStream) {

        try {
            JSONObject jsonObjectResponse = new JSONObject();
            jsonObjectResponse.put("Failed", responseFromDB);
            outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
            outputStream.flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private static String QueryRegisterCustomerFromDB(JSONObject jsonObject)  {

        String response = "";
        String custStr = (String) jsonObject.get("customer");
        ObjectMapper objectMapper = new ObjectMapper();
        Customer customer  = new Customer();

        try {
            customer = objectMapper.readValue(custStr, Customer.class);
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO store.customer values(?,?,?,?)");
            preparedStatement.setString(1, customer.getCustId());
            preparedStatement.setString(2, customer.getCustName());
            preparedStatement.setString(3, customer.getCustTel());
            preparedStatement.setString(4, customer.getCustType());

            preparedStatement.execute();

            preparedStatement.close();
        }

        catch (Exception ex) {
            if (ex.toString().contains("PRIMARY")) {
                response = "Error !!! Unsuccess to register new customer ID: " + customer.getCustId();
            }
            else
                logger.error(ex);
        }

        return response;
    }

    private static void StorageResponse(JSONObject jsonObject, DataOutputStream outputStream) {

        try {

            ResultSet branchStorageFromDB = QueryStorageFromDBAccordingToBranchName(jsonObject);

            //converter of ResultSet into JSONArray : all the specific branch storage
            JSONArray jsonArrayResponse = ConvertResultSetToJsonArray(branchStorageFromDB);

            SendTheStorageToClient(jsonArrayResponse,outputStream);

        } catch (Exception e) {
            logger.error(e);

        }

    }

    private static void SendTheStorageToClient(JSONArray jsonArrayResponse,DataOutputStream outputStream){

        try {
            outputStream.writeUTF(jsonArrayResponse.toString() + "\n");
            outputStream.flush();

        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private static ResultSet QueryStorageFromDBAccordingToBranchName(JSONObject jsonObject) throws SQLException {

        String branchNum = (String) jsonObject.get("branch");
        Statement statement = conn.createStatement();
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
            logger.error(ex);
        }
    }

    private static String MySqlDefinitionAndEmployeeSnQuery(Employee employee) {

        String response ="";

        try {
            Statement statement = conn.createStatement();
            String query = "select max(emp_sn) as empSn from store.employee";

            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {

                InsertTheNewSnEmployee(resultSet, employee);
            }
            resultSet.close();

            response = PrepareMySqlQueryToInsertTheNewEmployeeToDB(conn, employee);

            return response;
        }
        catch (Exception ex){
            logger.error(ex);
        }
        return response;
    }

    private static void SendRegisterEmployeeResponseToClient(Employee employee, String response, DataOutputStream outputStream)  {

        try {
            JSONObject jsonObjectResponse = new JSONObject();
            jsonObjectResponse.put("Failed", response);
            jsonObjectResponse.put("emp sn", employee.getEmpSn());
            outputStream.writeUTF(jsonObjectResponse.toString() + "\n");
            outputStream.flush();
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private static String PrepareMySqlQueryToInsertTheNewEmployeeToDB(Connection conn, Employee employee) {

        String response = "";

        try {
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

            preparedStatement.setBlob(11, inputStream);


            preparedStatement.execute();

            preparedStatement.close();
        }

        catch (Exception ex) {
            if (ex.toString().contains("PRIMARY")) {
                response = "Employee already exists";
            }
            else
                logger.error(ex);
        }
        return response;
    }

    private static void InsertTheNewSnEmployee(ResultSet resultSet, Employee employee) {

        try {
            String maxSn = resultSet.getString("empSn");
            int max = Integer.parseInt(maxSn) + 1;
            maxSn = "" + max;
            employee.setEmpSn(maxSn);
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private static void LogInEmployee(JSONObject jsonObject, DataOutputStream outputStream) {
        try {

            Employee employee = GetLogInResponseFromClient(jsonObject);

            String response = MySqlDefinitionAndQueryForLogIn(employee);

            PrepareAndSendJsonDataToClient(response,employee,outputStream);

        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private static void PrepareAndSendJsonDataToClient(String response,Employee employee,DataOutputStream outputStream) {
        try {
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
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private static String MySqlDefinitionAndQueryForLogIn(Employee employee)  {

        String response="";

        try {
            Statement statement = conn.createStatement();

            String query = String.format("select emp_type,emp_name,emp_sn,emp_bank,emp_tel,emp_securePass,emp_passSalt,emp_status,emp_photo from store.employee where emp_id='%s' and emp_branch='%s'", employee.getEmpId(), employee.getEmpBranch());

            ResultSet responseFromDB = statement.executeQuery(query);

            response = CheckIfEmployeeIdPlusBranchExistInDB(employee, responseFromDB);

            statement.close();
        }
        catch (Exception ex){
            logger.error(ex);
        }
        return response;
    }

    private static String CheckIfEmployeeIdPlusBranchExistInDB(Employee employee,ResultSet responseFromDB) {

        String response = "";

        try {
            if (responseFromDB.next()) {

                String emp_status = responseFromDB.getString("emp_status");

                PreparePhotoToEmployeeObject(employee, responseFromDB);

                if (!IsUserAlreadyConnectFromOtherComputer(emp_status)) {

                    String inputPasswordFromUser = employee.getEmpPass();
                    response = CheckIfInputPasswordIsOk(inputPasswordFromUser, responseFromDB, employee);
                } else {
                    response = "EMPLOYEE ID: " + employee.getEmpId() + " - Can not connect. There is already connected somewhere else !!!";
                }
            } else {
                response = "Your Employee ID ,Password or Branch name is incorrect !!!";
            }
        }

        catch (Exception ex){
            logger.error(ex);
        }
        return response;
    }

    private static void PreparePhotoToEmployeeObject(Employee employee,ResultSet responseFromDB) {

        try {
            Base64 codec = new Base64();
            String photo64;

            InputStream photois = responseFromDB.getBinaryStream("emp_photo");
            byte[] bytes = IOUtils.toByteArray(photois);
            photo64 = codec.encodeBase64String(bytes);

            employee.setEmpPhoto(photo64);
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private static boolean IsUserAlreadyConnectFromOtherComputer(String employee_status) {

        if(employee_status.equals("IN"))
            return true;
        return false;
    }

    private static String CheckIfInputPasswordIsOk(String inputPasswordFromUser,ResultSet responseFromDB,Employee employee)  {

        String response = "";

        try {
            employee.setEmpPass(responseFromDB.getString("emp_securePass"));
            employee.setEmpPassSalt(responseFromDB.getString("emp_passSalt"));

            boolean passwordMatch = PasswordUtils.verifyUserPassword(inputPasswordFromUser, employee.getEmpPass(), employee.getEmpPassSalt());

            if (passwordMatch) {
                InsertOtherParametersFromDBToEmployee(employee, responseFromDB);
                ChangeStatusOfEmployeeToIN(employee.getEmpId());
            } else
                response = "Your Employee ID ,Password or Branch name is incorrect !!!";
        }
        catch (Exception ex){
            logger.error(ex);
        }
        return response;
    }

    private static void ChangeStatusOfEmployeeToIN(String empId) {

        try {
            String query = "update store.employee set emp_status = 'IN' where emp_id = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, empId);
            preparedStmt.execute();

        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private static void InsertOtherParametersFromDBToEmployee(Employee employee, ResultSet responseFromDB)  {

        try {
            employee.setEmpType(responseFromDB.getString("emp_type"));
            employee.setEmpName(responseFromDB.getString("emp_name"));
            employee.setEmpSn(responseFromDB.getString("emp_sn"));
            employee.setEmpBank(responseFromDB.getString("emp_bank"));
            employee.setEmpTel(responseFromDB.getString("emp_tel"));
        }
        catch (Exception ex){
            logger.error(ex);
        }
    }

    private static Employee GetLogInResponseFromClient(JSONObject jsonObject) {

        Employee employee = new Employee();

        employee.setEmpId((String) jsonObject.get("employee id"));
        employee.setEmpPass((String)jsonObject.get("employee pass"));
        employee.setEmpBranch((String) jsonObject.get("employee branch"));

        return employee;

    }

    public static JSONArray ConvertResultSetToJsonArray(ResultSet resultSet)  {

        // Every item open num of rows in the JSONArray according to num of columns

        JSONArray jsonArray = new JSONArray();

        try {
            while (resultSet.next()) {

                int total_rows = resultSet.getMetaData().getColumnCount();

                JSONObject obj = new JSONObject();

                for (int i = 0; i < total_rows; i++) {

                    obj.put(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));
                    jsonArray.add(obj);
                }
            }
        }
        catch (Exception ex){
            logger.error(ex);
        }

        return jsonArray;
    }

    private String GetIdToLogOutFromClient(JSONObject jsonObject) {

        String emp_id = (String) jsonObject.get("employee id");

        return emp_id;

    }

    private static JSONArray ResultSetJson(ResultSet resultSet, JSONArray jsonArray) {

        try {

            while (resultSet.next()) {

                int total_rows = resultSet.getMetaData().getColumnCount();

                for (int i = 0; i < total_rows; i++) {

                    JSONObject obj = new JSONObject();
                    obj.put(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));
                    jsonArray.add(obj);
                }
            }
        }
        catch (Exception ex){
            //logger.error(ex);
        }

        return jsonArray;
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
        //System.setProperty(HANDSHAKE_DEBUG, DEFINITION_HANDSHAKE_DEBUG);

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
















