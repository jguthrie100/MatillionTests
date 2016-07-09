
public class Test1 {
	
	public int run(String str1, String str2) {
		// Check strings are same length
		if(str1.length() != str2.length()) {
			System.out.println("Error: Both strings must be equal length");
			return -1;
		}
		
		int numDifferences = 0;
		
		// Loop through each char and compare between strings
		for(int i=0; i<str1.length(); i++) {
			if(str1.charAt(i) != str2.charAt(i)) {
				numDifferences++;
			}
		}
		return numDifferences;
	}
}
