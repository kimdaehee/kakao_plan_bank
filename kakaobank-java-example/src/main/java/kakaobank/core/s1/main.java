package kakaobank.core.s1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class main {
	
	private static FileInputStream namestream;
	
	public static int rndRange(int min, int max) {

		return (int) (Math.random() * (max - min + 1)) + min;

	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException{

		String account = "/home/kakaobank_project/account_list.kakao";
		String dbName = "/home/kakaobank_project/DataBase/kakaoDB.db";

		 //최초파일사이즈였을때만 해당루틴 진입
		//2.각각의 고객에 대해 100건 내외의 금융거래정보를 생성하고 Kafka를 통해 금융거래정보 로그를 전송하는 Producer 프로그램을 Java로 작성
		namestream = new FileInputStream(account); //임의의 고객명
		
		
		DataInputStream in = new DataInputStream(namestream);
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(in), "utf-8"));
		
		String strLine;
		
		int custom_num = 0;
		while ((strLine = br.readLine()) != null) {
			//1.가입을 진행한다. (a. 모든 고객의 금융거래정보는 가입 로그부터 시작함)
			Connection con = null;
	        PreparedStatement stmt = null;
	        
			if (con == null)
				con = kakaobank.core.db.connect.connection(dbName); //d. 프로파일 검증을 위해 생성된 고객별 금융거래정보 로그를 파일로 저장해야 함
			
			try {
				stmt = con.prepareStatement(String.format("INSERT INTO %s(customer_number, name, join_dt, account_number, create_dt, balance) VALUES(?, ?, ?, ?, ?, ?)", "account"));

				String acc_dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				
				stmt.setInt(1, custom_num++);
				stmt.setString(2, strLine);
				stmt.setString(3, acc_dt);
				//2.계좌를 개설한다. (b. 모든 고객은 반드시 1개 이상의 계좌를 보유함)
				stmt.setString(4, String.format("333302%d", custom_num));//계좌번호 3333-02-1 ~ 순차적용
				stmt.setString(5, acc_dt);
				int balance = rndRange(1000000, 10000000);
				stmt.setInt(6, balance); //최초금액은 10만원 고정
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
		
		in.close();
		br.close();
	
		
		
		//(*)각각의고객에 100내외의 금융거래정보를 생성한다.
		//3.랜덤을 이용하여 입금 출금, 특정인(랜덤)에게 이체(*이체는 사전계좌가 무조건 존재하여야만 함)하여 로그기록을 남긴다. (sqlite) - c. 입금,출금,이체 대상이 되는 계좌는 사전에 계좌개설이 반드시 이루어져야 함
		//4.해당로그는 kafka producer을 이용하여 곧바로 전송한다.
		
	}

}
