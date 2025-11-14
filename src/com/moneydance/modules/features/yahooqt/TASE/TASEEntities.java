package com.moneydance.modules.features.yahooqt.TASE;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.List;
import java.util.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.moneydance.modules.features.yahooqt.TASE.jsondata.IndiceListing;
import com.moneydance.modules.features.yahooqt.TASE.utils.TASEHelper.Language;
import com.moneydance.modules.features.yahooqt.TASE.utils.WebAccess;

public class TASEEntities {
  
  private final String URL = "api.tase.co.il"; //$NON-NLS-1$
  private final String PATH = "/api/content/searchentities"; //$NON-NLS-1$
  
  
  /*
   * Return a List of all the Indices/Entities available on TLV
   */
  public Optional<List<IndiceListing>> getAllListings(Language lang) throws IOException
  {
    // PortfolioLog.info("Getting all Listings from TLV");
    return responsetoEntitiesList(rpcAllIndices(lang));
  }
  
  /*
    * Internal implementation - visible for testing to allow mocking
    */
  public String rpcAllIndices(Language lang) throws IOException
  {
    final int RETRY_TIMES = 5;
    int times_tried = 0;
    String response = null;
    
    while (times_tried < RETRY_TIMES) {
      try {
        response = new WebAccess (URL, PATH)
        .addUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; FSL 7.0.6.01001") //$NON-NLS-1$
          .addParameter("lang", String.valueOf(lang.getValue())) //$NON-NLS-1$
          .addHeader("Accept", "*/*") //$NON-NLS-1$ //$NON-NLS-2$
          .addHeader("referer", "https://www.tase.co.il/") //$NON-NLS-1$ //$NON-NLS-2$
          .addHeader("Cache-Control", "no-cache") //$NON-NLS-1$ //$NON-NLS-2$
          .addHeader("Content-Type", "application/json") //$NON-NLS-1$ //$NON-NLS-2$
          .get();
        return response;
        
      } catch (ConnectException e) {
        times_tried++;
      } catch (IOException e) {
        times_tried++;
      }
    }
    return response;
  }
  
  /*
    * Convert JSON response to List
    */
  private Optional<List<IndiceListing>> responsetoEntitiesList(String response)
  {
    Gson gson = new Gson();
    
    Type IndiceListingType = new TypeToken<List<IndiceListing>>()
    {
    }.getType();
    
    
    try {
      List<IndiceListing> list = gson . fromJson (response, IndiceListingType);
      
      
      return Optional.of(list);
    } catch (Exception e) {
      // PortfolioLog.error(e);
      return Optional.empty();
    }
  }
}