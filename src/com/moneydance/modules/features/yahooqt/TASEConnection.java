package com.moneydance.modules.features.yahooqt;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

//import com.infinitekind.util.DateUtil.incrementDate;
//import com.infinitekind.util.DateUtil.getStrippedDate;
import com.infinitekind.util.AppDebug;
import com.infinitekind.moneydance.model.CurrencyType;
import com.infinitekind.util.DateUtil;
import com.infinitekind.util.StringUtils.*;
//import com.infinitekind.util.StringUtils.fieldIndex;
//import com.infinitekind.util.StringUtils.isEmpty;
//import com.infinitekind.util.StringUtils.parseRate;
//import com.moneydance.modules.features.yahooqt.SQUtil.isBlank;
import org.jetbrains.annotations.Nullable;
// import org.python.tests.multihidden.BaseConnection;
import org.jetbrains.annotations.NotNull;
import com.moneydance.modules.features.yahooqt.BaseConnection;
import com.moneydance.modules.features.yahooqt.TASE.TASESecurity;
import com.moneydance.modules.features.yahooqt.TASE.LatestSecurityPrice;
import com.moneydance.modules.features.yahooqt.TASE.TASEFund;
import com.moneydance.modules.features.yahooqt.TASE.Security;
import com.moneydance.modules.features.yahooqt.TASE.TASEEntities;
import com.moneydance.modules.features.yahooqt.TASE.jsondata.IndiceListing;
import com.moneydance.modules.features.yahooqt.TASE.utils.TASEHelper.TaseSecuritySubType;
import com.moneydance.modules.features.yahooqt.TASE.utils.TASEHelper.TaseSecurityType;
import com.moneydance.modules.features.yahooqt.TASE.utils.TASEHelper.TaseType;
import com.moneydance.modules.features.yahooqt.TASE.utils.TASEHelper.Language;
import java.lang.Exception;

public final class TASEConnection  extends  BaseConnection
{

  private  static final String TASESecURL = "https://mayaapi.tase.co.il/api/fund/history"; //$NON-NLS-1$
  private  static final String TASEFundURL = "/api/fund/details"; //$NON-NLS-1$
  private  static final String CURRENCY_CODE = "ILS"; //$NON-NLS-1$
  private  static final String TASE_REFERRER = "https://www.tase.co.il/";
  private  static final int HISTORY_SUPPORT =1;
  private  static final String TASE_ID="tase";

  private TASESecurity TASESecurities = null;
  private TASEFund TASEFunds = null;
  private Boolean ismapped=false;
  private List<IndiceListing> mappedEntities  = null;

  public TASEConnection(String connectionID, StockQuotesModel model) {
    super(connectionID, model, HISTORY_SUPPORT);
    // super();
    // model = new StockQuotesModel();
    this.TASESecurities = new TASESecurity();
    this.TASEFunds = new TASEFund();
    this.ismapped = false;

  }

  public TASEConnection(StockQuotesModel model) {
    super (TASE_ID, model, HISTORY_SUPPORT);
    this.TASESecurities = new TASESecurity();
    this.TASEFunds = new TASEFund();
    this.ismapped = false;
  }

  @Nullable
  public String getFullTickerSymbol(SymbolData parsedSymbol, StockExchange exchange) {
    if ((parsedSymbol == null) || isBlank(parsedSymbol.getSymbol())){
      return null;
    }

    // check if the exchange was already added on, which will override the selected exchange
    if (!isBlank(parsedSymbol.getSuffix())) {
      return parsedSymbol.getSymbol() + parsedSymbol.getSuffix();
    }
    // Check if the selected exchange has a Tase suffix or not. If it does, add it.
    String suffix = exchange.getSymbolTASE();
    if (suffix == null) {
      return null;
    }
    if (suffix == null || isBlank(suffix)){
      return parsedSymbol.getSymbol();
    }
    return parsedSymbol.getSymbol() + suffix;
  }

  @Nullable
  public String getCurrencyCodeForQuote(String rawTickerSymbol , StockExchange exchange) {
    if (isBlank(rawTickerSymbol)){
      return null;
    }

    // check if this symbol overrides the exchange and the currency code
    int periodIdx = rawTickerSymbol.lastIndexOf('.');
    if (periodIdx > 0) {
      String marketID = rawTickerSymbol.substring(periodIdx + 1);

      // Check if the dash exists
      if (marketID.contains("-")) {
        // the currency ID was encoded along with the market ID
        String[] parts = marketID.split("-"); // This creates ["US", "USD"]
        if (parts.length > 1) {
          return parts[1];
        }
      }
    }
    if (exchange != null && exchange.getCurrencyCode() != null) {
        return exchange.getCurrencyCode();
    } else {
      return null;
    }
  }

  public void updateExchangeRate(DownloadInfo downloadInfo) {
    downloadInfo.recordError("Implementation error: TASE does not offer exchange rates");
  }

  // @Override
  // public Boolean updateSecurities(List<DownloadInfo> securitiesToUpdate){
  //   // TODO: if there's any initialisation step, that goes here before updateSecurity()
  //   //  is invoked for each individual security
  //   //getTaseEntities
  //   return super.updateSecurities((List<? extends DownloadInfo>) securitiesToUpdate);
  // }

  /**
   * Retrieve the current exchange rate for the given currency and base
   * @param downloadInfo   The wrapper for the currency to be downloaded and the download results
   */
  public void  updateSecurity(DownloadInfo downloadInfo) {
    AppDebug.ALL.log("tase: updating security: " + downloadInfo.fullTickerSymbol);
    if ((downloadInfo.fullTickerSymbol == null) || (downloadInfo.fullTickerSymbol.length() == 0))
      return;

    if (!this.ismapped)
    {
      this.mapEntities();
    }

    TaseType type = getSecurityType(downloadInfo.fullTickerSymbol);
    // String security = downloadInfo.fullTickerSymbol;
    Security security = new Security(downloadInfo.fullTickerSymbol, "EUR");
    security.setTickerSymbol(downloadInfo.fullTickerSymbol);


    Optional<LatestSecurityPrice> priceOpt = Optional.empty();
    CurrencyType relativeCurrency = downloadInfo.getSecurity().getBook().getCurrencies().getCurrencyByIDString(CURRENCY_CODE);
    try
    {
      if (type == TaseType.FUND)
      {
        priceOpt = this.TASEFunds.getLatestQuote(security);
      }
      if (type == TaseType.SECURITY)
        priceOpt = this.TASESecurities.getLatestQuote(security);

      if (priceOpt.isPresent()) {
        LatestSecurityPrice price = priceOpt.get();
        if (relativeCurrency != null) {
          downloadInfo.relativeCurrency = relativeCurrency;
        }
        downloadInfo.setRate(price.getValue(), System.currentTimeMillis());

      }
      return;
    }
    catch (Exception e)
    {
      AppDebug.ALL.log("tase: ERROR updating security: " + e+ downloadInfo.fullTickerSymbol);
      return;
    }
  }

  /*
   * Returns cached index of all Tel-Aviv Entities (stock, bonds, indexes,
   * companies) Use this list to look up Entity type
   */
  private List<IndiceListing> getTaseEntities()
  {
    if (!this.ismapped)
    {
      this.mapEntities();
    }

    if (this.mappedEntities == null)
      return Collections.emptyList();
    else
      return this.mappedEntities;
  }

  /**
   * Find Security Type of a SecurityId
   *
   * @param securityId
   * @return TaseType
   */
  private TaseType getSecurityType(String securityId)
  {
    if (!this.ismapped || this.mappedEntities == null)
      return TaseType.NONE;

    IndiceListing foundIndice = this.mappedEntities.stream().filter(p -> p.getId().equals(securityId)).findFirst()
                                                   .orElse(null);
    if (foundIndice != null)
      return foundIndice.getTaseType();

    return TaseType.NONE;
  }

  private Boolean isEmpty(String candidate) {
    if (candidate == null || candidate.length() == 0) {
      return true;
    } else {
      return false;
    }
  }

  private Boolean isBlank(String candidate) {
    if (candidate == null) {
      return true;
    }
    Boolean isBlank = isEmpty(candidate);
    if (!isBlank) {
      for (int index = candidate.length() - 1; index >= 0; index--) {
        isBlank = Character.isWhitespace(candidate.charAt(index));
        if (!isBlank) {
          break; // non-whitespace character found, don't bother checking the remainder
        }
      }
    }
    return isBlank;
  }

  /*
   * Gets all Entities from the TASE API, then filters the Mutual Funds and
   * Securities. Not interested in disclosures, indices and companies
   */
  private void mapEntities()
  {
    try
    {
      TASEEntities entities = new TASEEntities();

      Optional<List<IndiceListing>> mappedEntitiesOptional = entities.getAllListings(Language.ENGLISH);

      if (mappedEntitiesOptional.isEmpty())
      {
        //PortfolioLog.error("Could not get Tel Aviv Stock Exchange Entities"); //$NON-NLS-1$
        AppDebug.ALL.log("Could not get Tel Aviv Stock Exchange Entities");
        this.ismapped = false;
      }
      else
      {
        this.ismapped = true;
        this.mappedEntities = mappedEntitiesOptional.get();

        Iterator<IndiceListing> entitiesIterator = this.mappedEntities.iterator();

        while (entitiesIterator.hasNext())
        {
          IndiceListing listing = entitiesIterator.next();

          int type = listing.getType();
          String subtype = listing.getSubType();
          listing.setTaseType(TaseType.NONE);

          if (type == TaseSecurityType.MUTUAL_FUND.getValue() && subtype == null) // $NON-NLS-1$
          {
            listing.setTaseType(TaseType.FUND);
          }
          if (type == TaseSecurityType.SECURITY.getValue()
              && subtype != TaseSecuritySubType.WARRENTS.toString())
          {
            listing.setTaseType(TaseType.SECURITY);
          }
        }
      }
    }
    catch (Exception e)
    {
      //PortfolioLog.error("Could not get Tel Aviv Stock Exchange Entities"); //$NON-NLS-1$
      AppDebug.ALL.log("Could not get Tel Aviv Stock Exchange Entities");
      this.ismapped = false;
    }
  }
  
}