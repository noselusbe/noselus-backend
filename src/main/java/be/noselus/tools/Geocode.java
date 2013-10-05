package be.noselus.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Geocode {

	@SuppressWarnings("unchecked")
	public static String lookup(double latitude, double longitude) throws IOException {
		URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=false");
		
		InputStream is = url.openConnection().getInputStream();

	    BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );
		
		Gson parser = new GsonBuilder().setPrettyPrinting().create();
		Map<String, List<Map<String, Object>>> obj = parser.fromJson(reader, TreeMap.class);
		
		List<Map<String, Object>> results = obj.get("results");
		
		for (int i = 0; i < results.size(); i++) {
			List<String> types = (List<String>) results.get(i).get("types");
			if (types.contains("street_address")) {
				return (String) results.get(i).get("formatted_address");
			}
		}
		
		return (String) obj.get("results").get(0).get("formatted_address");
	}
	
	public static Map<String, Double> lookup(String address) throws IOException {
		String addr = new String(address).replaceAll(" ", "+");
		URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address=" + addr + "&sensor=false");
		
		InputStream is = url.openConnection().getInputStream();

	    BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );
		
		Gson parser = new GsonBuilder().setPrettyPrinting().create();
		Map<String, List<Map<String, Object>>> obj = parser.fromJson(reader, TreeMap.class);
		Map<String, Map<String, Double>> geometry = (Map<String, Map<String, Double>>) obj.get("results").get(0).get("geometry");
		
		Map<String, Double> result = new HashMap<>();
		result.put("latitude", geometry.get("location").get("lat"));
		result.put("longitude", geometry.get("location").get("lng"));
		
		return result;
	}
}
