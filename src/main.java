import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;



public class Main {

	public static void main(String[] args) throws IOException, RowsExceededException, WriteException{
		System.out.println("Bill or member data? Type 'B' or 'M'.");
		Scanner scan = new Scanner(System.in);
		String response = scan.nextLine();
		System.out.println("How many bills would you like to analyze?");
		int limit = scan.nextInt(); //determines how many samples to retrieve
		URLGatherer g = new URLGatherer();
		Analyzer a = new Analyzer();
		
		if (response.equalsIgnoreCase("B")||response.equalsIgnoreCase("M")) {
			ArrayList<String> urls = g.gather(response,limit); //gathers URLs
			
			a.analyze(response,urls);
			System.out.println("Done!");
		}
		else {
			System.out.println("Invalid input. Goodbye!");
		}
		
		
		
		
		scan.close();

	}

}
