package pingTableMail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.ObservableList;


public class Conn {
	
	private static Connection conn;
	private static Statement statmt;
	private static ResultSet resSet;
	
	public  void Connect() throws ClassNotFoundException, SQLException 
	   {
		   conn = null;
		   Class.forName("org.sqlite.JDBC");
		   String url = "jdbc:sqlite:host.db"; ////it2/Public/host.db\
		   conn = DriverManager.getConnection(url); 
	   }
	
	public  void CreateDB() throws SQLException
	   {
		statmt = conn.createStatement();
		statmt.execute("CREATE TABLE if not exists 'ipList' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'ip' VARCHAR(15) NOT NULL UNIQUE ON CONFLICT REPLACE DEFAULT (0), 'name' text);");
	   }
	
	public  void WriteDB(ObservableList<IpHost> ipList) throws SQLException
	{
		   for	(IpHost host : ipList) {
//			   statmt.execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Petya', 125453); ");
			   String insertSqlText = "INSERT INTO 'ipList' ('name', 'ip') VALUES (" + "'" + host.getHostName()+"', '"+ host.getIp() + "');";
			   statmt.execute(insertSqlText);
		   }
	}
	
	public void ReadDB(ObservableList<IpHost> ipList) throws SQLException
	   {
		statmt = conn.createStatement();
		resSet = statmt.executeQuery("SELECT * FROM ipList");
		
		while(resSet.next())
		{
			String  name = resSet.getString("name");
			String  ip = resSet.getString("ip");
			ipList.add(new IpHost(ip, name));
		}	
		//Collections.sort(ipList);
	    }

		public  void CloseDB() throws SQLException
		   {
			conn.close();
			statmt.close();
			resSet.close();
		   }
}
