package kakaobank.core.s1;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class main {
	public static Connection connection(String dbFileName) throws ClassNotFoundException, SQLException
    {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:/"+dbFileName;
            Connection conn = DriverManager.getConnection(url);
            return conn;
    }
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws ParseException, NumberFormatException, SQLException, ClassNotFoundException, InterruptedException {
		
		String dbName = "/home/kakaobank_project/DataBase/kakaoDB.db";
		
		Properties configs = new Properties();
        // 환경 변수 설정
        configs.put("bootstrap.servers", "localhost:9092");     // kafka server host 및 port
        configs.put("session.timeout.ms", "10000");             // session 설정
        configs.put("group.id", "kakaobank");                // topic 설정
        configs.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");    // key deserializer
        configs.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  // value deserializer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);    // consumer 생성
        consumer.subscribe(Arrays.asList("kakaobank"));      // topic 설정
        while (true) {  // 계속 loop를 돌면서 producer의 message를 띄운다.
            ConsumerRecords<String, String> records = consumer.poll(500);
            for (ConsumerRecord<String, String> record : records) {
                String s = record.topic();
                if ("kakaobank".equals(s)) {
                    System.out.println(record.value());
                    System.out.println("value : " + record.value());
		        	//{"message":[{"account_number":"3333020","amount":-1000000,"datetime":"2018-07-14 12:57:52","customer_number":0}],"category":"withdrawals"}
		        	 
		        	JSONObject theKaKao = null;
        			JSONArray kakaoArray = null;
        			JSONParser kakaoJsParser = new JSONParser(); 
        			
		        	String jsonData = record.value();
		        	
		        	theKaKao = (JSONObject) kakaoJsParser.parse(jsonData);
		        	kakaoArray = (JSONArray) theKaKao.get("message");
		        	String kakaoCategory = theKaKao.get("category").toString();
		        	
		        	
		        	//System.out.println("catecory : " + kakaoCategory);
		        	
					/*switch (kakaoCategory) {
					case "account":
						System.out.println("account value : " + record.value());
						break;
					case "deposits":
						System.out.println("deposits value : " + record.value());
						break;
					case "withdrawals":
						System.out.println("withdrawals value : " + record.value());
						break;
					case "transfers":
						System.out.println("transfers value : " + record.value());
						break;
					}*/
					
		        	
		        	if(kakaoCategory.equals("account")){
		        		//회원가입 및 계좌 계설
		        		
		        		for (int i = 0; i < kakaoArray.size(); i++)
			        	{
		        			JSONObject account_date = (JSONObject) kakaoArray.get(i);
		        			
		        			Connection con = null;
		        	        PreparedStatement stmt = null;
		        	        
		        			if (con == null)
		        				con = connection(dbName);
		        			
		        			try {
		        				stmt = con.prepareStatement(String.format("INSERT INTO %s(customer_number, name, join_dt, account_number, create_dt, balance) VALUES(?, ?, ?, ?, ?, ?)", "account"));

		        				stmt.setInt(1, Integer.parseInt(account_date.get("customer_number").toString()));
		        				stmt.setString(2, account_date.get("name").toString());
		        				stmt.setString(3, account_date.get("join_dt").toString());
		        				stmt.setString(4, account_date.get("account_number").toString());
		        				stmt.setString(5, account_date.get("create_dt").toString());
		        				stmt.setInt(6, Integer.parseInt(account_date.get("balance").toString()));
		        				stmt.executeUpdate();
		        			} finally {
		        				try {
		        					if (stmt != null) {
		        						stmt.close();
		        					}
		        				} catch (Exception e) {
		        					System.out.println("stmt null error");
		        				}
		        				try {
		        					if (con != null) {
		        						con.close();
		        					}
		        				} catch (Exception e) {
		        					System.out.println("con null error");
		        				}
		        			}
			        	}
		        		
		        	}else if(kakaoCategory.equals("deposits")){
		        		//입금
		        		for (int i = 0; i < kakaoArray.size(); i++)
			        	{
		        			JSONObject account_date = (JSONObject) kakaoArray.get(i);
		        			
		        			Connection con = null;
		        	        PreparedStatement stmt = null;
		        	        ResultSet rs = null;
		        	        
		        	        int user_cash = 0;
		        	        
		        			if (con == null)
		        				con = connection(dbName);
		        			
		        			try {
		        				stmt = con.prepareStatement(String.format("INSERT INTO %s(customer_number, account_number, amount, datetime) VALUES(?, ?, ?, ?)", "deposits"));
		        				stmt.setInt(1, Integer.parseInt(account_date.get("customer_number").toString()));
		        				stmt.setString(2, account_date.get("account_number").toString());
		        				stmt.setInt(3, Integer.parseInt(account_date.get("amount").toString()));
		        				stmt.setString(4, account_date.get("datetime").toString());
		        				stmt.executeUpdate();
		        				
		        				//입금에 대한 계산
		        				stmt = con.prepareStatement("select balance from account where account_number = ?");
		        				stmt.setString(1, account_date.get("account_number").toString());
		        				rs = stmt.executeQuery();
		        				
		        				while(rs.next()){
		        					user_cash = rs.getInt("balance");
		        				}
		        				
		        				stmt = con.prepareStatement("update account set balance=? where account_number=?");
		        				stmt.setInt(1, user_cash + Integer.parseInt(account_date.get("amount").toString()));
		        				stmt.setString(2, account_date.get("account_number").toString());
		        				stmt.executeUpdate();
		        				
		        			} finally {
		        				try {
		        					if (stmt != null) {
		        						stmt.close();
		        					}
		        				} catch (Exception e) {
		        					System.out.println("stmt null error");
		        				}
		        				try {
		        					if (con != null) {
		        						con.close();
		        					}
		        				} catch (Exception e) {
		        					System.out.println("con null error");
		        				}
		        			}
			        	}
		        	}else if(kakaoCategory.equals("withdrawals")){
		        		//출금
		        		for (int i = 0; i < kakaoArray.size(); i++)
			        	{
		        			JSONObject account_date = (JSONObject) kakaoArray.get(i);
		        			
		        			Connection con = null;
		        	        PreparedStatement stmt = null;
		        	        ResultSet rs = null;
		        	        
		        	        int user_cash = 0;
		        	        
		        			if (con == null)
		        				con = connection(dbName);
		        			
		        			try {
		        				stmt = con.prepareStatement(String.format("INSERT INTO %s(user_code, account_number, amount, datetime) VALUES(?, ?, ?, ?)", "withdrawals"));
		        				stmt.setInt(1, Integer.parseInt(account_date.get("customer_number").toString()));
		        				stmt.setString(2, account_date.get("account_number").toString());
		        				stmt.setInt(3, Integer.parseInt(account_date.get("amount").toString()));
		        				stmt.setString(4, account_date.get("datetime").toString());
		        				stmt.executeUpdate();
		        				
		        				//출금에 대한 계산
		        				stmt = con.prepareStatement("select balance from account where account_number = ?");
		        				stmt.setString(1, account_date.get("account_number").toString());
		        				rs = stmt.executeQuery();
		        				
		        				while(rs.next()){
		        					user_cash = rs.getInt("balance");
		        				}
		        				
		        				stmt = con.prepareStatement("update account set balance=? where account_number=?");
		        				stmt.setInt(1, user_cash - Integer.parseInt(account_date.get("amount").toString()));
		        				stmt.setString(2, account_date.get("account_number").toString());
		        				stmt.executeUpdate();
		        				
		        			} finally {
		        				try {
		        					if (stmt != null) {
		        						stmt.close();
		        					}
		        				} catch (Exception e) {
		        					System.out.println("stmt null error");
		        				}
		        				try {
		        					if (con != null) {
		        						con.close();
		        					}
		        				} catch (Exception e) {
		        					System.out.println("con null error");
		        				}
		        			}
			        	}
		        	}else if(kakaoCategory.equals("transfers")){
		        		//계좌이체
		        		for (int i = 0; i < kakaoArray.size(); i++)
			        	{
		        			JSONObject account_date = (JSONObject) kakaoArray.get(i);
		        			
		        			Connection con = null;
		        	        PreparedStatement stmt = null;
		        	        ResultSet rs = null;
		        	        
		        	        int send_user_cash = 0;
		        	        int recive_user_cash = 0;
		        	        
		        			if (con == null)
		        				con = connection(dbName);
		        			
		        			try {
		        				stmt = con.prepareStatement(String.format("INSERT INTO %s(user_code, send_account_number, recive_bank_code, recive_account_number, recive_name, amount, datetime) VALUES(?, ?, ?, ?, ?, ?, ?)", "transfers"));
		        				stmt.setInt(1, Integer.parseInt(account_date.get("user_code").toString()));
		        				stmt.setString(2, account_date.get("send_account_number").toString());
		        				stmt.setInt(3, Integer.parseInt(account_date.get("recive_bank_code").toString()));
		        				stmt.setString(4, account_date.get("recive_account_number").toString());
		        				stmt.setString(5, account_date.get("recive_name").toString());
		        				stmt.setInt(6, Integer.parseInt(account_date.get("amount").toString()));
		        				stmt.setString(7, account_date.get("datetime").toString());
		        				stmt.executeUpdate();
		        				
		        				//자금이체에 대한 계산
		        				stmt = con.prepareStatement("select balance from account where account_number = ?");
		        				stmt.setString(1, account_date.get("send_account_number").toString());
		        				rs = stmt.executeQuery();
		        				
		        				while(rs.next()){
		        					send_user_cash = rs.getInt("balance");
		        				}
		        				
		        				stmt = con.prepareStatement("select balance from account where account_number = ?");
		        				stmt.setString(1, account_date.get("recive_account_number").toString());
		        				rs = stmt.executeQuery();
		        				
		        				while(rs.next()){
		        					recive_user_cash = rs.getInt("balance");
		        				}
		        				
		        				//보내는사람쪽은 마이너스
		        				stmt = con.prepareStatement("update account set balance=? where account_number=?");
		        				stmt.setInt(1, send_user_cash + Integer.parseInt(account_date.get("amount").toString()));
		        				stmt.setString(2, account_date.get("send_account_number").toString());
		        				stmt.executeUpdate();
		        				
		        				//받는사람쪽은 마이너스
		        				stmt = con.prepareStatement("update account set balance=? where account_number=?");
		        				stmt.setInt(1, recive_user_cash + Integer.parseInt(account_date.get("amount").toString()));
		        				stmt.setString(2, account_date.get("recive_account_number").toString());
		        				stmt.executeUpdate();
		        				
		        				
		        			} finally {
		        				try {
		        					if (stmt != null) {
		        						stmt.close();
		        					}
		        				} catch (Exception e) {
		        					System.out.println("stmt null error");
		        				}
		        				try {
		        					if (con != null) {
		        						con.close();
		        					}
		        				} catch (Exception e) {
		        					System.out.println("con null error");
		        				}
		        			}
			        	}
		        		
		        		
		        	}else{
		        		//nothing//
		        	}
                } else {
                    throw new IllegalStateException("get message on topic " + record.topic());
                }
            }
        } 
	}
}
