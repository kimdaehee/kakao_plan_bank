package kakaobank.core.ai;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.JSONObject;

public class AI_Account {
	
	//해당모듈은 30초당 계정 1개씩 생성을한다.
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InterruptedException{
		Properties configs = new Properties();
		  configs.put("bootstrap.servers", "localhost:9092");
		  configs.put("acks", "all");
		  configs.put("block.on.buffer.full", "true");
		  configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		  configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		  int custom_num = 0;
		  while(true){
			  	String name = "kakao" + custom_num;
				
				String topic = String.format("333302%d", custom_num);
				
				KafkaProducer<String, String> producer = new KafkaProducer<>(configs);
	
				//계정정보를 생성한다.
				
				JSONObject account_js_object = new JSONObject();
				
				String joinDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				
				account_js_object.put("customer_number", custom_num);
				account_js_object.put("name", name);
				account_js_object.put("join_dt", joinDate);
				account_js_object.put("account_number", String.format("333302%d", custom_num));
				account_js_object.put("create_dt", createDate);
				account_js_object.put("balance", 0); //최초금액은 0원을 지급한다.
			
				String LastRule = account_js_object.toJSONString();
				
				producer.send(new ProducerRecord<>(topic, LastRule),
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
			
				Thread.sleep(30000); //30초 주기로 계정을 생성
		  }
	}
}
