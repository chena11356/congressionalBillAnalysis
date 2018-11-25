import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Analyzer {

	public String removeNonNums(String input) {
		String res = "";
		for (int i = 0; i < input.length(); i++) {
			if (Character.isDigit(input.charAt(i)))
				res+=input.charAt(i);
		}
		return res;
	}
	
	public void analyze(String response, ArrayList<String> urls) throws IOException, RowsExceededException, WriteException { //response is "B" or "M", urls is an ArrayList of urls
		WritableWorkbook out;
		WritableSheet sheet;
		Label label;
		Document d; //the page it's on
		int j; //keeps track of which variable it's on
		String temp;
		String temp2;
		Document dTemp;
		Element eTemp;
		Elements t;
		
		int numCo, coDem, coRep, coInd;
		/*
		int numCongress, yearCongress, //number and year of congress, DONE
		rcVotes, //number of roll call votes, DONE
		amendSubm, //number of amendments submitted
		amendProp, //number of amendments proposed on senate floor
		amendRc, //roll call votes on amendments in Senate
		amendAgree, //amendments agreed to
		amendDem, //number of democrats who sponsored amendment
		amendRep, //number of republicans who sponsored amendment
		amendInd, //number of independents who sponsored amendment
		amendDemCo, amendRepCo, amendIndCo, //above, but for cosponsors
		numCo, //number of cosponsors
		coDem, //number of democrat cosponsors
		coRep, //number of republican cosponsors
		coInd, //number of independent cosponsors
		coNE, //number of cosponsors from ME, NH, VT, MA, CT, RI, NY, NJ, PA
		coMW, //above, but from OH, MI, IN, IL, WI, MO, IA, MN, KS, NE, SD, ND
		coW, //above, but from WA, OR, CA, AZ, NV, ID, MT, WY, UT, CO, NM
		coS, //above, but from TX, OK, AR, LA, AL, MS, TN, KY, FL, GA, SC, NC, VA, WV, DC, MD, DE
		numBillsYear; //number of bills passed the year the bill was proposed
		
		String sponsParty, sponsState, //party and state of sponsor (D / R / I ; state abbreviation) 
		committee1, committee2, //committee, 2 if there are 2 
		billStatus, //introduced, passed house, passed senate, etc.
		chamber, //Senate vs House
		presParty, //party in control of White House at time of passing
		subj; //subject - policy area
		
		double amendDemPercent, //dems who sponsored amendments out of total amendment sponsors, times 100%
		amendRepPercent, //above, but for reps 
		amendIndPercent, //above, but for inds
		amendDemCoPercent, amendRepCoPercent, amendIndCoPercent, //above, but for cosponsors
		partisanship; //how partisan was the bill?
		
		boolean war; //was country at war when bill was proposed?
		*/
		
		//---------------------------------------------------------//
		//------------------NOW FIND THESE VARIABLES---------------//
		//---------------------------------------------------------//
		
		int tryCount = 0;
		int maxTries = 20;
		
		
		if(response.equalsIgnoreCase("B")) {
			out = Workbook.createWorkbook(new File("billData"+System.currentTimeMillis()+".xls"));
			sheet = out.createSheet("Main", 0);



			for (int i = 0; i < urls.size(); i++) {
				j = 0;
				while (true) {
					try {d = Jsoup.connect(urls.get(i)+"/amendments").timeout(10000).get(); //connect to website
						break;
					} catch (IOException h) {
						i++;
						if (++tryCount == maxTries) throw h;
					}
				}
				

				
				//retrieve numCongress
				t = d.getElementsByTag("title");
				temp = t.first().text(); //gets title
				temp2 = temp.substring(temp.indexOf("-",temp.indexOf("-")+1)+2, temp.indexOf("-",temp.indexOf("-")+1)+7);
				temp2 = removeNonNums(temp2);
				label = new Label(j, i, temp2);
				sheet.addCell(label);
				j++;
				
				
				//retrieve yearCongress
				temp = temp.substring(temp.indexOf("(")+1, temp.indexOf("(")+5);
				label = new Label(j, i, temp);
				sheet.addCell(label);
				j++;
				
				//retrieve sponsParty
				t = d.getElementsByClass("overview_wrapper bill");
				temp = t.first().html(); //gets the whole div
				dTemp = Jsoup.parseBodyFragment(temp); //makes the div part a temp document
				t = dTemp.getElementsByTag("td"); //gives the td elements of the overview
				temp = t.html();
				temp2 = temp.substring(temp.indexOf("[")+1, temp.indexOf("[")+2); //gives the party
				label = new Label(j,i, temp2);
				sheet.addCell(label);
				j++;
				
				//retrieve sponsState
				temp = temp.substring(temp.indexOf("["));
				temp2 = temp.substring(temp.indexOf("-")+1,temp.indexOf("-")+3); //gives the state abbr
				label = new Label(j,i,temp2);
				sheet.addCell(label);
				j++;
				
				//retrieve committee1
				//retrieve committee2
				
				
				//retrieve rcVotes
				temp2 = temp.substring(temp.lastIndexOf(">",temp.length()-3)+1, temp.lastIndexOf(">",temp.length()-3)+4); //gives the party
				temp2 = removeNonNums(temp2);
				if (temp2.equals(""))
					temp2 = "0";
				label = new Label(j,i, temp2);
				sheet.addCell(label);
				j++;
				
				//retrieve billStatus
				t = d.getElementsByClass("hide_fromsighted");
				temp = t.eq(t.size()-2).last().text();
				temp = temp.substring(temp.indexOf("status")+7);
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
				//retrieve amendSubm
				t = d.getElementsByClass("selected");
				temp = t.last().html(); //gets the whole selected li
				if (temp.indexOf("span class=")==-1)
					temp = t.eq(t.size()-2).last().html();
				temp = temp.substring(temp.indexOf("span class="),temp.indexOf("</span"));
				temp = removeNonNums(temp);
				
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
				//retrieve numCo
				while (true) {
					try {d = Jsoup.connect(urls.get(i)+"/cosponsors").timeout(10000).get(); //connect to website
						break;
					} catch (IOException e) {
						i++;
						if (++tryCount == maxTries) throw e;
					}
				}
				
				
				
				t = d.getElementsByClass("selected");
				temp = t.last().html(); //gets the whole selected li
				if (temp.indexOf("span class=")==-1)
					temp = t.eq(t.size()-2).last().html();
				temp = temp.substring(temp.indexOf("span class="),temp.indexOf("</span"));
				temp = removeNonNums(temp);
				numCo = Integer.parseInt(temp);
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
				//retrieve coDem
				eTemp = d.getElementById("facetItempartyDemocraticcount");
				if (eTemp!=null) {
					temp = eTemp.text();
					temp = removeNonNums(temp);
				}
				else {
					temp = "0";
				}
				coDem = Integer.parseInt(temp);
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
				//retrieve coRep
				eTemp = d.getElementById("facetItempartyRepublicancount");
				if (eTemp!=null) {
					temp = eTemp.text();
					temp = removeNonNums(temp);
				}
				else {
					temp = "0";
				}
				coRep = Integer.parseInt(temp);
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
				//retrieve coInd
				eTemp = d.getElementById("facetItempartyIndependentcount");
				if (eTemp!=null) {
					temp = eTemp.text();
					temp = removeNonNums(temp);
				}
				else {
					temp = "0";
				}
				coInd = Integer.parseInt(temp);
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
				//make coDemPercent
				if (numCo!=0) {
					temp = "" + (double)(coDem)/(coDem+coRep+coInd);
				} else {
					temp = "0";
				}
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
				//make coRepPercent
				if (numCo!=0) {
					temp = "" + (double)(coRep)/(coDem+coRep+coInd);
				} else {
					temp = "0";
				}
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
			}
		}
		else {
			out = Workbook.createWorkbook(new File("memberData"+System.currentTimeMillis()+".xls"));
			sheet = out.createSheet("Main", 0);
		}
		

		
		/*label = new Label(0, 2, "A label record"); //0 is column 1, 2 is row 2
		sheet.addCell(label); 
		Number number = new Number(3, 4, 3.1459); 
		sheet.addCell(number);*/
		

		out.write(); 
		out.close();
		
	}
	
}
