package restful_api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import restful_api.account_Info.account_info;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class account_num_Info {
	public static void main(String[] args)
	{

		Spark.setPort(5442);
		
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
				String account_number = arg0.queryParams("account_number");
				
				String dbName = "/home/kakaobank_project/DataBase/kakaoDB.db";
				//String dbName = "C:\\kakaoDB.db";
				
				JSONObject last_new_date = new JSONObject();
				
				try{
					Connection con = null;
	    	        PreparedStatement stmt = null;
	    	        ResultSet rs = null;
	    	        
	    	        
	    			if (con == null)
	    				con = kakaobank.core.db.connect.connection(dbName);
					
	    			
	    			//입금에 대한 계산
					stmt = con.prepareStatement("select customer_number, account_number, create_dt, balance from account where customer_number = ? and account_number=?");
					stmt.setString(1, customer_number);
					stmt.setString(2, account_number);
					rs = stmt.executeQuery();
					
					account_info ainfo = new account_info();
					
					while(rs.next()){
						ainfo.customer_number = rs.getInt("customer_number");
						ainfo.account_number = rs.getString("account_number");
						ainfo.create_dt = rs.getString("create_dt");
						ainfo.balance = rs.getInt("balance");
					}
					
					last_new_date.put("customer_number", ainfo.customer_number);
					last_new_date.put("account_number", ainfo.account_number);
					last_new_date.put("create_dt", ainfo.create_dt);
					last_new_date.put("balance", ainfo.balance);
					
					
					JSONArray deposits_Array = new JSONArray();
					JSONObject deposits_date = new JSONObject();
					stmt = con.prepareStatement("select * from deposits where customer_number = ?");
					stmt.setString(1, customer_number);
					rs = stmt.executeQuery();
					
					int deposits_amount = 0;
					String deposits_datetime = "";
					
					while(rs.next()){
						deposits_amount = rs.getInt("amount");
						deposits_datetime = rs.getString("datetime");
						
						deposits_date.put("amount", deposits_amount);
						deposits_date.put("datetime", deposits_datetime);
						
						deposits_Array.add(deposits_date);
					}
					last_new_date.put("deposits", deposits_Array);
					
					
					JSONArray withdrawals_Array = new JSONArray();
					JSONObject withdrawals_date = new JSONObject();
					stmt = con.prepareStatement("select * from withdrawals where user_code = ?");
					stmt.setString(1, customer_number);
					rs = stmt.executeQuery();
					
					int withdrawal_amount = 0;
					String withdrawal_datetime = "";
					
					while(rs.next()){
						withdrawal_amount = rs.getInt("amount");
						withdrawal_datetime = rs.getString("datetime");
						
						withdrawals_date.put("amount", withdrawal_amount);
						withdrawals_date.put("datetime", withdrawal_datetime);
						
						withdrawals_Array.add(withdrawals_date);
					}
					last_new_date.put("withdrawals", withdrawals_Array);
					
					
					JSONArray transfers_Array = new JSONArray();
					JSONObject transfers_date = new JSONObject();
					stmt = con.prepareStatement("select amount, datetime from transfers where user_code = ?");
					stmt.setString(1, customer_number);
					rs = stmt.executeQuery();
					
					int transfer_amount = 0;
					String transfer_datetime = "";
					
					while(rs.next()){
						transfer_amount = rs.getInt("amount");
						transfer_datetime = rs.getString("datetime");
						
						transfers_date.put("amount", withdrawal_amount);
						transfers_date.put("datetime", transfer_datetime);
						
						transfers_Array.add(transfers_date);
					}
					last_new_date.put("transfers", transfers_Array);
					
				}catch(Exception e){
					System.out.println("Error");
				}
				
				return last_new_date.toJSONString();
	
			}
		});
	
	}
}
