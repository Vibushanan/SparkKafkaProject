package kafka;
import org.apache.kafka.clients.producer.Producer;


public interface KafkaProducer {

	
	public Producer<String,String> getKafkaProducer();
	
}
