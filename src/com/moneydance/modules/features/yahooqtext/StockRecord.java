/*************************************************************************\
* Copyright (C) 2010 The Infinite Kind, LLC
*
* This code is released as open source under the Apache 2.0 License:<br/>
* <a href="http://www.apache.org/licenses/LICENSE-2.0">
* http://www.apache.org/licenses/LICENSE-2.0</a><br />
\*************************************************************************/

package com.moneydance.modules.features.yahooqtext;

import com.infinitekind.moneydance.model.CurrencySnapshot;
import com.infinitekind.moneydance.model.CurrencyType;
import com.infinitekind.util.DateUtil;
import com.moneydance.apps.md.controller.Util;
import com.moneydance.modules.features.yahooqt.tdameritrade.Candle;

import java.util.Date;

/**
 * Stores a single entry for a historical price entry (snapshot) for a security.
 *
 * @author Kevin Menningen - Mennē Software Solutions, LLC
 */
public class StockRecord implements Comparable<StockRecord> {
  /** The integer date of the quote. */
  public int date = 0;
  /** The exact date of the quote, which can have the time of day set as well. */
  long dateTimeGMT = 0;
  /** Number of shares traded. */
  long volume = 0;
  /** The high price in terms of the price currency (gets converted to base currency). */
  double highRate = -1.0;
  /** The low price in terms of the price currency (gets converted to base currency). */
  double lowRate = -1.0;
  /** The open price in terms of the price currency. Currently not used. */
  double open = -1.0;
  /** The close price in terms of the price currency (gets converted to base currency). */
  double closeRate = -1.0;
  
  String priceDisplay = "";
  private double multiplier;
  
  public StockRecord()
  {
  	super();
  }
  
  public StockRecord(Candle candle, double multiplier)
  {
  	super();
  	this.multiplier = multiplier;
  	this.dateTimeGMT = candle.datetime;
  	Date dateObj = new Date(this.dateTimeGMT);

  	this.date = DateUtil.convertDateToInt(dateObj);
  	this.volume = candle.volume;
  	this.highRate = parseUserRate(candle.high);
  	this.lowRate = parseUserRate(candle.low);
  	this.open = parseUserRate(candle.open);
  	this.closeRate = parseUserRate(candle.close);
  }
  @Override
  public String toString() {
    return "close="+ closeRate +"; volume="+volume+"; high="+ highRate +"; low="+ lowRate +"; date="+date;
  }
  
  public int compareTo(StockRecord o) {
    // sort by date
    return date - o.date;
  }

  public void updatePriceDisplay(CurrencyType priceCurrency, char decimal) {
    long amount = (closeRate == 0.0) ? 0 : priceCurrency.getLongValue(1.0 / closeRate);
    priceDisplay = priceCurrency.formatFancy(amount, decimal);
  }
  
  CurrencySnapshot apply(CurrencyType security, CurrencyType priceCurrency) {
    // all snapshots are recorded in terms of the base currency.
    double newRate = priceCurrency.getUserRateByDateInt(date)*closeRate;
    CurrencySnapshot result = security.setSnapshotInt(date, newRate);
    // downloaded values are prices in a certain currency, change to rates for the stock history
    result.setUserDailyHigh(priceCurrency.getUserRateByDateInt(date)*highRate);
    result.setUserDailyLow(priceCurrency.getUserRateByDateInt(date)*lowRate);
    result.setDailyVolume(volume);
    result.syncItem();
    return result;
  }
	
	private double parseUserRate(double value)
	{
		double userPrice = value;
		if (userPrice == 0.0) return 0.0;
		userPrice *= multiplier;
		// the rate is the inverse of the price
		return 1.0 / Util.safeRate(userPrice);
	}
	
}
