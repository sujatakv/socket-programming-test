import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by sujata on 4/10/15.
 */
public class DatabaseManager {

    private String server;
    private int port;
    private String userName;
    private String password;
    private String database;
    private String tableName;
    private List<String> paramNames = new ArrayList<String>();

    private Connection connection;

    private String partialInsertQuery;

    public DatabaseManager() {
        readProperties();
        openConnection();
        createTable();
        setPartialInsertQuery();
    }

    public void readProperties() {
        String databasePropFile = System.getProperty("user.dir") + "/database.properties" ;

        try {
            System.out.println("Going to read " + databasePropFile);

            InputStream fileInputStream = new FileInputStream(databasePropFile);
            Properties properties = new Properties();
            properties.load(fileInputStream);

            server = properties.getProperty("server");
            port = Integer.parseInt(properties.getProperty("port"));
            userName = properties.getProperty("username");
            password = properties.getProperty("password");
            database = properties.getProperty("database");
            tableName = properties.getProperty("tablename");

            int i=0;
            while (true) {
                String paramName = properties.getProperty("param" + ++i);
                if(paramName!=null && !paramName.isEmpty()) {
                    paramNames.add(paramName);
                    System.out.println("param" + i + " = " + paramName);
                } else {
                    break;
                }
            }

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
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Registered!");
            String dbUrl = "jdbc:mysql://" + server + ":" + port + "/" + database;
            System.out.println("Db URL : " + dbUrl);
            System.out.println(userName + " : " + password);
            connection = DriverManager.getConnection(dbUrl, userName, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void createTable() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            StringBuilder query = new StringBuilder();

            query.append("CREATE TABLE IF NOT EXISTS " + tableName + " ( "
                    +" id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY"
            );
            for(String paramName : paramNames) {
                query.append(", " + paramName + " VARCHAR(30) NOT NULL ");
            }
            query.append(");");

            System.out.println(query.toString());
            statement.executeUpdate(query.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement!=null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setPartialInsertQuery() {
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO " + tableName + " (");
        boolean first = true;
        for(String param : paramNames) {
            if(!first) query.append(",");
            else first = false;
            query.append(param);
        }
        query.append(") VALUES ");
        partialInsertQuery = query.toString();
    }

    private String getPartialInsertQuery() {
        return partialInsertQuery;
    }

    public void insertData(List<String> paramValues) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            StringBuilder query = new StringBuilder();
            query.append( getPartialInsertQuery() + " (");
            boolean first = true;
            for(String paramValue : paramValues) {
                if(!first) query.append(",");
                else first = false;
                query.append("'" + paramValue + "'");
            }
            query.append(")");
            System.out.println(query);
            statement.executeUpdate(query.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement!=null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();
    }
}
