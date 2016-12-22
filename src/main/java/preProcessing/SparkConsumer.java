package preProcessing;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.datastax.driver.core.LocalDate;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.RDDAndDStreamCommonJavaFunctions;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import com.datastax.spark.connector.writer.RowWriterFactory;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.LatLng;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;
import scala.Tuple2;

public class SparkConsumer {
	
	
	public static Date getDate(String date) {

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate = null;
		try {
			startDate = df.parse(date);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return startDate;

	}
	
	
	public static String getAddress(String lat,String log){
		
		GeocodeResponse geocoderResponse = null;
		
		Geocoder geoCoder = new Geocoder();
		
		
		GeocoderRequest geocoderRequest = 
	            new GeocoderRequestBuilder().setLocation(new LatLng(lat,log)).getGeocoderRequest();
		
		
		try {
			geocoderResponse=geoCoder.geocode(geocoderRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return geocoderResponse.getResults().get(0).getFormattedAddress();	
	}
	
	

	public static void main(String[] args) {
		SparkSession spark = SparkSession
				.builder()
				.master("local[2]")
				.config("spark.sql.warehouse.dir",
						"D:/New folder (3)/Thermostat/")
				.config("spark.cassandra.connection.host", "10.172.20.229")
				.appName("Streaming").getOrCreate();

		Map<String, String> colMap = new HashMap<String, String>();
	
		colMap.put("rundate", "rundate");
		colMap.put("icdid", "icdid");
		colMap.put("runtime", "runtime");
		colMap.put("current_used", "current_used");
		colMap.put("deltat", "deltat");
		colMap.put("kw_dt_seer_by_dd_sqft", "kw_dt_seer_by_dd_sqft");
		colMap.put("state", "state");
		
		SparkContext sc = spark.sparkContext();

		final JavaSparkContext jsctxt = new JavaSparkContext(sc);
		JavaStreamingContext jsc = new JavaStreamingContext(
				JavaSparkContext.fromSparkContext(sc), Durations.seconds(20));

		Map<String, Integer> topics = new HashMap<String, Integer>();

		topics.put("Thermostat1", 2);

		JavaPairReceiverInputDStream<String, String> messages = KafkaUtils
				.createStream(jsc, "10.172.20.234:2181", "spark", topics);

		com.datastax.spark.connector.japi.CassandraJavaUtil
				.javaFunctions(jsctxt).cassandraTable("test", "employee")
				.foreach(new VoidFunction<CassandraRow>() {

					public void call(CassandraRow t) throws Exception {
						System.out.println(t.toString());

					}

				});

		messages.print();

		JavaDStream<RawData> cRow = messages
				.map(new Function<Tuple2<String, String>, RawData>() {

					public RawData call(Tuple2<String, String> v1)
							throws Exception {
						Map<String, Object> cassJavaRow = new HashMap<String, Object>();

						String[] msg = v1._2.split(",");

						if (msg.length >= 16) {

							LocalDate rdate = null;

							if (!msg[0].isEmpty()) {

								Date dte = getDate(msg[0]);

								if (!(dte == null)) {
									
									System.out.println(dte.getDay()+","+ dte.getMonth()+","+
											dte.getYear());
									
									
									rdate = LocalDate.fromYearMonthDay(
											dte.getYear(),dte.getMonth(),dte.getDay()
											);
								
								}

							}
System.out.println("Message   : "+v1._2);							
				int cnt=0;			
			for(String ste: msg){
				System.out.println(cnt+"  :  "+ste);
				cnt++;
				
			}
			
							return new RawData(rdate, msg[2], 
									(long)Double.parseDouble(msg[7]),
									Double.parseDouble(msg[6]),
									Double.parseDouble(msg[14]),
									Double.parseDouble(msg[16]), 
									getAddress(msg[3], msg[4]));
						} else {
							return null;
						}

					}

				});

		
		cRow.foreachRDD(new VoidFunction<JavaRDD<RawData>>(){

		
			@Override
			public void call(JavaRDD<RawData> rdd) throws Exception {
				
				System.out.println("Writing to Table");
				com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions(rdd).writerBuilder("thermostat", "rawdata_by_icdid", CassandraJavaUtil.mapToRow(RawData.class)).saveToCassandra();
			
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

	
	public static class RawData implements Serializable {

		private LocalDate rundate;
		private String icdid;
		private long runtime;
		private double current_used;
		private double deltat;
		private double kw_dt_seer_by_dd_sqft;
		private String state;
		
		public RawData(LocalDate rundate, String icdid, long runtime,
				double current_used, double deltat,
				double kw_dt_seer_by_dd_sqft, String state) {
			super();
			this.rundate = rundate;
			this.icdid = icdid;
			this.runtime = runtime;
			this.current_used = current_used;
			this.deltat = deltat;
			this.kw_dt_seer_by_dd_sqft = kw_dt_seer_by_dd_sqft;
			this.state = state;
		}
		
		public LocalDate getRundate() {
			return rundate;
		}

		public void setRundate(LocalDate rundate) {
			this.rundate = rundate;
		}

		public String getIcdid() {
			return icdid;
		}

		public void setIcdid(String icdid) {
			this.icdid = icdid;
		}

		public long getRuntime() {
			return runtime;
		}

		public void setRuntime(long runtime) {
			this.runtime = runtime;
		}

		public double getCurrent_used() {
			return current_used;
		}

		public void setCurrent_used(double current_used) {
			this.current_used = current_used;
		}

		public double getDeltat() {
			return deltat;
		}

		public void setDeltat(double deltat) {
			this.deltat = deltat;
		}

		public double getKw_dt_seer_by_dd_sqft() {
			return kw_dt_seer_by_dd_sqft;
		}

		public void setKw_dt_seer_by_dd_sqft(double kw_dt_seer_by_dd_sqft) {
			this.kw_dt_seer_by_dd_sqft = kw_dt_seer_by_dd_sqft;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}
		
		//public RawData() {}
		
		
	}
	
	
	
}
