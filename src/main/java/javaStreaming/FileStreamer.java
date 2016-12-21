package javaStreaming;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class FileStreamer implements Runnable{
	
	String fileName ;
	
	org.apache.kafka.clients.producer.Producer<String, String> producer;
	
	FileStreamer(String file,org.apache.kafka.clients.producer.Producer<String, String> p){
		fileName = file;
		producer = p;
	}
	
	
	public void run() {
		
			BufferedReader br = null;
	        String line = "";
	        String cvsSplitBy = ",";
	        
	        
	        try {
	        	
	        	br = new BufferedReader(new FileReader(fileName));
	            while ((line = br.readLine()) != null) {
	            	
	            	
	            	RecordMetadata metadata = null;
	            	
	            	try {									
									producer.send(new ProducerRecord<String,String>("Thermostat1",line));
						Thread.sleep(5000);
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	
	            }
	        	
	        }catch (FileNotFoundException e) {
	        	
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        producer.close();
		
	}

}
