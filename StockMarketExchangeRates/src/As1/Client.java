package As1;
/*
 * By: Frank Vanris
 * Date: 1/27/2024
 * Total Hours: 36
 * Advice Given: Google, Stackoverflow, Co-Pilot, Oracle, Professor, Parental Figures.
 * Desc: This is the client class that has all the application behaviors. and
 * handles the interaction with the users.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Set;

public class Client {
	
	//start line for design purposes
	public static void startLine() {
		System.out.println("##----------------------------------------------------------------------------------------------------------");
	}
	
	//we are showing the suer the first main categories that he can pick from
	private static String getCategory(List<String> categories) {

		//input variable
		Scanner input = new Scanner(System.in);
		
		//category variable
		String selectedCategory = null;
		
		//checking if input is valid, if not keep repeating till user inputs correct values
		boolean isValidInput = false;
		
		do {
			//Interface design that the user can see
			startLine();
			System.out.println("These are the available stock list categories, please choose one (or enter [exit] to leave the program): ");
			
			//printing out the categories to user
			for(int index = 0; index < categories.size(); index++) {
				String item = categories.get(index);
				System.out.println((index + 1) + ". " + item);
			}
		
			try {
				//Read the user input
				String userInput = input.nextLine();
				
				//if user inputs exit, you leave the program.
				if(userInput.toLowerCase().equals("exit")) { 
					return "exit";
				}
				//obtain the input and get the category that the index is directed to
				int userValInput = Integer.parseInt(userInput);	
				
				//checking if user input is greater than the category size if so say that is not valid
				if(userValInput < 1 ||  categories.size() < userValInput ) {
					System.out.println("Sorry that is not valid number must be within the range of what is being shown");
					System.out.println();
					
				} else {
					selectedCategory = categories.get(userValInput - 1);
					isValidInput = true;
				}
			
			//catching an exception of a certain condition is met
			}catch (NumberFormatException e) {
				//handle the case where user input is not a valid integer
				System.out.println("Invalid input. Please enter a valid number.");
				System.out.println();
			}
		}while (!isValidInput);
		
		return selectedCategory;
	}
	
	//we are showing the sub categories of the Category that he chose
	private static String getSubCategory(Map<String, String> subCat2Url) {
		
		//input variable
		Scanner subInput = new Scanner(System.in);
		
		//subcategory variable
		String selectedSubCategory = null;
		
		//if user inputs invalid values then this statement will repeat until user inputs the correct values
		boolean isValidInput = false;
		
		do {
			//Interface design for subCategories
			startLine();
			System.out.println("These are the available stock lists within this category, please choose key:");
			
			//obtaining the subcategory set
			Set<String> subCatsSet = subCat2Url.keySet();
			String subCats[] = new String[subCatsSet.size()];
			System.arraycopy(subCatsSet.toArray(), 0, subCats, 0, subCats.length);
			
			//printing out the subcategory to the user
			for(int index = 0; index < subCats.length; index++) {
				String item = subCats[index];
				System.out.println((index + 1) + ". " + item);
			}
			
			//Read the user input
			String userInput = subInput.nextLine();
		
			try {
				//obtain the input and get the subCategory
				int userSubValInput = Integer.parseInt(userInput);
				
				//checking if user input is greater than the category size if so say that is not valid		
				if(userSubValInput > subCat2Url.size() || userSubValInput < 1) {
					System.out.println("Sorry that is not valid number must be within the range of what is being shown");
					System.out.println();
				} else {
					selectedSubCategory = subCats[userSubValInput - 1];	
					isValidInput = true;
				}
			} catch (NumberFormatException e) {
				//handle the case where user input is not a valid integer
				System.out.println("Invalid input. Please enter a valid number.");
				System.out.println();
			} 
		}while(!isValidInput);
		
		return selectedSubCategory;
	}
	
	//Asking user for given number of company exchange rates to look at.
	private static int getNumCompanies() {
		Scanner input = new Scanner(System.in);
		int numCompanies = 0;
		
		//ask user for number of companies they want to view
		boolean isCorrectInput = false;
		do {
			System.out.println("How many companies would you like to view?");
			try {
				numCompanies = input.nextInt();
				if(numCompanies < 1) {
					System.out.println("Sorry that is not a valid input.");
					System.out.println();
				} else {
					isCorrectInput = true;
				}
			
			}catch(NumberFormatException e) {
				//handle the case where user input is not a valid integer
				System.out.println("Invalid input. Please enter a valid number.");
				System.out.println();
			}
		}while(!isCorrectInput);
		
		return numCompanies;
	}
	
	//we are getting the top four companies that has a high change rate
	private static void getTopCompanies(ArrayList<ExchangeRate> topFour, int tableCount) {
		
		//Interface pop up for top four companies
		startLine();
		System.out.println("This is the list of top companies by change percentage (table " + tableCount + ")");
		
		//obtaining the top four company entries with a high exchange rate
		for(ExchangeRate entry : topFour) {
			System.out.println(entry.getCompanyName() + ", " + entry.getExchangeRate() + "%");
		}	
	}
	
	//main function where everything is called
	public static void main(String[] args) {
		StockAnalyst stockAnalyst = new StockAnalyst();
	
		//obtaining the url
		for (boolean isDone = false; !isDone;) {			
			String mainPageList = stockAnalyst.getUrlText(StockAnalyst.WEB_URL);
			if (mainPageList == null)
				return;
			
			//getting the categories
			List<String> categories = stockAnalyst.getStocksListCategories(mainPageList);
			
			String category = getCategory(categories);
			
			if(category.equals("exit")) {
				System.out.println("Bye Bye");
				return;
			}
		
			//getting the sub categories of a category
			Map<String, String> subCat2Url = stockAnalyst.getStocksListsInListCategory(mainPageList, category);
			String subCategory = getSubCategory(subCat2Url);
			
			String nextUrl = subCat2Url.get(subCategory);
			
			//removing given string from the main URL String to replace it with other url patterns
			String subStringToRemove = "/list/";
			String modifiedWeb_Url = StockAnalyst.WEB_URL.replace(subStringToRemove, "");
			String stockPageUrl = modifiedWeb_Url + nextUrl;
			
			String stockPageList = stockAnalyst.getUrlText(stockPageUrl);
			
			int numCompanies = getNumCompanies();
			
			String strPattern = "(<table[^>]*>)";
			Pattern pattern = Pattern.compile(strPattern);
			Matcher matcher = pattern.matcher(stockPageList);
			
			//SPECIAL CASE: 20 bonus points COMPLETED
			//This is the special case for the bonus points where if there is more than 1 table print out the top 4 exchange rates in (number) of tables.
			for(int tableCount = 1; matcher.find(); tableCount++) {
				//getting the top 4 companies of a sub category with high change rates from multiple tables if any.
				String stockPageListTables = stockPageList.substring(matcher.start());
				ArrayList<ExchangeRate> topFour = stockAnalyst.getTopCompaniesByChangeRate(stockPageListTables, numCompanies);
				if (topFour != null)
					getTopCompanies(topFour, tableCount);
			}
			
		}
	}
}
