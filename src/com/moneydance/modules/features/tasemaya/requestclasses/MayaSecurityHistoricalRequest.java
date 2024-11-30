package com.moneydance.modules.features.tasemaya.requestclasses;

import java.time.LocalDate;

import com.moneydance.modules.features.tasemaya.utils.Utils.Language;

public class MayaSecurityHistoricalRequest extends MayaSecurityBaseRequest {
    private static final String END_POINT = "security/historyeod";
    private static final String METHOD = "POST";

    public String getEndPoint() {
        return END_POINT;
    }

    public String getMethod() {
        return METHOD;
    }

    public String getBaseURL() {
        return super.getMayaApiBaseUrl();
    }

    public MayaSecurityHistoricalRequest(String security_id, LocalDate from_date, LocalDate to_date, int page,
            int p_type,int total_rec, Language lang) throws Exception {

        super();
        this.headers.remove("Accept"); // Not for fund
        this.headers.put("Content-Type", "application/json");
        // this.headers.replace("Host", "mayaapi.tase.co.il");
        this.headers.remove("X-Maya-With");

        this.params.put("dFrom", from_date.toString());
        this.params.put("dTo", to_date.toString());
        this.params.put("oID", security_id);
        this.params.put("pageNum", Integer.toString(page));
        this.params.put("pType", Integer.toString(p_type));
        this.params.put("TotalRec", Integer.toString(total_rec));
        this.params.put("lang", Integer.toString(lang.getValue()));

    }
}
