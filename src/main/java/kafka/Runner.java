package kafka;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Runner {

	public static void main(String[] args) {
		
		Producer prod = new Producer("10.172.20.229:9092","all","0","3000");
		
		
		org.apache.kafka.clients.producer.Producer<String, String>  prd= prod.getKafkaProducer();
		System.out.println(prd.metrics());
	
		RecordMetadata metadata = null;
		
		
			try {
				metadata =	prd.send(new ProducerRecord<String,String>("Thermostat","","1newvalue")).get();
			
			
			System.out.println(metadata.partition());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

}
