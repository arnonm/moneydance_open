package com.moneydance.modules.features.tasemaya.requestclasses;

import java.time.LocalDate;

import com.moneydance.modules.features.tasemaya.utils.Utils.Language;

public class  MayaFundHistoricalRequest extends MayaFundBaseRequest {

    private static final String END_POINT = "fund/history";
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

    // public void MayaFundHistoricalRequest(String securityId) {
    //     LocalDate from_date = LocalDate.now();
    //     LocalDate to_date = LocalDate.now();
    //     MayaFundHistoricalRequest(securityId, from_date, to_date, 1, 0, Language.ENGLISH);
    // }

    public MayaFundHistoricalRequest(String securityId, LocalDate from_date, LocalDate to_date, 
            int page, int period, Language lang) throws Exception
    {
        super(lang);
        this.headers.remove("Accept"); // Not for fund
        this.headers.put("Content-Type", "application/x-www-form-urlencoded");
        this.headers.replace("Host", "mayaapi.tase.co.il");

        this.params.put("DateFrom", from_date.toString());
        this.params.put("DateTo", to_date.toString());
        this.params.put("FundId", securityId);
        this.params.put("Page", Integer.toString(page));
        this.params.put("Period", Integer.toString(period));
    }

}
