import java.sql.*;
import java.util.Scanner;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Test2 {
	private MysqlDataSource dataSource;
	private Connection conn;
	private Scanner scan;
	
	private static final String MYSQL_USER = "java";
	private static final String MYSQL_PASS = "javapassword";
	private static final String MYSQL_SERVER = "localhost";
	
	public Test2() throws SQLException {
		// Connect to Foodmart DB
		this.dataSource = new MysqlDataSource();
		dataSource.setUser(MYSQL_USER);
		dataSource.setPassword(MYSQL_PASS);
		dataSource.setServerName(MYSQL_SERVER);
		dataSource.setDatabaseName("foodmart");
		
		this.conn = dataSource.getConnection();
		this.scan = new Scanner(System.in);
	}
	
	// Close DB connections etc
	public void close() throws SQLException {
		scan.close();
		conn.close();
	}
	
	// Test 2
	public void run() throws SQLException {
		
		// Get Department selection
		String sql = "SELECT department_description FROM department";
		String instruction = "Select a Department: ";
		String department_description = displayMenu(sql, instruction);
		
		// Get Pay Type selection
		sql = "SELECT DISTINCT pay_type FROM position";
		instruction = "Select a Pay Type: ";
		String pay_type = displayMenu(sql, instruction);
		
		// Get Education Level selection
		sql = "SELECT DISTINCT education_level FROM employee";
		instruction = "Select an Education Level: ";
		String education_level = displayMenu(sql, instruction);
		
		// Print selection summary
		System.out.println("\nDisplaying FoodMart employees with following attributes:");
		System.out.println("   Department: " + department_description);
		System.out.println("   Pay Type:   " + pay_type);
		System.out.println("   Education:  " + education_level);
		System.out.println();
		
		// All selections made - begin main SQL process
		
		// Main SQL statement
		sql  = "SELECT full_name, birth_date, hire_date, gender ";
		sql += "FROM employee ";
		sql += "WHERE department_id = (SELECT department_id FROM department WHERE department_description = ?) ";
		sql += "AND position_id IN (SELECT position_id FROM position WHERE pay_type = ?) ";
		sql += "AND education_level = ? ";
		sql += "ORDER BY full_name ASC";
		
		Statement stmt = conn.createStatement();
		
		// Prepare main statement and sub-in parameters
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, department_description);
		pstmt.setString(2, pay_type);
		pstmt.setString(3, education_level);
		
		// Execute SQL
		ResultSet rs = pstmt.executeQuery();
		
		// Print Results
		printResults(rs);

		// Close connections etc
		rs.close();
		pstmt.close();
		stmt.close();
	}
	
	/*
	 * Displays menu based on passed in sql
	 * 
	 * Returns user's selection
	 */
	private String displayMenu(String sql, String instruction) throws SQLException {
				
		Statement stmt = conn.createStatement();		
		ResultSet choices = stmt.executeQuery(sql);
		
		// Print user instruction
		System.out.println(instruction);
		
		// Loop through all choices and print each one
		int lastRow = 0;
		while(choices.next()) {
			lastRow = choices.getRow();
			System.out.println(lastRow + ": " + choices.getString(1));
		}
		
		if(lastRow == 0) {
			throw new SQLException("Menu unavailable as SQL Query returned no results");
		}
		
		// Get user's menu selection
		// Loop Scanner input until valid integer option is entered
		int selection = 0;
		boolean validSelection = false;
		while (!validSelection) {
			System.out.println("\nEnter selection: ");
	        try {
	        	// Get user input
	    		selection = scan.nextInt();
	    		
	    		// Handle integers not in the list of options
	    		if(selection < 1 || selection > lastRow) {
	    			throw new IndexOutOfBoundsException();
	    		}
	    		validSelection = true;
	        }
	        catch (java.util.InputMismatchException e) {
	        	System.out.print("Error: Please enter a number from the list..");
	            scan.nextLine();
	        }
	        catch (IndexOutOfBoundsException e) {
	        	System.out.print("Error: Please enter a number from the list..");
	        	scan.nextLine();
	        }
	    }
		
		// Get data from the relevant row based on user's selection
		choices.absolute(selection);
		String userSelection = choices.getString(1);
		
		// Close DB connections etc
		choices.close();
		stmt.close();
		
		return userSelection;
	}
	
	// Print SQL results to a formatted results table
	private void printResults(ResultSet rs) throws SQLException {

		// Colwidth array essentially specifies width of each col in the results table
		int[] colWidth = {"Full Name".length(), "Birth Date".length(), "Hire Date".length(), "Gender".length()};
		
		// Make an initial pass through of the results to work out the longest value in each column and set col width
		while(rs.next()) {
			colWidth[0] = Math.max(rs.getString("full_name").length(), colWidth[0]);
			colWidth[1] = Math.max(rs.getString("birth_date").length(), colWidth[1]);
			colWidth[2] = Math.max(rs.getString("hire_date").length(), colWidth[2]);
			colWidth[3] = Math.max(rs.getString("gender").length(), colWidth[3]);
		}
		
		// Create horizontal line string
		int resultsTableWidth = colWidth[0] + colWidth[1] + colWidth[2] + colWidth[3] + 13;
		String hLine = "";
		for(int i=0; i<resultsTableWidth; i++) {
			hLine += "-";
		}
		
		// Create table heading
		String tableHeading = "| " + String.format("%-"+colWidth[0]+"s", "Full Name") + " | " +
		String.format("%-"+colWidth[1]+"s", "Birth Date") + " | " +
		String.format("%-"+colWidth[2]+"s", "Hire Date") + " | " +
		String.format("%-"+colWidth[3]+"s", "Gender") + " |";
		
		// Print table heading
		System.out.println(hLine);
		System.out.println(tableHeading);
		System.out.println(hLine);
		
		// Make a 2nd pass through all the results and print to results table
		rs.absolute(1);
		boolean noResults = true;
		while(rs.next()) {
			noResults = false;
			
			String rowOutput = "| " + String.format("%-"+colWidth[0]+"s", rs.getString("full_name")) + " | " +
			String.format("%-"+colWidth[1]+"s", rs.getString("birth_date")) + " | " +
			String.format("%-"+colWidth[2]+"s", rs.getString("hire_date")) + " | " +
			String.format("%-"+colWidth[3]+"s", rs.getString("gender")) + " |";

			System.out.println(rowOutput);
		}
		
		if(noResults) {
			System.out.println(
				String.format("%-"+(hLine.length()-1)+"s", "| No employees matched your choices") + "|"
			);
		}
		
		System.out.println(hLine);
	}
}
