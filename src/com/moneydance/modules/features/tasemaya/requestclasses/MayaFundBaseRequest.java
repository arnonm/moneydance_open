package com.moneydance.modules.features.tasemaya.requestclasses;

import com.moneydance.modules.features.tasemaya.utils.Utils.Language;

abstract class MayaFundBaseRequest extends MayaBaseRequest {

    protected static final String API_BASE_URL = "https://mayaapi.tase.co.il/api/";

    public abstract String getEndPoint();

    public abstract String getMethod();

    public String getMayaApiBaseUrl() {
        return API_BASE_URL;
    }

    public MayaFundBaseRequest(Language lang) throws Exception {
        super();
        // Map<String, String> headers = new HashMap<>();
        this.headers.put("X-Maya-With", "allow");
        this.headers.put("Accept-Language", lang == Language.ENGLISH ? "en-US" : "he-IL");
        // headers.put("Content-Type", "application/json");
        // headers.put("Host", "api.tase.co.il");

    }

}

/*
 * import java.util.Map;
 * import java.util.HashMap;
 * 
 * public abstract class MayaFundBaseRequest extends MayaBaseRequest {
 * protected static final String MAYA_API_BASE_URL =
 * "https://mayaapi.tase.co.il/api/";
 * 
 * public MayaFundBaseRequest(Language lang) {
 * super();
 * Map<String, String> headers = new HashMap<>();
 * headers.put("X-Maya-With", "allow");
 * headers.put("Accept-Language", lang == Language.ENGLISH ? "en-US" : "he-IL");
 * this.getRequest().setHeaders(headers);
 * }
 * 
 * public abstract String getEndPoint();
 * 
 * public abstract String getMethod();
 * }
 * 
 * 
 */