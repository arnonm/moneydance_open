package com.moneydance.modules.features.tasemaya.requestclasses;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.moneydance.modules.features.tasemaya.utils.ParameterStringBuilder;

public abstract class MayaBaseRequest {
    private final static String REFERER_URL = "https://www.tase.co.il/";
    private final static String HOST = "api.tase.co.il";
    private final static String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; FSL 7.0.6.01001)";

    protected Logger logger;
    protected Map<String, String> headers = new HashMap<>();
    protected Map<String, String> params = new HashMap<>();
    protected String URLParameters;
    protected String POSTParameters;
    public HttpURLConnection conn;

    public MayaBaseRequest(Object... args) throws IOException {

        this.logger = (logger == null) ? Logger.getLogger(this.getClass().getName()) : logger;
        this.URLParameters = "";
        this.headers.put("Accept", "*/*"); // Not for fund
        this.headers.put("Cache-Control", "no-cache");
        this.headers.put("referer", REFERER_URL);
        this.headers.put("User-Agent", USER_AGENT);
        this.headers.put("Host", HOST);

    }

    protected abstract String getMayaApiBaseUrl();

    protected abstract String getEndPoint();

    public abstract String getMethod();

    public String getURLParameters() {
        return this.URLParameters;
    };

    public void disconnect() {
        this.conn.disconnect();
    }

    public String getUrl() {
        return getMayaApiBaseUrl() + getEndPoint() + getURLParameters();
    }

    public HttpURLConnection prepare() throws Exception {
        // System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        setExtendedURL();

        if (getMethod() == "POST") {
            this.POSTParameters = ParameterStringBuilder.getParamsString(this.params);
            this.headers.put("Content-Length", String.valueOf(this.POSTParameters.length()));
        }

        if (getMethod() == "POST" && getEndPoint() == "security/historyeod") {
            this.POSTParameters = ParameterStringBuilder.postJsonParamsString(this.params);
            this.headers.put("Content-Length", String.valueOf(this.POSTParameters.length()));
        }
        URL url = new URL(this.getUrl());
        logger.info("MayaBaseRequest::prepare - URL " + this.getUrl());

        this.conn = (HttpURLConnection) url.openConnection();
        setHeaders();
        this.conn.setRequestMethod(getMethod());

        setPOSTParameters();
        printRequestDetails();
        return this.conn;

    }

    private void setExtendedURL() throws Exception {
        if (this.getMethod() == "GET") {
            String tempParams = ParameterStringBuilder.getParamsString(this.params);
            if (tempParams.length() > 0) {
                this.URLParameters = "?" + tempParams;
            }
        }
    }

    public void setPOSTParameters() throws IOException {
        if (this.getMethod() == "POST") {
            logger.info("POST setParameters");
            this.conn.setDoOutput(true);
            try {
                DataOutputStream out = new DataOutputStream(this.conn.getOutputStream());
                logger.info("Post parameters " + this.POSTParameters);
                out.write(this.POSTParameters.getBytes("UTF-8"));
                out.flush();
                out.close();

            } catch (IOException e) {
                logger.warning(e.getMessage());
                throw e;
            }
        }
    }

    public void setHeaders() {
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            logger.info("MayaBaseRequest::setHeaders - set Request " +
                entry.getKey() + ": " + entry.getValue());
            this.conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        logger.info("MayaBaseRequest::setHeaders - " +
           this.conn.getRequestProperties());
    }

    private void printRequestDetails() {
        logger.info("MayaBaseRequest::printRequestDetails - URL " +
        this.getUrl());
        logger.info("MayaBaseRequest::printRequestDetails - URL " +
        this.conn.getURL());
        logger.info("MayaBaseRequest::printRequestDetails - RequestMethod " +
        this.conn.getRequestMethod());

        logger.info("MayaBaseRequest::printRequestDetails - Headers:");
        try {
            logger.info(this.conn.getRequestProperties().toString());
        } catch (Exception e) {
        }
        logger.info("MayaBaseRequest::printRequestDetails - ContentType - " +
            this.conn.getContentType());
    }

}
