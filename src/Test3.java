import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Test3 {
	private static final String TOWNS_CSV_FILE = "/home/jamie/software_dev/MatillionTests/test_three.csv";
	
	/*
	 * Returns an array of 2 towns chosen randomly from the towns CSV file
	 * 
	 */
	private String[] getTowns() throws FileNotFoundException {
	
		Scanner scan = new Scanner(new File(TOWNS_CSV_FILE));
		
		List<String> towns = new ArrayList<String>();
		
		// Scan through CSV file and add towns to ArrayList
		// Considered using CSV parser library, but its not required. Just need the String.
		while(scan.hasNext()) {
			towns.add(scan.nextLine() + ",UK");
		}
		
		Random rand = new Random();
		String[] townsOutput = new String[2];
		
		// Add 2 towns to output array
		// Added while loop to repeat assignment just in case the same town gets added twice
		while(townsOutput[0] == townsOutput[1]) {
			townsOutput[0] = towns.get(rand.nextInt(towns.size()));
			townsOutput[1] = towns.get(rand.nextInt(towns.size()));
		}

		scan.close();
		return townsOutput;
	}
	
	private String getDistanceBetweenTowns(String town1, String town2) throws IOException {

		// Build URL
		String url = "https://maps.googleapis.com/maps/api/distancematrix/xml";
		url += "?origins=" + URLEncoder.encode(town1, "UTF-8");
		url += "&destinations=" + URLEncoder.encode(town2, "UTF-8");
		url += "&mode=walking";
		
		// Connect to Google API
		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/xml");
		
		// Get resulting input stream from Google API
		Scanner scan = new Scanner(conn.getInputStream());
		
		// Build XML string
		String xmlOutput = "";
		while(scan.hasNext()) {
			xmlOutput += scan.nextLine();
		}
		
		// Grab relevant distance data
		//  (should use proper XML parser, but this is just a quick hack to get specific data)
		//  (Assumes first <text> tag always relates to the walking duration)
		int start = xmlOutput.indexOf("<text>") + 6;
		int end = xmlOutput.indexOf("</text>");
		String distance = "";
		
		try {
			distance = xmlOutput.substring(start, end);
		} catch(StringIndexOutOfBoundsException e) {
			/*
			 * Sometimes gets a StringIndexOutOfBoundsException
			 * Typically means something was wrong with the request (i.e. Google couldn't parse address)
			 * and unexpected XML containing errors (and no <text> tag) was returned
			 * - Recursively keep running test until a valid result is received
			 */
			String towns[] = getTowns();
			distance = getDistanceBetweenTowns(towns[0], towns[1]);
		}
		
		scan.close();
		conn.disconnect();
		
		return distance;
	}
	
	public String run() throws IOException {
		String[] towns = getTowns();
		String distance = getDistanceBetweenTowns(towns[0], towns[1]);
		
		String outputString = "It will take " + distance + " to walk from " + towns[0].split(",")[0]
		                         + " to " + towns[1].split(",")[0] + ".";
		
		return outputString;
	}
}
