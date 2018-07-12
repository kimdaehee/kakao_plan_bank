package kakaobank.core.s1;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class consumer {
	public static void main(String[] args) {
		  Properties configs = new Properties();
		  configs.put("bootstrap.servers", "localhost:9092");
		  configs.put("session.timeout.ms", "10000");
		  configs.put("group.id", "test");
		  configs.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		  configs.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		  KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
		  consumer.subscribe(Arrays.asList("test"));
		  while (true) {
		    ConsumerRecords<String, String> records = consumer.poll(500);
		    for (ConsumerRecord<String, String> record : records) {
		      switch (record.topic()) {
		        case "test":
		          System.out.println(record.value());
		          break;
		        default:
		          throw new IllegalStateException("get message on topic " + record.topic());
		      }
		    }
		  }
		}
}
