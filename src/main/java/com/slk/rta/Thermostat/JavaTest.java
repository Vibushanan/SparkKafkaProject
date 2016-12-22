package com.slk.rta.Thermostat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;

public class JavaTest {

	public static void main(String[] args) {
		
		String startDateString = "06/27/2007";
	    DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
	    Date startDate;
	    try {
	        startDate = df.parse(startDateString);
	        String newDateString = df.format(startDate);
	        System.out.println("Date   ..."+newDateString);
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
		
		
		
		
		
		Date expiry = new Date(Long.parseLong("1398937990"));
		
		
		System.out.println(expiry.getTime());
		
		Geocoder geoCoder = new Geocoder();
		
		
		GeocoderRequest geocoderRequest = 
	            new GeocoderRequestBuilder().setLocation(new LatLng("40.3094858","-84.1462389")).getGeocoderRequest();
		
		
		
		try {
			GeocodeResponse geocoderResponse=geoCoder.geocode(geocoderRequest);
			
			List<GeocoderResult> result = geocoderResponse.getResults();
			
			
			
			for(GeocoderResult res : result){
				
				
				
				
				System.out.println();
				
				System.out.println("res.getAddressComponents()"+res);
				
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		

	}

}
