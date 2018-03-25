package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by clementserrano on 24/03/2018.
 */
public class ConnexionMySQL {

    private static Connection connection;

    public static Connection getConnexion() {
        if(connection == null){
            String url = "jdbc:mysql://localhost:3306/ipc";
            String username = "root";
            String password = "";

            try {
                connection = DriverManager.getConnection(url, username, password);
            } catch (SQLException e){
                throw new IllegalStateException("Cannot connect the database!", e);
            }
        }
        return connection;
    }
}
