/*
 * By: Frank Vanris
 * Date: 2/3/2024
 * Total Hours: 36
 * Advice Given: Google, Stackoverflow, Co-Pilot, Oracle, Sara Farag, Parental Figures.
 * Desc: created an ExchangeRate class to compare exchangeRates with other exchangeRates.
 */
package As1;

import java.util.Comparator;

public class ExchangeRate {
	
	//class that compares an exchange rate with another exchange rate
	 public static class CompareExchangeRatesHighToLow implements Comparator<ExchangeRate> {
		 public int compare(ExchangeRate obj1, ExchangeRate obj2) {
			 if (obj1.mExchangeRate > obj2.mExchangeRate) {
				 return -1;
			 }
			 if (obj1.mExchangeRate == obj2.mExchangeRate) {
				 return 0;
			 }
			 return 1;
		 }
	 }
	
	//obtaining exchange rate and company name
	private double mExchangeRate;
	private String mCompanyName;
	
	//constructor
	public ExchangeRate(double exchangeRate, String companyName) {
		mExchangeRate = exchangeRate;
		mCompanyName = companyName;
	
	}
	
	
	//my getter functions
	public double getExchangeRate() {
		return mExchangeRate;
	}
	
	public String getCompanyName() {
		return mCompanyName;
	}
}
