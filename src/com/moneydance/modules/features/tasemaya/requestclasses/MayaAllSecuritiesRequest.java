package com.moneydance.modules.features.tasemaya.requestclasses;

import com.moneydance.modules.features.tasemaya.utils.Utils.Language;

public class MayaAllSecuritiesRequest extends MayaSecurityBaseRequest {

    private static final String END_POINT = "content/searchentities";
    private static final String METHOD = "GET";

    public String getEndPoint() {
        return END_POINT;
    }

    public String getMethod() {
        return METHOD;
    }

    public MayaAllSecuritiesRequest(Language lang) throws Exception {
        super();
        headers.put("Content-Type", "application/json");
        params.put("lang", Integer.toString(lang.getValue()));

    }
}
