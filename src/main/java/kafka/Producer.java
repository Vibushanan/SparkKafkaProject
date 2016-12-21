package kafka;

import java.util.Properties;

public class Producer implements KafkaProducer{

	Properties props = new Properties();
	
	public Producer(String bootstrapServer,String acks, String retries,String timeout){
		props.put("bootstrap.servers",bootstrapServer);
		props.put("acks", acks);
		props.put("retries", retries);
	props.put("request.timeout.ms", "50000");
		props.put("metadata.fetch.timeout.ms", timeout);	
		props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
	}
	

	public org.apache.kafka.clients.producer.Producer<String, String> getKafkaProducer() {
		// TODO Auto-generated method stub
	
		org.apache.kafka.clients.producer.Producer<String,String> producer = new org.apache.kafka.clients.producer.KafkaProducer<String,String>(props);
		
		return producer;
	}

	
}
