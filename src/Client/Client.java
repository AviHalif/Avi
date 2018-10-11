package Client;

import Gui.RegisterEmployee;
import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.Properties;

public class Client {

    //SSLSSocketFactory establishes the ssl context and and creates SSLSocket
    private SSLSocketFactory sslsocketfactory;

    //Create SSLSocket using SSLServerFactory already established ssl context and connect to server
    private SSLSocket sslSocket;

    //Create OutputStream to send message to server
    private DataOutputStream outputStream;

    //Create InputStream to read messages send by the server
    private DataInputStream inputStream;

    private Properties properties;
    private String propFileName;
    private InputStream inStream;

    //The Port number through which the server will accept this clients connection
    public int server_port;
    //The Server Address
    public String server_name;

    public static final String TRUSTSTORE = "javax.net.ssl.trustStore";
    public static final String TRUSTSTORE_FILE_DIRECTORY =  "C:/Users/AviHalif/IdeaProjects/Avi/src/myTrustStore.jts";
    public static final String TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";
    public static final String TRUSTSTORE_PASSWORD_FILE = "java13579";
    public static final String HANDSHAKE_DEBUG = "javax.net.debug";
    public static final String DEFINITION_HANDSHAKE_DEBUG = "all";

    public Client(){
        try {

            DefineConfig();

              /*Adding the JSSE (Java Secure Socket Extension) provider which provides SSL and TLS protocols
                        and includes functionality for data encryption, server authentication, message integrity,
                        and optional client authentication.*/
            Security.addProvider(new Provider());
            //specifying the trustStore file which contains the certificate & public of the server
            System.setProperty(TRUSTSTORE, TRUSTSTORE_FILE_DIRECTORY);
            //specifying the password of the trustStore file
            System.setProperty(TRUSTSTORE_PASSWORD, TRUSTSTORE_PASSWORD_FILE);
            //This optional and it is just to show the dump of the details of the handshake process
            System.setProperty(HANDSHAKE_DEBUG, DEFINITION_HANDSHAKE_DEBUG);
            sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            server_name = properties.getProperty("ServerName");
            server_port = Integer.parseInt(properties.getProperty("ServerPort"));

            sslSocket = (SSLSocket) sslsocketfactory.createSocket(server_name, server_port);

            outputStream = new DataOutputStream(sslSocket.getOutputStream());
            inputStream = new DataInputStream(sslSocket.getInputStream());
        }
        catch (Exception ex){}
    }

    private void DefineConfig() throws IOException {

        properties = new Properties();
        propFileName = "config.properties";
        inStream = RegisterEmployee.class.getClassLoader().getResourceAsStream(propFileName);
        properties.load(inStream);
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public SSLSocket getSslSocket() {
        return sslSocket;
    }
}
