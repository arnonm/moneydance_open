package com.moneydance.modules.features.tasemaya.requestclasses;

import com.moneydance.modules.features.tasemaya.utils.Utils.Language;

public class MayaSecurityDataRequest  extends MayaSecurityBaseRequest{
    
    private static final String END_POINT = "company/securitydata";
    private static final String METHOD = "GET";

    public MayaSecurityDataRequest(String securityId, Language lang) throws Exception {
        super();
        // Map<String, String> params = new HashMap<>();
        this.params.put("securityId", securityId);
        this.params.put("lang", Integer.toString(lang.getValue()));
        // headers.put("referer", "https://www.tase.co.il/");
        // headers.put("Content-Type", "application/json");
        // headers.put("Host", "api.tase.co.il");
        this.headers.remove("Acccept");
        this.headers.remove("X-Maya-With");

    }

    @Override
    public String getEndPoint() {
        return END_POINT;
    }

    @Override
    public String getMethod() {
        return METHOD;
    }

    
}
