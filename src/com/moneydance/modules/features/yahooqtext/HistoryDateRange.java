/*************************************************************************\
* Copyright (C) 2010 The Infinite Kind, LLC
*
* This code is released as open source under the Apache 2.0 License:<br/>
* <a href="http://www.apache.org/licenses/LICENSE-2.0">
* http://www.apache.org/licenses/LICENSE-2.0</a><br />
\*************************************************************************/

package com.moneydance.modules.features.yahooqtext;

import com.infinitekind.moneydance.model.*;
import com.infinitekind.util.*;

/**
 * Date range specific to a security to fill in the needed number of days of history.
 *
 * @author Kevin Menningen - Mennē Software Solutions, LLC
 */
public class HistoryDateRange {
  private static final int NEW_DOWNLOAD_DAYS = 365;
  private static final int MINIMUM_DAYS = 5;

  public static DateRange getRangeForSecurity(CurrencyType secCurrency, int numDays) {
    int lastDate = 0;
    for (CurrencySnapshot snap : secCurrency.getSnapshots()) {
      lastDate = Math.max(lastDate, snap.getDateInt());
    }
    // determine how many days of history to download
    int days;
    final int today = DateUtil.getStrippedDateInt();
    if (lastDate == 0) {
      days = Math.max(numDays, NEW_DOWNLOAD_DAYS); // no history exists for that security/currency
    } else {
      days = DateUtil.calculateDaysBetween(lastDate, today);
      days = Math.max(days, numDays);
      days = Math.max(days, MINIMUM_DAYS);
    }

    return new DateRange(DateUtil.incrementDate(today, 0, 0, -(days + 1)), today);
  }
}
