/**
 * Created by sujata on 4/10/15.
 */
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SocketClient {
    private String ip;
    private int port;
    private Socket socket;
    private String separator;

    private DatabaseManager databaseManager = new DatabaseManager();

    public SocketClient() {
        readProperties();
    }

    private void readProperties() {
        String databasePropFile = System.getProperty("user.dir") + "/device.properties" ;

        try {
            System.out.println("Going to read " + databasePropFile);

            InputStream fileInputStream = new FileInputStream(databasePropFile);
            Properties properties = new Properties();
            properties.load(fileInputStream);

            ip = properties.getProperty("ip");
            port = Integer.parseInt(properties.getProperty("port"));
            separator = properties.getProperty("parameter-separator");

        } catch (FileNotFoundException e) {
            System.err.println(databasePropFile + " file not found");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number for the database server");
        }
    }

    public void openConnection() {
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            System.err.println("Failed to open socket connection");
            e.printStackTrace();
        }
    }

    public void readStream() {
        try {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                System.out.println("Waiting for message ... ");
                String line = inputStream.readLine();
                List<String> params = Arrays.asList(line.split(separator));
                int i=0;
                for(String param : params) {
                    System.out.println("param" + ++i + " = " + param);
                }
                databaseManager.insertData(params);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        socketClient.openConnection();
        socketClient.readStream();
    }
}
