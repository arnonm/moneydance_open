package com.moneydance.modules.features.tasemaya.utils;

import java.util.Map;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params) 
      throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
          // if (result.length() != 0) result.append("&");
          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          result.append("=");
          result.append(URLEncoder.encode((entry.getValue()), "UTF-8"));
          result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }

    public static String postJsonParamsString(Map<String, String> params) throws UnsupportedEncodingException {
      StringBuilder result = new StringBuilder();

        result.append("{");
        for (Map.Entry<String, String> entry : params.entrySet()) {
          result.append('"');
          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          result.append('"');
          result.append(": ");
          result.append('"');
          result.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
          result.append('"');
          result.append(",");
        }
        result.append("}");

        String resultString = result.toString();

        return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }
}

