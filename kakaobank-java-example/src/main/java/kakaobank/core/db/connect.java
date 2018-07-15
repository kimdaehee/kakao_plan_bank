package kakaobank.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import kakaobank.core.log.TraceLog;

public class connect {
	public static Connection connection(String dbFileName) throws ClassNotFoundException, SQLException
    {
            
            try{
            	Class.forName("org.sqlite.JDBC");
                String url = "jdbc:sqlite:/"+dbFileName;
                Connection conn = DriverManager.getConnection(url);
                return conn;
            }catch(Exception e){
            	TraceLog.SetDebug("connect", "sqlite connection failed!!");
            	return null;
            }
    }
}
