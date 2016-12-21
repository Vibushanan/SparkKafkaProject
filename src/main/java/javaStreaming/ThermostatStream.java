package javaStreaming;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import kafka.*;


public class ThermostatStream {

	
	
	public static void main(String[] args) {
		
	
		ThermostatStream ts = new ThermostatStream();
		List<String> filestoStream = ts.getFilePaths("D:/StreamingInputThermostat");
		
		System.out.println(filestoStream.size());
		
		
		ExecutorService executorService = Executors.newFixedThreadPool(filestoStream.size());
		
		for(String f : filestoStream){
			Producer p = new Producer("10.172.20.229:9092","all","0","300000");
			
			
			
			executorService.execute(new FileStreamer(f,p.getKafkaProducer()));
			
		}
		
		
		executorService.shutdown();

		
		while (!executorService.isTerminated()) {
        }
        System.out.println("Finished all threads");
		
	}
	
	
	
	public List<String> getFilePaths(String directory){
		List<String> toProcess = new ArrayList<String>();
		
		File dir = new File(directory);
		File[] files = dir.listFiles();
		
		for(File fil : files){
			
			File[] idDir = fil.listFiles();
			
			for(File idr : idDir){
				String idFile = idr.getName();
				
				if(idFile.startsWith("part") && idFile.endsWith(".csv")){
				
					toProcess.add(idr.getAbsolutePath());
					
				}
				
			}
			
			
		}
		
		
		return toProcess;	
	}
	

}
