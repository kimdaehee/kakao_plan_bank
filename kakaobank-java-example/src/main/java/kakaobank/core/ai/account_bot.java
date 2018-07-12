package kakaobank.core.ai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import kakaobank.core.s1.main.account_info;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class account_bot {
	
	public static int rndRange(int min, int max) {

		return (int) (Math.random() * (max - min + 1)) + min;

	}
	
	private static FileOutputStream account_kakao; //계정 계좌 정보
	
	//해당모듈은 30초당 계정 1개씩 생성을한다.
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InterruptedException{
		
		  //랜덤으로 돌리기위한 계좌 배열 선언
		  ArrayList<String> account_list = new ArrayList<String>();
		  
		  Properties configs = new Properties();
		  configs.put("bootstrap.servers", "localhost:9092");
		  configs.put("acks", "all");
		  configs.put("block.on.buffer.full", "true");
		  configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		  configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		  int custom_num = 0;
		  
		  @SuppressWarnings("rawtypes")
		  HashMap account_db = new HashMap();
		  
		  JSONObject complete_account = new JSONObject();
		  JSONArray make_account = new JSONArray();
		  JSONObject account_js_object = new JSONObject();
		  
		  
		  while(true){
			  	String name = "kakao" + custom_num;
				
				String topic = "kakaobank";
				
				KafkaProducer<String, String> producer = new KafkaProducer<>(configs);
	
				//계정정보를 생성한다.
				
				account_js_object = new JSONObject();
				make_account = new JSONArray();
				account_js_object = new JSONObject();
				
				String joinDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				String account_number = String.format("333302%d", custom_num);
				
				account_js_object.put("customer_number", custom_num);
				account_js_object.put("name", name);
				account_js_object.put("join_dt", joinDate);
				account_js_object.put("account_number", account_number);
				account_js_object.put("create_dt", createDate);
				account_js_object.put("balance", 0); //최초금액은 0원을 지급한다.
			
				
				//계좌를 리스트에 담는다.
	        	account_list.add(account_number);
				
				account_info ainfo = new account_info();
				ainfo.customer_number = custom_num;
				ainfo.name = name;
				ainfo.join_dt = joinDate;
				ainfo.account_number = account_number;
				ainfo.create_dt = createDate;
				ainfo.balance = 0;
				
				make_account.add(account_js_object);
				
				complete_account = new JSONObject();
				complete_account.put("message", make_account);//배열을k 넣음
				complete_account.put("category", "account");
				
				String sendMessage = complete_account.toJSONString();
				
				producer.send(new ProducerRecord<>(topic, sendMessage),
						(metadata, exception) -> {
							if (metadata != null) {
								System.out.println("partition(" + metadata.partition() + "), offset(" + metadata.offset() + ")");
							} else {
								exception.printStackTrace();
							}
						});
				producer.flush();
				producer.close();
				
				custom_num++;
				
				
				
				if(!account_db.containsKey(account_number)){ //계좌에 대한 정보를 버퍼에 가지고 있는다.
					account_db.put(account_number, ainfo);
					
					/**해당계좌가 만들어졌다고 파일로 저장**/
					
					String acc_db_path = "/home/kakao_project/account/"; //저장경로
					String account_fp = String.format("%s%s", acc_db_path, account_number); //파일명(계좌번호)
					
					File accFp = new File(acc_db_path);
					
					if(!accFp.exists()){
						//폴더가 존재하지 않으면 만든다.
						accFp.mkdirs();
					}
					
					account_kakao = new FileOutputStream(account_fp);
					account_kakao.write(sendMessage.getBytes("utf-8"));
					account_kakao.close();
				}
				
				
				
				
				
				//계정을 생성하고 추가계정이 존재하면 랜덤으로 입금 출금 이체 를 하는 자동 봇을 만들어서 로그를 생성한다.
				Iterator<String> keys = account_db.keySet().iterator();
				
				int customer_number = 0;
		        while( keys.hasNext() ){ //계좌가 
		        	String acc_number = keys.next();
		        	
		        	//고객마다 입금을 30개씩 아래의 랜덤값을 통해 로그 제작
		        	for(int a=0; a<30; a++){
						int[] cash = {1000, 10000, 100000, 1000000};//4개중 랜덤
						
						int amount = cash[rndRange(0,3)];
						
						account_js_object = new JSONObject();
						make_account = new JSONArray();
						account_js_object = new JSONObject();
						
						String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						
						account_js_object.put("customer_number", customer_number);
						account_js_object.put("account_number", acc_number);
						account_js_object.put("amount", amount);
						account_js_object.put("datetime", datetime);
						make_account.add(account_js_object);
						
						complete_account = new JSONObject();
						complete_account.put("message", make_account);//배열을k 넣음
						complete_account.put("category", "deposits");
						
						
						sendMessage =  complete_account.toJSONString();
						
						producer = new KafkaProducer<>(configs);

						  producer.send(new ProducerRecord<>(topic, sendMessage),
						    (metadata, exception) -> {
						      if (metadata != null) {
						        System.out.println( "partition(" + metadata.partition() + "), offset(" + metadata.offset() + ")");
						      } else {
						        exception.printStackTrace();
						      }
						    });
						  producer.flush();
						  producer.close();
					}
		        	
		        	//고객마다 출금을 30개씩 아래의 랜덤값을 통해 로그 제작
		        	for(int a=0; a<30; a++){
						int[] cash = {1000, 10000, 100000, 1000000};//4개중 랜덤
						
						int amount = cash[rndRange(0,3)];
						
						account_js_object = new JSONObject();
						make_account = new JSONArray();
						account_js_object = new JSONObject();
						
						String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						
						account_js_object.put("customer_number", customer_number);
						account_js_object.put("account_number", acc_number);
						account_js_object.put("amount", amount *(-1));
						account_js_object.put("datetime", datetime);
						make_account.add(account_js_object);
						
						complete_account = new JSONObject();
						complete_account.put("message", make_account);//배열을k 넣음
						complete_account.put("category", "withdrawals");
						
						
						sendMessage =  complete_account.toJSONString();
						
						producer = new KafkaProducer<>(configs);

						  producer.send(new ProducerRecord<>(topic, sendMessage),
						    (metadata, exception) -> {
						      if (metadata != null) {
						        System.out.println("partition(" + metadata.partition() + "), offset(" + metadata.offset() + ")");
						      } else {
						        exception.printStackTrace();
						      }
						    });
						  producer.flush();
						  producer.close();
					}
		        	
		        	
		        	//계좌가 1개 이상 있어야하고, 계좌번호와 이체대상번호가 같지않아야 함
		        	if(account_list.size() > 1){
		        		
		        		String target_account_number = account_list.get(rndRange(0,account_list.size()));
		        		
		        		if(!account_number.equals(target_account_number)){
		        			//40건의 계좌이체를 진행한다.
					        for(int a=0; a<40; a++){
					        	//account_list
					        	
					        	int[] cash = {1000, 10000, 100000, 200000};//4개중 랜덤
								
								int amount = cash[rndRange(0,3)];
								
								account_js_object = new JSONObject();
								make_account = new JSONArray();
								account_js_object = new JSONObject();
								
								
								account_info getInfo = (account_info) account_db.get(target_account_number);
								
								String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
								
								account_js_object.put("user_code", customer_number);
								account_js_object.put("send_account_number", acc_number);
								account_js_object.put("recive_account_number", target_account_number);
								account_js_object.put("recive_name", getInfo.name);
								account_js_object.put("amount", amount);
								account_js_object.put("datetime", datetime);
								make_account.add(account_js_object);
								
								complete_account = new JSONObject();
								complete_account.put("message", make_account);//배열을k 넣음
								complete_account.put("category", "transfers");
								
								
								sendMessage =  complete_account.toJSONString();
								
								producer = new KafkaProducer<>(configs);

								  producer.send(new ProducerRecord<>(topic, sendMessage),
								    (metadata, exception) -> {
								      if (metadata != null) {
								        System.out.println("partition(" + metadata.partition() + "), offset(" + metadata.offset() + ")");
								      } else {
								        exception.printStackTrace();
								      }
								    });
								  producer.flush();
								  producer.close();
								
					        }
		        		}
		        	}
		        	customer_number++;
		        }
		        
				Thread.sleep(30000); //30초 주기로 계정을 생성
		  }
	}
}
