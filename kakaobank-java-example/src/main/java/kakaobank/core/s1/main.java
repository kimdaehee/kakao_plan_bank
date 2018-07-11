package kakaobank.core.s1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class main {
	
	public static class  account_info implements Cloneable{
		public int customer_number;
		public String name;
		public String join_dt;
		public String account_number;
		public String create_dt;
		public int balance;
	}
	
	public static class  deposits_info implements Cloneable{
		public int amount;
		public String datetime;
	}
	
	
	private static FileInputStream namestream;
	private static FileOutputStream account_kakao; //계정 계좌 정보
	
	
	public static int rndRange(int min, int max) {

		return (int) (Math.random() * (max - min + 1)) + min;

	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException{

		String account_list = "D:\\git_workspace\\git\\kakao_plan_bank\\kakaobank-java-example\\DataBase\\account_list.kakao";
		
		//결과 FileDB(json)
		String acc_DB = "D:\\git_workspace\\git\\kakao_plan_bank\\kakaobank-java-example\\DataBase\\account.kakao";
		

		 //최초파일사이즈였을때만 해당루틴 진입
		//2.각각의 고객에 대해 100건 내외의 금융거래정보를 생성하고 Kafka를 통해 금융거래정보 로그를 전송하는 Producer 프로그램을 Java로 작성
		namestream = new FileInputStream(account_list); //임의의 고객명
		
		
		DataInputStream in = new DataInputStream(namestream);
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(in), "utf-8"));
		
		String strLine;
		
		int custom_num = 0;
		
		JSONObject complete_account = new JSONObject();
		JSONArray make_account = new JSONArray();
		
		HashMap account_db = new HashMap();
		
		while ((strLine = br.readLine()) != null) {
			//1.가입을 진행한다. (a. 모든 고객의 금융거래정보는 가입 로그부터 시작함)
			
			JSONObject account_js_object = new JSONObject();
			
			String joinDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			
			
			account_js_object.put("customer_number", custom_num);
			account_js_object.put("name", strLine);
			account_js_object.put("join_dt", joinDate);
			account_js_object.put("account_number", String.format("333302%d", custom_num));
			account_js_object.put("create_dt", createDate);
			//account_js_object.put("balance", 100000); //최초금액은 10만원을 지급한다.
			
			account_info ainfo = new account_info();
			ainfo.customer_number = custom_num;
			ainfo.name = strLine;
			ainfo.join_dt = joinDate;
			ainfo.account_number = String.format("333302%d", custom_num);
			ainfo.create_dt = createDate;
			//ainfo.balance = 100000;
			
			if(!account_db.containsKey(ainfo.account_number)){
				account_db.put(ainfo.account_number, ainfo);
			}
			
			make_account.add(account_js_object);
			custom_num++;
		}
		in.close();
		br.close();
		
		complete_account = new JSONObject();
		complete_account.put("account_list", make_account);//배열을k 넣음
		complete_account.put("account_count", custom_num);
        
		String LastRule = complete_account.toJSONString();
        
		account_kakao = new FileOutputStream(acc_DB); //스트림으로 파일 생성
		account_kakao.write(LastRule.getBytes("utf-8"));
		account_kakao.close();
		
		
		//(*)각각의고객에 100내외의 금융거래정보를 생성한다.(입금30번,출근30번, 이체30번)
		
		
		//입금
		Iterator<String> keys = account_db.keySet().iterator();
        while( keys.hasNext() ){
        	String account_number = keys.next();
        	
        	account_info ainfo = (account_info) account_db.get(account_number);
        	
        	//고객마다 입금을 30개씩 아래의 랜덤값을 통해 로그 제작
        	for(int a=0; a<30; a++){
				int[] cash = {1000, 10000, 100000, 1000000};//4개중 랜덤
				
				int amount = cash[rndRange(0,3)];
				
				ainfo.balance += amount;
				
				JSONObject account_js_object = new JSONObject();
				
				//System.out.println(amount);
			}
        	
        	System.out.println(ainfo.balance);
        }
		
		
		
		//3.랜덤을 이용하여 입금 출금, 특정인(랜덤)에게 이체(*이체는 사전계좌가 무조건 존재하여야만 함)하여 로그기록을 남긴다. (sqlite) - c. 입금,출금,이체 대상이 되는 계좌는 사전에 계좌개설이 반드시 이루어져야 함
		//4.해당로그는 kafka producer을 이용하여 곧바로 전송한다.
		
	}

}
