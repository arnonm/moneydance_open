package com.moneydance.modules.features.tasemaya;

public class BadExceptionResponse extends Exception{
    
    public BadExceptionResponse(int statusCode, String reason, String url, String method) {
        String message = "Bad response to URL: " + url + " in method " + method + 
        " [status code: "+statusCode +", reason "+ reason +" ]";
        new BadExceptionResponse(0, message, "", "");
    }
}
