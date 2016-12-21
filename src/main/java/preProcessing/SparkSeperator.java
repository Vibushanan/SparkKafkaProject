package preProcessing;

import java.util.List;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import static org.apache.spark.sql.functions.col;
public class SparkSeperator {

	
	
	public static void main (String args[]){
		
		SparkSession spark = SparkSession.builder()
	             .master("local")
	             .config("spark.sql.warehouse.dir", "D:/New folder (3)/Thermostat/")		
	             .appName("Streaming")
	             .getOrCreate();
		
		
		Dataset<Row> input = spark.read().csv("D:/StreamingData.csv").filter(col("_c0").notEqual("run_date"));
	
		
		
		Dataset<Row> filteredRDD  = 	input.filter(col("_c0").equalTo("5/1/2014"));


		Dataset<Row> uniqueID  = filteredRDD.select(input.col("_c2")).distinct();
		
		List<Row> id = uniqueID.collectAsList();
		
		for(Row row : id){
			System.out.println("Processing "+row.getString(0));
			filteredRDD.filter(col("_c2").equalTo(row.getString(0))).write().csv("D:/StreamingInputThermostat/"+row.getString(0));
		}
		
		
		
	}
	
}
