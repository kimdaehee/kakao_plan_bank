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
	private static FileOutputStream account_kakao; //���� ���� ����
	
	
	public static int rndRange(int min, int max) {

		return (int) (Math.random() * (max - min + 1)) + min;

	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException{

		String account_list = "D:\\git_workspace\\git\\kakao_plan_bank\\kakaobank-java-example\\DataBase\\account_list.kakao";
		
		//��� FileDB(json)
		String acc_DB = "D:\\git_workspace\\git\\kakao_plan_bank\\kakaobank-java-example\\DataBase\\account.kakao";
		

		 //�������ϻ���������� �ش��ƾ ����
		//2.������ ���� ���� 100�� ������ �����ŷ������� �����ϰ� Kafka�� ���� �����ŷ����� �α׸� �����ϴ� Producer ���α׷��� Java�� �ۼ�
		namestream = new FileInputStream(account_list); //������ ����
		
		
		DataInputStream in = new DataInputStream(namestream);
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(in), "utf-8"));
		
		String strLine;
		
		int custom_num = 0;
		
		JSONObject complete_account = new JSONObject();
		JSONArray make_account = new JSONArray();
		
		HashMap account_db = new HashMap();
		
		while ((strLine = br.readLine()) != null) {
			//1.������ �����Ѵ�. (a. ��� ���� �����ŷ������� ���� �α׺��� ������)
			
			JSONObject account_js_object = new JSONObject();
			
			String joinDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			
			
			account_js_object.put("customer_number", custom_num);
			account_js_object.put("name", strLine);
			account_js_object.put("join_dt", joinDate);
			account_js_object.put("account_number", String.format("333302%d", custom_num));
			account_js_object.put("create_dt", createDate);
			//account_js_object.put("balance", 100000); //���ʱݾ��� 10������ �����Ѵ�.
			
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
		complete_account.put("account_list", make_account);//�迭��k ����
		complete_account.put("account_count", custom_num);
        
		String LastRule = complete_account.toJSONString();
        
		account_kakao = new FileOutputStream(acc_DB); //��Ʈ������ ���� ����
		account_kakao.write(LastRule.getBytes("utf-8"));
		account_kakao.close();
		
		
		//(*)�����ǰ��� 100������ �����ŷ������� �����Ѵ�.(�Ա�30��,���30��, ��ü30��)
		
		
		//�Ա�
		Iterator<String> keys = account_db.keySet().iterator();
        while( keys.hasNext() ){
        	String account_number = keys.next();
        	
        	account_info ainfo = (account_info) account_db.get(account_number);
        	
        	//������ �Ա��� 30���� �Ʒ��� �������� ���� �α� ����
        	for(int a=0; a<30; a++){
				int[] cash = {1000, 10000, 100000, 1000000};//4���� ����
				
				int amount = cash[rndRange(0,3)];
				
				ainfo.balance += amount;
				
				JSONObject account_js_object = new JSONObject();
				
				//System.out.println(amount);
			}
        	
        	System.out.println(ainfo.balance);
        }
		
		
		
		//3.������ �̿��Ͽ� �Ա� ���, Ư����(����)���� ��ü(*��ü�� �������°� ������ �����Ͽ��߸� ��)�Ͽ� �αױ���� �����. (sqlite) - c. �Ա�,���,��ü ����� �Ǵ� ���´� ������ ���°����� �ݵ�� �̷������ ��
		//4.�ش�α״� kafka producer�� �̿��Ͽ� ��ٷ� �����Ѵ�.
		
	}

}
