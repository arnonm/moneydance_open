package com.moneydance.modules.features.tasemaya.requestclasses;

import com.moneydance.modules.features.tasemaya.utils.Utils.Language;

public class MayaSecurityDetailsRequest extends MayaSecurityBaseRequest {
    
    private static final String END_POINT = "security/majordata";
    private static final String METHOD = "GET";

    @Override
    public String getEndPoint() {
        return END_POINT;
    }

    @Override
    public String getMethod() {
        return METHOD;
    }

    
    public  MayaSecurityDetailsRequest(String securityId, Language lang) throws Exception {
        super();
        // Map<String, String> params = new HashMap<>();
        this.params.put("secId", securityId);
        this.params.put("compId", securityId);
        this.params.put("lang", lang.toString());

    }
}
