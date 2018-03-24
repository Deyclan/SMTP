package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by clementserrano on 24/03/2018.
 */
public class ConnexionMySQL {

    public static Connection getConnexion() {
        String url = "jdbc:mysql://localhost:3306/ipc";
        String username = "root";
        String password = "";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            return connection;
        } catch (SQLException e){
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }
}
