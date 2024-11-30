package com.moneydance.modules.features.tasemaya;

import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.moneydance.modules.features.tasemaya.adapters.FundsLocalDateTimeAdapter;
import com.moneydance.modules.features.tasemaya.jsondata.FundHistory;
import com.moneydance.modules.features.tasemaya.jsondata.FundListing;
import com.moneydance.modules.features.tasemaya.requestclasses.MayaFundDetailsRequest;
import com.moneydance.modules.features.tasemaya.requestclasses.MayaFundHistoricalRequest;
import com.moneydance.modules.features.tasemaya.utils.GSONUtil;
import com.moneydance.modules.features.tasemaya.utils.Utils.Language;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
// import java.time.format.DateTimeFormatter;


public class MayaFunds extends MayaBase{
    
    public static final  int TYPE=4;
    private int period = 0;
    // private Type SecurityListingType = new TypeToken <SecurityListing>() {}.getType();
    private Type FundListingType = new TypeToken <FundListing>() {}.getType();
    

    public MayaFunds(Logger logger, int num_of_attempts, boolean verify) {
        super(logger, num_of_attempts, verify);
    }


    public  Map<String, String> getNames(
        FundListing englishDetails, FundListing hebrewDetails) 
    {
        Map<String, String>names = new HashMap<>();
        this.logger.info("MayaFunds::getNames");
        names.put("english_short_name", (String) englishDetails.FundLongName);
        names.put("english_long_name", (String) englishDetails.FundLongName);
        names.put("hebrew_short_name", (String) hebrewDetails.FundShortName);
        names.put("hebrew_long_name", (String) hebrewDetails.FundShortName);
        return names;
    }

    
    public FundListing getDetails(String securityId, Language lang) throws Exception{
        this.logger.info("MayaFunds::getDetails - before request");
        String responseString =  sendRequest(new MayaFundDetailsRequest(securityId, lang));
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new FundsLocalDateTimeAdapter())
            .create();

        FundListing listing  = gson.fromJson(responseString, FundListing.class);
        this.logger.info("MayaFunds::getDetails - after request");
        return listing;
    }


    public FundHistory getPriceHistoryChunk(
        String securityId, LocalDate fromDate, LocalDate toDate, int page, Language lang) throws Exception {
            int _page = (page == 0) ? 1 : page ;
            String responseString = sendRequest(new MayaFundHistoricalRequest(securityId, fromDate, toDate, _page, period, lang));
            Gson gson = GSONUtil.createGson();
            FundHistory historyListing = gson.fromJson(responseString, FundHistory.class);
            //  return sendRequest(new MayaFundHistoricalRequest(securityId, fromDate, toDate, _page, period, lang));
            return historyListing;
            
    }

    //public Stream<Map<String, Object>> getPriceHistory (
    public FundHistory getPriceHistory (
        String securityId, LocalDate fromDate, LocalDate toDate, int page, Language lang) throws Exception 
    {
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        if (fromDate == null) {
            fromDate = toDate.minusDays(1);
        }
        // Map<String, Object> data = getPriceHistoryChunk(securityId, fromDate, toDate, _page, lang);
        return getPriceHistoryChunk(securityId, fromDate, toDate, page, lang);
        // Map<String, Object> items = (Map<String, Object>) data.getOrDefault("Items", new ArrayList<>());
        // return items.stream();
        // return data;
    }

}



/*
 import java.util.*;
import java.time.LocalDate;
import java.util.stream.Stream;

import pymaya.maya_base.MayaBase;
import pymaya.request_classes.MayaFundDetailsRequest;
import pymaya.request_classes.MayaHistoricalRequest;
import pymaya.utils.Language;

public class MayaFunds extends MayaBase {
    public static final int TYPE = 4;

    public static Map<String, String> getNames(Map<String, Object> englishDetails, Map<String, Object> hebrewDetails) {
        Map<String, String> names = new HashMap<>();
        names.put("english_short_name", (String) englishDetails.getOrDefault("FundLongName", ""));
        names.put("english_long_name", (String) englishDetails.getOrDefault("FundLongName", ""));
        names.put("hebrew_short_name", (String) hebrewDetails.getOrDefault("FundShortName", ""));
        names.put("hebrew_long_name", (String) hebrewDetails.getOrDefault("FundLongName", ""));
        return names;
    }

    public Map<String, Object> getDetails(String securityId, Language lang) {
        return this.sendRequest(new MayaFundDetailsRequest(securityId, lang));
    }

    public Map<String, Object> getPriceHistoryChunk(String securityId, LocalDate fromDate, LocalDate toDate, int page, Language lang) {
        return this.sendRequest(new MayaHistoricalRequest(securityId, fromDate, toDate, page, lang));
    }

    public Stream<Map<String, Object>> getPriceHistory(String securityId, LocalDate fromDate, LocalDate toDate, int page, Language lang) {
        Map<String, Object> data = getPriceHistoryChunk(securityId, fromDate, toDate, page, lang);
        List<Map<String, Object>> table = (List<Map<String, Object>>) data.getOrDefault("Table", new ArrayList<>());
        return table.stream();
    }

    public Stream<Map<String, Object>> getPriceHistory(String securityId, LocalDate fromDate) {
        return getPriceHistory(securityId, fromDate, LocalDate.now(), 1, Language.ENGLISH);
    }

    public Stream<Map<String, Object>> getPriceHistory(String securityId, LocalDate fromDate, LocalDate toDate) {
        return getPriceHistory(securityId, fromDate, toDate, 1, Language.ENGLISH);
    }

    public Stream<Map<String, Object>> getPriceHistory(String securityId, LocalDate fromDate, LocalDate toDate, int page) {
        return getPriceHistory(securityId, fromDate, toDate, page, Language.ENGLISH);
    }
}


*/