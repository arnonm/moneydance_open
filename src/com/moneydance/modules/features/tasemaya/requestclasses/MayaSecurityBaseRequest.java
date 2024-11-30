package com.moneydance.modules.features.tasemaya.requestclasses;


public abstract class MayaSecurityBaseRequest extends MayaBaseRequest{
    
    private static final String maya_api_base_url = "https://api.tase.co.il/api/";

    public abstract String getEndPoint();
    
    public abstract String getMethod();



    public String getMayaApiBaseUrl() {
        return maya_api_base_url;
    }

   public MayaSecurityBaseRequest() throws Exception {
    super();
    logger.info("MayaSecurityBaseRequest::contructor");
    this.headers.put("X-Maya-With", "allow");
   };
}