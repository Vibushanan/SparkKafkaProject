package preProcessing;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;
import scala.Tuple2;

public class SparkConsumer {

	public static void main(String[] args) {
		SparkSession spark = SparkSession.builder()
	             .master("local[2]")
	             .config("spark.sql.warehouse.dir", "D:/New folder (3)/Thermostat/")	
	             .config("spark.cassandra.connection.host", "10.172.20.229")	
	             .appName("Streaming")
	             .getOrCreate();
		
		
		SparkContext sc = spark.sparkContext();
		
		
		final JavaSparkContext jsctxt = new JavaSparkContext(sc);
		JavaStreamingContext jsc = new JavaStreamingContext(JavaSparkContext.fromSparkContext(sc),
				Durations.seconds(20));
		
		Map<String,Integer> topics= new HashMap<String,Integer>();
		
		topics.put("Thermostat1", 2);
		
		JavaPairReceiverInputDStream<String, String> messages = KafkaUtils.createStream(jsc, "10.172.20.234:2181", "spark", topics);
		
		com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions(jsctxt).cassandraTable("test", "employee")
		.foreach(new VoidFunction<CassandraRow>(){

			public void call(CassandraRow t) throws Exception {
				System.out.println(t.toString());
				
			}
			
		});
		
		messages.print();
		
		messages.map(new Function<Tuple2<String,String>,String>(){

			public String call(Tuple2<String, String> v1) throws Exception {
				
				String[] msg = v1._2.split(",");
				
				String date = msg[0];
				
				String icdid = msg[2];
				
				double current_used = Double.valueOf(msg[6]);
				
				String runTime = msg[7];
				
				double delT =Double.valueOf(msg[14]);
				
				double  kw_dt_seer_by_dd_sqft = Double.valueOf(msg[16]);
				
				return null;
			}
			
		});
		jsc.start();
		
		try {
			jsc.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
