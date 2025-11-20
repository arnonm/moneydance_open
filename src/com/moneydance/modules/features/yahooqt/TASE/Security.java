package com.moneydance.modules.features.yahooqt.TASE;


import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// import com.google.common.base.Strings;

public final class Security  {
  
  public static final class ByName implements Comparator<Security>, Serializable {
    
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Security s1, Security s2) {
       if (s1 == null && s2 == null)
                return 0;
            else if (s1 == null)
                return -1;
            else if (s2 == null)
                return 1;
       return s1.getName().compareTo(s2.getName());
    }
  }

    public String uuid;
    public String onlineId;
    public String currentcyCode = "EUR";
    public String name;

    private String isin;
    private String tickerSymbol;
    private String wkn;
    private List<SecurityPrice> prices = new ArrayList<>();
    private LatestSecurityPrice latest; 
    private String symbol;
    private String currencyCode; 

   public Security(String name, String currencyCode)
    {
        this.name = name;
    }

     public Security(String name, String isin, String tickerSymbol)
    {
        this.name = name;
        this.isin = isin;
        this.tickerSymbol = tickerSymbol;
    }

    /* package */ Security(String uuid) {
        this.uuid = uuid;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
        // this.updatedAt = Instant.now();
    }
   
   public String getIsin()
    {
        return isin;
    }

    public void setIsin(String isin)
    {
        this.isin = isin;
        // this.updatedAt = Instant.now();
    }

    public String getTickerSymbol()
    {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol)
    {
        this.tickerSymbol = tickerSymbol;
        // this.updatedAt = Instant.now();
    }
    public String getWkn()
    {
        return wkn;
    }

    public void setWkn(String wkn)
    {
        this.wkn = wkn;
        // this.updatedAt = Instant.now();
    }

/**
     * Returns a list of historical security prices that includes the latest
     * security price if no history price exists for that date
     */
    public List<SecurityPrice> getPricesIncludingLatest()
    {
        List<SecurityPrice> copy = new ArrayList<>(prices);

        if (latest == null)
            return copy;

        int index = Collections.binarySearch(copy, new SecurityPrice(latest.getDate(), latest.getValue()));

        if (index >= 0) // historic quote exists -> use it
            return copy;

        copy.add(~index, latest);
        return copy;
    }
    
    public List<SecurityPrice> getPrices()
    {
        return Collections.unmodifiableList(prices);
    }

     /**
     * Returns a list of the last historical security prices with requested
     * number of prices (or less if there are not enough prices) from requested
     * Date.
     */
    public List<SecurityPrice> getLatestNPricesOfDate(LocalDate dateOfLastPrice, int numberOfPrices)
    {
        List<SecurityPrice> allPrices = getPricesIncludingLatest();

        int index = Collections.binarySearch(allPrices, new SecurityPrice(dateOfLastPrice, 0),
                        new SecurityPrice.ByDate());

        if (index < 0)
            index = -index - 2; // if price for requested date not found, use
                                // price before start date

        if (index >= allPrices.size())
            index = allPrices.size() - 1; // requested date greater than last
                                          // prize --> use last price

        int fromIndex = index - numberOfPrices + 1;
        if (fromIndex < 0)
            fromIndex = 0; // always start with first element if fromIndex is
                           // out of bounds

        return new ArrayList<>(allPrices.subList(fromIndex, index + 1));
    }

    /**
     * Adds security price to historical quotes.
     * 
     * @return true if the historical quote was updated.
     */
    public boolean addPrice(SecurityPrice price)
    {
        return addPrice(price, true);
    }

    /**
     * Adds security price to historical quotes.
     * 
     * @param overwriteExisting
     *            is used to decide on whether to keep or overwrite existing
     *            prices
     * @return true if the historical quote was updated.
     */
    public boolean addPrice(SecurityPrice price, boolean overwriteExisting)
    {
        Objects.requireNonNull(price);

        int index = Collections.binarySearch(prices, price);

        if (index < 0)
        {
            prices.add(~index, price);
            return true;
        }
        else
        {
            SecurityPrice replaced = prices.get(index);

            // different prices are replaced only, if the source is manual, csv
            // or html import, the value is 0.0
            if (!replaced.equals(price) && (overwriteExisting || replaced.getValue() == 0.0))
            {
                // only replace if necessary -> UI might keep reference!
                prices.set(index, price);
                return true;
            }
            else
            {
                return false;
            }
        }

        
    }


    public void removePrice(SecurityPrice price)
    {
        prices.remove(price);
    }

    public void removeAllPrices()
    {
        prices.clear();
    }

    public SecurityPrice getSecurityPrice(LocalDate requestedDate)
    {
        // assumption: prefer historic quote over latest if there are more
        // up-to-date historic quotes

        SecurityPrice lastHistoric = prices.isEmpty() ? null : prices.get(prices.size() - 1);

        // use latest quote only
        // * if one exists
        // * and if either no historic quotes exist
        // * or
        // ** if the requested time is after the latest quote
        // ** and the historic quotes are older than the latest quote

        if (latest != null //
                        && (lastHistoric == null //
                                        || (!requestedDate.isBefore(latest.getDate()) && //
                                                        !latest.getDate().isBefore(lastHistoric.getDate()) //
                                        )))
            return latest;

        if (lastHistoric == null)
            return new SecurityPrice(requestedDate, 0);

        // avoid binary search if last historic quote <= requested date
        if (!lastHistoric.getDate().isAfter(requestedDate))
            return lastHistoric;

        SecurityPrice p = new SecurityPrice(requestedDate, 0);
        int index = Collections.binarySearch(prices, p);

        if (index >= 0)
            return prices.get(index);
        else if (index == -1) // requested is date before first historic quote
            return prices.get(0);
        else
            return prices.get(-index - 2);
    }

}