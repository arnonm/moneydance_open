package com.moneydance.modules.features.tasemaya;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moneydance.modules.features.tasemaya.jsondata.FundHistory;
import com.moneydance.modules.features.tasemaya.jsondata.FundListing;
import com.moneydance.modules.features.tasemaya.jsondata.SecurityHistory;
import com.moneydance.modules.features.tasemaya.jsondata.SecurityListing;
import com.moneydance.modules.features.tasemaya.utils.GSONUtil;
import com.moneydance.modules.features.tasemaya.utils.Utils.Language;

public class Maya {

    private MayaSecurity mayaSecurities;
    private MayaFunds mayaFunds;
    private Map<String, Set<String>> mappedSecurities;
    private Type securityListingType;
    private Logger logger;



    public Maya(Logger logger, int number_of_attempts, boolean verify) {

        this.mayaSecurities = new MayaSecurity(logger, number_of_attempts, verify);
        this.mayaFunds = new MayaFunds(logger, number_of_attempts, verify);
        this.mappedSecurities = new HashMap<>();
        this.securityListingType = new TypeToken <List<SecurityListing>>() {}.getType();
        this.logger = (logger == null) ? Logger.getLogger(this.getClass().getName()) : logger;



        try {
            this.mapSecurities();
        } catch (Exception e) {
            logger.severe("");
        }
    }

    
    public Maya() {
        this(null, 1, true);
    }

    public List<SecurityListing> getAllSecurities(Language lang) throws Exception {
        logger.info("Maya::getAllSecurities");
        return mayaSecurities.getAllSecurities(lang);

    }

    private void mapSecurities() throws Exception {
        List<SecurityListing> allSecuritiesList = getAllSecurities(Language.ENGLISH);
        Iterator<SecurityListing> securityListingIterator = allSecuritiesList.iterator();

        while (securityListingIterator.hasNext()) {
            SecurityListing listing = securityListingIterator.next();
            String id = listing.Id;
            String type =listing.Type;
            mappedSecurities.computeIfAbsent(id, k-> new HashSet<>()).add(type);
        }
    }

    private Object getMayaClass(String securityId) {
        logger.info("Maya::getMayaClass - " + mappedSecurities.get(securityId));
        if (mappedSecurities.get(securityId).contains(Integer.toString(MayaFunds.TYPE))) {
            logger.info("Maya::getMayaClass - Type is MayaFunds");
            return mayaFunds;
        } else {
            logger.info("Maya::getMayaClass - Type is MayaSecurity");
            return mayaSecurities;
        }
    }

    /* was single functon called getDetails for both getDetails. Will refactor as one after finishing funds
    public String getDetails(String securityId, Language lang) throws Exception {
        Object mayaClass = getMayaClass(securityId);
        if (mayaClass instanceof MayaFunds) {
            return ((MayaFunds) mayaClass).getDetails(securityId, lang);
        } else {
            return ((MayaSecurity) mayaClass).getDetails(securityId, lang);
        }
    }
    */
    public Map<String, Object> getSecurityDetails(String securityId, Language lang) throws Exception {
        Object mayaClass = getMayaClass(securityId);
        SecurityListing listing =  ((MayaSecurity) mayaClass).getDetails(securityId, lang);
        return this.SecurityToMap(listing);
    }


    private Map<String, Object> SecurityToMap(SecurityListing listing) {
        Gson gson = new Gson();
        String json = gson.toJson(listing);
        logger.info("JSON "+json);
        Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
        return map;
    }

    private Map<String, Object> FundsToMap(FundListing listing) {
        Gson gson = GSONUtil.createGson();
        String json = gson.toJson(listing);
        logger.info("JSON "+json);
        Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
        return map;
    }

    private Map<String, Object> HistoryToMap(Object listing) {
        Gson gson = GSONUtil.createGson();
        String json = gson.toJson(listing);
        logger.info("JSON "+json);
        Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
        return map;

    }
    public Map<String, Object> getFundsDetails(String securityId, Language lang) throws Exception {
        Object mayaClass = getMayaClass(securityId);
        FundListing listing = ((MayaFunds) mayaClass).getDetails(securityId, lang);
        return this.FundsToMap(listing);
    }

    public Map<String, String> getNames(String securityId) throws Exception {
        logger.info("Maya::getNames");
        Object mayaClass = getMayaClass(securityId);
        if (mayaClass instanceof MayaFunds) {
            FundListing englishDetails = ((MayaFunds) mayaClass).getDetails(securityId, Language.ENGLISH);
            FundListing hebrewDetails = ((MayaFunds) mayaClass).getDetails(securityId, Language.HEBREW);
                return ((MayaFunds) mayaClass).getNames(englishDetails, hebrewDetails);
        } else {
            SecurityListing englishDetails = ((MayaSecurity) mayaClass).getDetails(securityId, Language.ENGLISH);
            SecurityListing hebrewDetails = ((MayaSecurity) mayaClass).getDetails(securityId, Language.HEBREW);
            return ((MayaSecurity) mayaClass).getNames(englishDetails, hebrewDetails);
        }
    }

    public Map<String, Object> getPriceHistoryChunk(String securityId, LocalDate fromDate, LocalDate toDate, int page, Language lang) throws Exception {
        logger.info("Maya::getPriceHistoryChunk");
        Object mayaClass = getMayaClass(securityId);
        if (mayaClass instanceof MayaFunds) {
            FundHistory fundHistory =  ((MayaFunds) mayaClass).getPriceHistoryChunk(securityId, fromDate, toDate, page, lang);
            return this.HistoryToMap(fundHistory);
        }  else {
            SecurityHistory securityHistory = ((MayaSecurity) mayaClass).getPriceHistoryChunk(securityId, fromDate, toDate, page, lang);
            return this.HistoryToMap(securityHistory);
        }
    }

    public Map<String, Object> getPriceHistory(String securityId, LocalDate fromDate,
            LocalDate toDate, int page, Language lang) throws Exception {
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        if (fromDate == null) {
            fromDate = toDate.minusDays(1);
        }
        Object mayaClass = getMayaClass(securityId);
        if (mayaClass instanceof MayaFunds) {
            FundHistory fundHistory = ((MayaFunds) mayaClass).getPriceHistory(securityId, fromDate, toDate, page, lang);
            return HistoryToMap(fundHistory);
        } else {
            SecurityHistory securityHistory = ((MayaSecurity) mayaClass).getPriceHistory(securityId, fromDate, toDate, page, lang);
            return HistoryToMap(securityHistory);
        }
    }

}

/*
 * import java.util.logging.Logger;
 * import java.util.Map;
 * import java.util.HashMap;
 * import java.util.Set;
 * import java.util.HashSet;
 * import java.time.LocalDate;
 * import java.util.List;
 * 
 * import org.apache.http.impl.client.CloseableHttpClient;
 * import org.apache.http.impl.client.HttpClients;
 * 
 * import com.example.pymaya.MayaFunds;
 * import com.example.pymaya.MayaSecurity;
 * import com.example.pymaya.utils.Language;
 * 
 * public class Maya {
 * private MayaSecurity mayaSecurities;
 * private MayaFunds mayaFunds;
 * private Map<String, Set<String>> mappedSecurities;
 * 
 * public Maya(Logger logger, int numOfAttempts, CloseableHttpClient session,
 * boolean verify, int cachesize) {
 * if (session == null) {
 * session = HttpClients.createDefault();
 * }
 * 
 * this.mayaSecurities = new MayaSecurity(logger, numOfAttempts, session,
 * verify, cachesize);
 * this.mayaFunds = new MayaFunds(logger, numOfAttempts, session, verify,
 * cachesize);
 * 
 * this.mappedSecurities = new HashMap<>();
 * mapSecurities();
 * }
 * 
 * public Maya() {
 * this(null, 1, null, true, 128);
 * }
 * 
 * public List<Map<String, Object>> getAllSecurities(Language lang) {
 * return mayaSecurities.getAllSecurities(lang);
 * }
 * 
 * private void mapSecurities() {
 * List<Map<String, Object>> allSecurities = getAllSecurities(Language.ENGLISH);
 * for (Map<String, Object> security : allSecurities) {
 * String id = (String) security.get("Id");
 * String type = (String) security.get("Type");
 * mappedSecurities.computeIfAbsent(id, k -> new HashSet<>()).add(type);
 * }
 * }
 * 
 * private Object getMayaClass(String securityId) {
 * if (mappedSecurities.get(securityId).contains(MayaFunds.TYPE)) {
 * return mayaFunds;
 * } else {
 * return mayaSecurities;
 * }
 * }
 * 
 * public Map<String, Object> getDetails(String securityId, Language lang) {
 * Object mayaClass = getMayaClass(securityId);
 * if (mayaClass instanceof MayaFunds) {
 * return ((MayaFunds) mayaClass).getDetails(securityId, lang);
 * } else {
 * return ((MayaSecurity) mayaClass).getDetails(securityId, lang);
 * }
 * }
 * 
 * public Map<String, String> getNames(String securityId) {
 * Object mayaClass = getMayaClass(securityId);
 * Map<String, Object> englishDetails = getDetails(securityId,
 * Language.ENGLISH);
 * Map<String, Object> hebrewDetails = getDetails(securityId, Language.HEBREW);
 * if (mayaClass instanceof MayaFunds) {
 * return ((MayaFunds) mayaClass).getNames(englishDetails, hebrewDetails);
 * } else {
 * return ((MayaSecurity) mayaClass).getNames(englishDetails, hebrewDetails);
 * }
 * }
 * 
 * public Map<String, Object> getPriceHistoryChunk(String securityId, LocalDate
 * fromDate, LocalDate toDate, int page, Language lang) {
 * Object mayaClass = getMayaClass(securityId);
 * if (mayaClass instanceof MayaFunds) {
 * return ((MayaFunds) mayaClass).getPriceHistoryChunk(securityId, fromDate,
 * toDate, page, lang);
 * } else {
 * return ((MayaSecurity) mayaClass).getPriceHistoryChunk(securityId, fromDate,
 * toDate, page, lang);
 * }
 * }
 * 
 * public List<Map<String, Object>> getPriceHistory(String securityId, LocalDate
 * fromDate, LocalDate toDate, int page, Language lang) {
 * if (toDate == null) {
 * toDate = LocalDate.now();
 * }
 * Object mayaClass = getMayaClass(securityId);
 * if (mayaClass instanceof MayaFunds) {
 * return ((MayaFunds) mayaClass).getPriceHistory(securityId, fromDate, toDate,
 * page, lang);
 * } else {
 * return ((MayaSecurity) mayaClass).getPriceHistory(securityId, fromDate,
 * toDate, page, lang);
 * }
 * }
 * }
 * 
 * 
 */