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

		 //�������ϻ���������� �ش��ƾ ����
		//2.������ ���� ���� 100�� ������ �����ŷ������� �����ϰ� Kafka�� ���� �����ŷ����� �α׸� �����ϴ� Producer ���α׷��� Java�� �ۼ�
		namestream = new FileInputStream(account); //������ ����
		
		
		DataInputStream in = new DataInputStream(namestream);
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(in), "utf-8"));
		
		String strLine;
		
		int custom_num = 0;
		while ((strLine = br.readLine()) != null) {
			//1.������ �����Ѵ�. (a. ��� ���� �����ŷ������� ���� �α׺��� ������)
			Connection con = null;
	        PreparedStatement stmt = null;
	        
			if (con == null)
				con = kakaobank.core.db.connect.connection(dbName); //d. �������� ������ ���� ������ ���� �����ŷ����� �α׸� ���Ϸ� �����ؾ� ��
			
			try {
				stmt = con.prepareStatement(String.format("INSERT INTO %s(customer_number, name, join_dt, account_number, create_dt, balance) VALUES(?, ?, ?, ?, ?, ?)", "account"));

				String acc_dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				
				stmt.setInt(1, custom_num++);
				stmt.setString(2, strLine);
				stmt.setString(3, acc_dt);
				//2.���¸� �����Ѵ�. (b. ��� ���� �ݵ�� 1�� �̻��� ���¸� ������)
				stmt.setString(4, String.format("333302%d", custom_num));//���¹�ȣ 3333-02-1 ~ ��������
				stmt.setString(5, acc_dt);
				int balance = rndRange(1000000, 10000000);
				stmt.setInt(6, balance); //���ʱݾ��� 10���� ����
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
	
		
		
		//(*)�����ǰ��� 100������ �����ŷ������� �����Ѵ�.
		//3.������ �̿��Ͽ� �Ա� ���, Ư����(����)���� ��ü(*��ü�� �������°� ������ �����Ͽ��߸� ��)�Ͽ� �αױ���� �����. (sqlite) - c. �Ա�,���,��ü ����� �Ǵ� ���´� ������ ���°����� �ݵ�� �̷������ ��
		//4.�ش�α״� kafka producer�� �̿��Ͽ� ��ٷ� �����Ѵ�.
		
	}

}
