package com.moneydance.modules.features.tasemaya;

import java.util.logging.*;

import com.moneydance.modules.features.tasemaya.requestclasses.MayaBaseRequest;

import java.net.HttpURLConnection;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// import org.apache.http.client.methods.CloseableHttpResponse;
// import org.apache.http.client.methods.HttpUriRequest;
// import org.apache.http.impl.client.CloseableHttpClient;
// import org.apache.http.impl.client.HttpClients;
// import org.apache.http.util.EntityUtils;
// import org.python.google.common.cache.Cache;
// import java.net.CacheResponse;

import java.util.concurrent.TimeUnit;

public class MayaBase {

    protected Logger logger;
    private int numOfAttempts;
    private int status = 0;
    private String response;

    public MayaBase(Logger logger, int numOfAttempts, boolean verify) {

        this.logger = (logger == null) ? Logger.getLogger(this.getClass().getName()) : logger;
        this.numOfAttempts = numOfAttempts;
    }


    public String sendRequest(MayaBaseRequest mayaApiRequest) throws RuntimeException {
        logger.info("MayaBase::sendRequest");
        try {
            this.response = getResponse(mayaApiRequest);
            if (this.status != 200) {

                logger.warning("Error in API call [" + this.status + "]");
                throw new IOException("status: " + status + " getURL: " + mayaApiRequest.getUrl() + " getMethod: "
                        + mayaApiRequest.getMethod());
            } else {
            logger.info("MayaBase::sendRequest - got response. Status: " + this.status);

            }
            mayaApiRequest.disconnect();
            return this.response;
            
        } catch (Exception e) {
            logger.warning("Error " + e.getMessage() + ", traceback: " + e.getStackTrace());
            throw new RuntimeException(e);
        }
    }


    private String getResponse(MayaBaseRequest mayaApiRequest) throws Exception {

        logger.info("MayaBase::getResponse -  Attempts " + numOfAttempts);
        for (int attempt = 0; attempt < numOfAttempts; attempt++) {
            logger.info("MayaBase::getResponse - Preparing request");
            HttpURLConnection conn = mayaApiRequest.prepare();
            logger.info("MayaBase::getResponse - Getting the response code");
            status = conn.getResponseCode();

            logger.info("MayaBase::getResponse - After the response code");
            response = getResponseDetails(conn);
            logger.info("MayaBase::getResponse - After getResponseDetails");

            if (status != 200) {
                logger.info("MayaBase::getResponse - response error attempt: " + (attempt + 1) + "/" + numOfAttempts);
                logger.info("MayaBase::getResponse - status response code " + status);
                logger.info("MayaBase::getResponse - status response " + response);
                logger.info("MayaBase::getResponse - try again in 1 sec...");
                TimeUnit.SECONDS.sleep(1);
            } else {
                logger.info("Disconnecting");
                conn.disconnect();
                return response;
            }
            logger.info("Disconnecting");
            conn.disconnect();
        }
        return response;
    }

    private String getResponseDetails(HttpURLConnection con) throws Exception {
        Reader streamReader = null;

        if (con.getResponseCode() > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }

        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);

        }
        in.close();
        return content.toString();
    }

    
}
/* 
 *import java.util.logging.Logger;
 * import java.util.concurrent.TimeUnit;
 * import java.util.Map;
 * import java.util.function.Function;
 * 
 * import org.apache.http.client.HttpClient;
 * import org.apache.http.impl.client.HttpClients;
 * import org.apache.http.client.methods.HttpUriRequest;
 * import org.apache.http.HttpResponse;
 * import org.apache.http.util.EntityUtils;
 * 
 * import com.google.common.cache.Cache;
 * import com.google.common.cache.CacheBuilder;
 * 
 * import com.fasterxml.jackson.databind.ObjectMapper;
 * 
 * public class MayaBase {
 * private Logger logger;
 * private int numOfAttempts;
 * private HttpClient httpClient;
 * private boolean verify;
 * private Cache<String, Map<String, Object>> cache;
 * 
 * public MayaBase(Logger logger, int numOfAttempts, HttpClient httpClient,
 * boolean verify, int cacheSize) {
 * this.logger = (logger == null) ? Logger.getLogger(this.getClass().getName())
 * : logger;
 * this.numOfAttempts = numOfAttempts;
 * this.httpClient = (httpClient == null) ? HttpClients.createDefault() :
 * httpClient;
 * this.verify = verify;
 * this.cache = CacheBuilder.newBuilder()
 * .maximumSize(cacheSize)
 * .build();
 * }
 * 
 * public HttpResponse getResponse(MayaBaseRequest mayaApiRequest) throws
 * Exception {
 * HttpResponse response = null;
 * 
 * for (int attempt = 0; attempt < numOfAttempts; attempt++) {
 * HttpUriRequest request = mayaApiRequest.prepare();
 * response = httpClient.execute(request);
 * 
 * if (response.getStatusLine().getStatusCode() != 200) {
 * logger.info("response error attempt: " + (attempt + 1) + "/" +
 * numOfAttempts);
 * logger.info("status response code " +
 * response.getStatusLine().getStatusCode() +
 * ", reason " + response.getStatusLine().getReasonPhrase());
 * logger.info("try again in 1 sec...");
 * TimeUnit.SECONDS.sleep(1);
 * } else {
 * return response;
 * }
 * }
 * return response;
 * }
 * 
 * public Map<String, Object> sendRequest(MayaBaseRequest mayaApiRequest) throws
 * Exception {
 * return cache.get("send:" + mayaApiRequest.hashCode(), new Function<String,
 * Map<String, Object>>() {
 * 
 * @Override
 * public Map<String, Object> apply(String key) {
 * try {
 * HttpResponse response = getResponse(mayaApiRequest);
 * 
 * if (response.getStatusLine().getStatusCode() != 200) {
 * logger.severe("Error in API call [" +
 * response.getStatusLine().getStatusCode() +
 * "] - " + response.getStatusLine().getReasonPhrase());
 * throw new BadResponseException(
 * response.getStatusLine().getStatusCode(),
 * response.getStatusLine().getReasonPhrase(),
 * mayaApiRequest.getUrl(),
 * mayaApiRequest.getMethod()
 * );
 * }
 * 
 * String jsonResponse = EntityUtils.toString(response.getEntity());
 * ObjectMapper mapper = new ObjectMapper();
 * return mapper.readValue(jsonResponse, Map.class);
 * 
 * } catch (Exception e) {
 * logger.severe("Error " + e.getMessage() + ", traceback: " +
 * e.getStackTrace());
 * throw new RuntimeException(e);
 * }
 * }
 * });
 * }
 * }
 * 
 * 
 */