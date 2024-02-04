package As1;
/*
 * By: Frank Vanris
 * Date: 1/27/2024
 * Total Hours: 36
 * Advice Given: Google, Stackoverflow, Co-Pilot, Oracle, Professor, Parental Figures.
 * Desc: This is the Stock Analyst Interface class that contains the functionality of the
 * interface.
 */
import java.util.*;



public interface IStockAnalyst {

    String WEB_URL = "https://stockanalysis.com/list/";

    /**
     * Method to get the text of a given web URL
     * @param url the web URL address
     * @return the web URL page text
     */
    String getUrlText(final String url);

    /**
     * Method to get the categories of stock lists
     * @param urlText the text of the page that has the stock list and their categories
     * @return list of stock lists categories
     */
    List<String> getStocksListCategories(final String urlText);

    /**
     * Method to get the list of stocks within a stock list category
     * @param urlText the text of the page that has the stock list and their categories
     * @param stockCategoryName the stock list category name
     * @return map that contains stock lists and their URLs. Key is stock list name, and value is the URL
     * Example of a map entry <â€œMega-Capâ€�, https://stockanalysis.com/list/mega-cap-stocks/>
     */
    Map<String, String> getStocksListsInListCategory(final String urlText, final String stockCategoryName);

    // Ignore change percentages that are not numbers (e.g. "-")

    /**
     * Method to get top companies by change rate
     * @param urlText the text of the page that has the stock list
     * @param topCount how many companies to return
     * @return map that has the top companies and their change rate. Key is the change rate and value is the company name
     */
    //Changed From TreeMap to ArrayList
    ArrayList<ExchangeRate> getTopCompaniesByChangeRate(final String urlText, int topCount);
}



