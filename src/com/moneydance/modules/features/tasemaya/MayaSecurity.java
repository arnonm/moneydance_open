package com.moneydance.modules.features.tasemaya;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.logging.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moneydance.modules.features.tasemaya.jsondata.SecurityHistory;
import com.moneydance.modules.features.tasemaya.jsondata.SecurityListing;
import com.moneydance.modules.features.tasemaya.requestclasses.MayaAllSecuritiesRequest;
import com.moneydance.modules.features.tasemaya.requestclasses.MayaSecurityDataRequest;
import com.moneydance.modules.features.tasemaya.requestclasses.MayaSecurityHistoricalRequest;
import com.moneydance.modules.features.tasemaya.utils.GSONUtil;
import com.moneydance.modules.features.tasemaya.utils.Utils.Language;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MayaSecurity extends MayaBase {

    private static final int TYPE = 8;
    private static final int RECORDS = 1;
    private Type SecurityListingType = new TypeToken <SecurityListing>() {}.getType();


    public MayaSecurity(Logger logger, int num_of_attempts, boolean verify) {
        super(logger, num_of_attempts, verify);
    }

    public List<SecurityListing> getAllSecurities(Language lang) throws Exception {
        this.logger.info("MayaSecurity::getAllSecurities - before SendRequest");
        String response =  sendRequest(new MayaAllSecuritiesRequest(lang));
        this.logger.info("MayaSecurity::getAllSecurities - after SendRequest");
        return this.ResponseToSecurityList(response);
    }

    public List<SecurityListing> ResponseToSecurityList(String response) {
        Gson gson = new Gson();
        Type SecurityListingType = new TypeToken <List<SecurityListing>>() {}.getType();
        List<SecurityListing> list = gson.fromJson(response, SecurityListingType);
        
        Iterator<SecurityListing> securityListingIterator = list.iterator();
        // while (securityListingIterator.hasNext()) {
        //     SecurityListing listing = securityListingIterator.next();
        //     this.logger.info(listing.toString());
        // }
        return list;
        
    }


    public  Map<String, String> getNames(
            SecurityListing englishDetails,SecurityListing hebrewDetails) {

        Map<String, String> names = new HashMap<>();
        names.getOrDefault(names, null);
        names.put("english_short_name", (String) englishDetails.Name);
        names.put("english_long_name",(String) englishDetails.getOrDefault("LongName", englishDetails.SecurityLongName));

        names.put("hebrew_short_name", (String) hebrewDetails.Name);
        names.put("hebrew_long_name",(String) hebrewDetails.getOrDefault("LongName", hebrewDetails.SecurityLongName));
        return names;
    }

    public SecurityListing getDetails(String securityId, Language lang) throws Exception {
        // return sendRequest(new MayaSecurityDataRequest(securityId, lang));
        String responseString =  sendRequest(new MayaSecurityDataRequest(securityId, lang));
        this.logger.info("MayaSecurity::getDetails - responseString "+ responseString);
        Gson gson = new Gson();
        SecurityListing securityListing  = gson.fromJson(responseString, this.SecurityListingType);
        // this.logger.info("MayaSecurity::getDetails - securityListing Id: "+ securityListing.Id + " Name: "+ securityListing.Name);

        return securityListing;
    }

    public SecurityHistory getPriceHistoryChunk(
            String securityId, LocalDate fromDate, LocalDate toDate, int page, Language lang) throws Exception {
        int _page = (page == 0) ? 1 : page ;
        this.logger.info("MayaSecurity::getPriceHistoryChunk");
        String responseString =  sendRequest(new MayaSecurityHistoricalRequest(securityId, fromDate, toDate, _page, TYPE, RECORDS, lang));
        Gson gson = GSONUtil.createGson();
        SecurityHistory historyListing = gson.fromJson(responseString, SecurityHistory.class);
        return historyListing;
    }

    public SecurityHistory getPriceHistory(
            String securityId, LocalDate fromDate, LocalDate toDate, int page, Language lang) throws Exception {
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        if (fromDate == null) {
            fromDate = toDate.minusDays(1);
        }
        return getPriceHistoryChunk(securityId, fromDate, toDate, page, lang);
    }

}
