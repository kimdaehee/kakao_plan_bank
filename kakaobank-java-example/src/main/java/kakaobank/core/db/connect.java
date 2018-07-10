package kakaobank.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connect {
	public static Connection connection(String dbFileName) throws ClassNotFoundException, SQLException
    {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:/"+dbFileName;
            Connection conn = DriverManager.getConnection(url);
            return conn;
    }
}
