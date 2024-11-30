package com.moneydance.modules.features.tasemaya.requestclasses;


import com.moneydance.modules.features.tasemaya.utils.Utils.Language;

public class MayaFundDetailsRequest extends MayaFundBaseRequest {

    private static final String END_POINT = "fund/details";
    private static final String METHOD= "GET";

    
    @Override
    public String getEndPoint(){
        return END_POINT;
    }

    @Override
    public String getMethod() {
        return METHOD;
    }


    public MayaFundDetailsRequest(String securityId, Language lang) throws Exception {
        super(lang != null ? lang: Language.ENGLISH);
        // Map<String, String> params = new HashMap<>();
        this.headers.replace("Host", "mayaapi.tase.co.il");
        this.headers.remove("Content-Type");
        this.params.put("fundId", securityId);
    }

    public MayaFundDetailsRequest(String securityId) throws Exception {
        this(securityId, Language.ENGLISH);
    }

    
    
}

