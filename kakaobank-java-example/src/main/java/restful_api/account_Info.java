package restful_api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class account_Info {
	
	public static class  account_info implements Cloneable{
		public int customer_number;
		public String name;
		public String join_dt;
		public String account_number;
		public String create_dt;
		public int balance;
	}
	
	public static void main(String[] args)
	{
		Spark.setPort(5441);
		
		Spark.get(new Route("/") {
			public Object handle(Request arg0, Response arg1) {
				// TODO Auto-generated method stub
				String ip = arg0.ip();
				if(!ip.equals("127.0.0.1"))
					return "Wrong Access";
				else
					return "Welcome";					
			}
		});
		
		Spark.get(new Route("/api/customer") {
			@SuppressWarnings({ "unchecked" })
			public Object handle(Request arg0, Response arg1) {
				
				// TODO Auto-generated method stub
				String ip = arg0.ip();
				
				
				if(!ip.equals("127.0.0.1"))
					return "Wrong Access";
				
				String customer_number = arg0.queryParams("customer_number");
				
				String dbName = "/home/kakaobank_project/DataBase/kakaoDB.db";
				//String dbName = "C:\\kakaoDB.db";
				
				JSONObject account_info_object = new JSONObject();
				
				try{
					Connection con = null;
	    	        PreparedStatement stmt = null;
	    	        ResultSet rs = null;
	    	        
	    	        
	    			if (con == null)
	    				con = kakaobank.core.db.connect.connection(dbName);
					
	    			
	    			//입금에 대한 계산
					stmt = con.prepareStatement("select customer_number, name, join_dt from account where customer_number = ?");
					stmt.setString(1, customer_number);
					rs = stmt.executeQuery();
					
					account_info ainfo = new account_info();
					
					while(rs.next()){
						ainfo.customer_number = rs.getInt("customer_number");
						ainfo.name = rs.getString("name");
						ainfo.join_dt = rs.getString("join_dt");
					}
					
					account_info_object.put("customer_number", ainfo.customer_number);
					account_info_object.put("name", ainfo.name);
					account_info_object.put("join_dt", ainfo.join_dt);
					
					stmt = con.prepareStatement("select max(amount) from deposits where customer_number = ?");
					stmt.setString(1, customer_number);
					rs = stmt.executeQuery();
					
					int largest_deposit_acmount = 0;
					
					while(rs.next()){
						largest_deposit_acmount = rs.getInt("max(amount)");
					}
					account_info_object.put("largest_deposit_acmount", largest_deposit_acmount);
					
					
					stmt = con.prepareStatement("select max(amount) from withdrawals where user_code = ?");
					stmt.setString(1, customer_number);
					rs = stmt.executeQuery();
					
					int largest_withdrawal_acmount = 0;
					
					while(rs.next()){
						largest_withdrawal_acmount = rs.getInt("max(amount)");
					}
					account_info_object.put("largest_withdrawal_acmount", largest_withdrawal_acmount);
					
					stmt = con.prepareStatement("select max(amount) from transfers where user_code = ?");
					stmt.setString(1, customer_number);
					rs = stmt.executeQuery();
					
					int largest_transfer_acmount = 0;
					
					while(rs.next()){
						largest_transfer_acmount = rs.getInt("max(amount)");
					}
					account_info_object.put("largest_transfer_acmount", largest_transfer_acmount);
					
				}catch(Exception e){
					System.out.println("Error");
				}
				
				return account_info_object.toJSONString();
	
			}
		});
	}
}
