/*
 * By: Frank Vanris
 * Date: 1/27/2024
 * Total Hours: 36
 * Assisted: Google, Stackoverflow, Co-Pilot, Oracle, Professor, Parental Figures.
 * Desc: This is the Stock Analyst class that contains all the methods for the 
 * Stock Analyst Interface.
 */
package As1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class StockAnalyst implements IStockAnalyst {
	
	//We are obtaining the URL and returning a String of text from that url
	@Override
	public String getUrlText(String url) {
		
		StringBuilder resultBuilder = new StringBuilder();
		
		try {
			
			//creating a URL object
			URL stockAnalystSite = new URL(url);
			
			//opening the connection to the URL
			URLConnection connection = stockAnalystSite.openConnection();
			
			//create a buffered reader to read the content 
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			//read the content
			String line;
			while((line = reader.readLine()) != null) {
				resultBuilder.append(line);
			}
			
			//closing the website reader
			reader.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//Handles IOException
			e.printStackTrace();
			return null;
		}
		
		return resultBuilder.toString();
	}

	//we are getting the main categories from the website
	@Override
	public List<String> getStocksListCategories(String urlText) {
		
		//creating a String list that consists of all the listing categories
		List<String> stockListCategories = new ArrayList<>();
		
		//reading the text of the Stock Analyst and obtaining the List categories within the website itself
		Pattern pattern = Pattern.compile("<h2[^>]*>([^<]*)</h2>");
		
		Matcher matcher = pattern.matcher(urlText);
		
		//while it finds a match we add it to the list
		while(matcher.find()) {
			stockListCategories.add(matcher.group(1));
		}
		
		return stockListCategories;
	}

	//getting the sub categories from a main category
	@Override
	public Map<String, String> getStocksListsInListCategory(String urlText, String stockCategoryName) {
		
		//creating a map object that stores the given information needed
		Map<String, String> subCat2Url = new HashMap<>();
		
		//We are breaking it down into 2 segments. one that obtains the main category, and another
		//that obtains the sub categories from that main category.
		//the sub is under the ul part, and the main is under the h2 part.
		
		// Find the category
		String patternStrng = "<h2 [^>]*>" + stockCategoryName + "</h2>\s*";
		
		Pattern pattern = Pattern.compile(patternStrng);
		Matcher matcher = pattern.matcher(urlText);
		
		//if matcher cant find print error
		if (!matcher.find()) {
			System.err.println("Category '" + stockCategoryName + "' not found");
			return null;
		}
		
		//obtaining the position 
		int afterCategoryPos = matcher.end();
		
		// Find the <ul>...</ul> part
		patternStrng =  "<ul[^>]*>.*?</ul>";
		
		pattern = Pattern.compile(patternStrng);
		matcher = pattern.matcher(urlText.subSequence(afterCategoryPos, urlText.length()));
		
		//if matcher can't find print error
		if (!matcher.find()) {
			System.err.println("Category '" + stockCategoryName + "' not found");
			return null;
		}
		
		//getting the rest of the length of the sub directories to obtain them from start to end
		int ulStart = matcher.start();
		int ulEnd = matcher.end();
		
		//reading the text of the stock Analyst and obtaining the sub list categories of the main categories from the website
		patternStrng = "<li[^>]*>\s*";
		patternStrng += "<a href=\"([^\"]*)\">([^<]*)</a>\s*";
		patternStrng += "</li>";
		pattern = Pattern.compile(patternStrng);
		matcher = pattern.matcher(urlText.subSequence(afterCategoryPos + ulStart, afterCategoryPos + ulEnd));
		
		//while it finds each group it is added to the map
		while(matcher.find()) {
			subCat2Url.put(matcher.group(2), matcher.group(1));
		}
		
		return subCat2Url;
	}

	
	//obtaining the companies exchange rates and the company names from the subcategory that was chosen.
	//UPDATE: change given return to ArrayList instead of TreeMap due to duplicate issues. Learned that
	//Tree maps cannot have duplicate keys so instead I changed it into an ArrayList to allow me to have duplicates
	//for exchange rates.
	@Override
	public ArrayList<ExchangeRate> getTopCompaniesByChangeRate(String urlText, int topCount) {
		
		//finding the column of the change rage (new function made)
		int[] colIndices = getColIndices(urlText); //returns company name index , exchange rate index, position, trStart Position, and trEndPosition
		int companyNameCol = colIndices[0];
		int changeRateCol = colIndices[1];
		
		// Getting the topCountingCompanies
		ArrayList<ExchangeRate> topCountingCompanies = getTopCountingCompanies(urlText, companyNameCol, changeRateCol, topCount);
				
		return topCountingCompanies;
	}
	
	// Find which column is the changeRateCol and companyNameCol from the thead		
	public int[] getColIndices(String urlText) {
		
		//obtain the table and thead
		String patternStrng = "<table[^>]*>\s*";
		patternStrng += "<thead [^>]*>";
		
		Pattern pattern = Pattern.compile(patternStrng);
		Matcher matcher = pattern.matcher(urlText);
		
		//print error if cannot find
		if (!matcher.find()) {
			System.err.println("thead  and body not found.");
			return null;
		}
		
		//get position
		int pos = matcher.end();
		
		//obtain the tr in text
		patternStrng = "<tr[^>]*>.*?</tr>";
		
		pattern = Pattern.compile(patternStrng);
		matcher = pattern.matcher(urlText.subSequence(pos, urlText.length()));
		
		//print error if cannot find
		if (!matcher.find()) {
			System.err.println("tr not found.");
			return null;
		}
		
		//getting the rest of the length from what is inside tr
		int trStart = matcher.start();
		int trEnd = matcher.end();
		
		
		//getting the column position of where exchange rate and the name of the company is
		patternStrng = "<th id=\"([^\"]*)"; 

		pattern = Pattern.compile(patternStrng);
		matcher = pattern.matcher(urlText.subSequence(pos + trStart, pos + trEnd));
		
		//where change rate and company name is in the columns of the table
		int changeRateCol = 0;
		int companyNameCol = 0;
		
		for(int index = 0 ; matcher.find(); ++index) {
			if (matcher.group(1).equals("change"))
				changeRateCol = index;
			else if(matcher.group(1).equals("n"))
				companyNameCol = index;
		}
		
		//returning the positions where exchange rate and company name is in the table
		return new int[] {companyNameCol, changeRateCol};
	}
	
	//obtain the Name and Exchange Rate from the columns (Changed my personal function into an ArrayList)
	public ArrayList<ExchangeRate> getTopCountingCompanies(String urlText, int nameCol, int changeRateCol, int topCount) {
		
		//obtaining the body now and obtaining the different change rates
		
		//obtaining body
		String patternStrng = "<tbody[^>]*>.*?</tbody>";
		
		Pattern bodyPattern = Pattern.compile(patternStrng);
		Matcher bodyMatcher = bodyPattern.matcher(urlText);
		
		//if text not found print error
		if (!bodyMatcher.find()) {
			System.err.println("body not found.");
			return null;
		}
		
		//getting the rest of the length from what is inside body
		int bodyStart = bodyMatcher.start();
		int bodyEnd = bodyMatcher.end();
		
		// for all rows
		patternStrng = "<tr[^>]*>.*?</tr>";
		
		//get tr
		Pattern rowPattern = Pattern.compile(patternStrng);
		Matcher rowMatcher = rowPattern.matcher(urlText.subSequence(bodyStart, bodyEnd));
		
		//ArrayList that contains an ExchangeRate object that will hold my exchange rates and company names
		ArrayList<ExchangeRate> rate2names = new ArrayList<>();
		
		//while we get a row we go through the columns and obtain the company name and the exchange rate
		//after we obtain both we place them in the tree map
		while(rowMatcher.find()) {
			
			//get start/end pos
			int trStart = rowMatcher.start();
			int trEnd = rowMatcher.end();
			
			//getting the rows with exchange rate
			patternStrng = "<td[^>]*>(.*?)</td>";
			Pattern colPattern = Pattern.compile(patternStrng);
			Matcher colMatcher = colPattern.matcher(urlText.subSequence(bodyStart + trStart, bodyStart + trEnd));
			
			//change rate and company name variables
			double exchangeRate = 0;
			String companyName = "";
			
			boolean isValidRate = true; // Presume valid 
			
			//another loop that goes through the columns within each row
			for(int index = 0; colMatcher.find(); index++) {
				if(index == nameCol) {
					companyName = colMatcher.group(1);
				}
				else if(index == changeRateCol) {
					try {
						String str = colMatcher.group(1);
						String newStr = str.replace("%", "");
						
						exchangeRate = Double.parseDouble(newStr);
					}catch (NumberFormatException e) {
						isValidRate = false;
					}
				} 
			}
			
			
			if (isValidRate) {
				
				//Store information in the arraylist and sort the arraylist from high to low
				rate2names.add(new ExchangeRate(exchangeRate, companyName));
				rate2names.sort(new ExchangeRate.CompareExchangeRatesHighToLow());
				
				//keep only the top count entries:
				if(rate2names.size() > topCount) {
					rate2names.remove(rate2names.size() - 1);
				}
			}
		}
		
		
		
		return rate2names;
	}
}
