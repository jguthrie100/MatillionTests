
public class MatillionTests {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Running Test 1: ");
		Test1 test1 = new Test1();
		int numDifferences = test1.run("ABCDEF123456", "ABxDEx1234xx");
		System.out.println(numDifferences + " differences\n");
		
		System.out.println("Running Test 2: ");
		Test2 test2 = new Test2();
		test2.run();
		test2.close();
		System.out.println();
		
		System.out.println("Running Test 3: ");
		Test3 test3 = new Test3();
		String output = test3.run();
		System.out.println(output);
	}

}
