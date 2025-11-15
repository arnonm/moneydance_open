package com.moneydance.modules.features.yahooqt.TASE.utils;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// --- APACHE HTTPCLIENT 4.X IMPORTS ---
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
// -------------------------------------

// NOTE: Assumes 'Pair<K, V>' and 'OnlineHelper' are defined elsewhere and accessible.

//@formatter:off
/**
 * This class is centralizing the download of web content and returns the response as string.
 * This is a FLUENT-API.
 * ... (Documentation omitted for brevity, but remains the same) ...
 */
//@formatter:on
public class WebAccess
{
    // Replaces the FunctionalInterface. Functional interfaces are compatible with Java 8.
    @FunctionalInterface
    private interface Request
    {
        // Must return HttpRequestBase or CloseableHttpResponse in 4.x
        HttpRequestBase create(URI uri) throws IOException;
    }

    public static class WebAccessException extends IOException
    {
        private static final long serialVersionUID = 1L;
        private final int httpErrorCode;
        private final List<Pair<String, String>> headers;
        private final String body;

        public WebAccessException(String message, int httpErrorCode, List<Pair<String, String>> headers, String body)
        {
            super(message);
            this.httpErrorCode = httpErrorCode;
            this.headers = headers;
            this.body = body;
        }

        public int getHttpErrorCode()
        {
            return httpErrorCode;
        }

        public List<String> getHeader(String key)
        {
            // Replaces List.toList() with Collectors.toList()
            return headers.stream().filter(p -> p.getKey().equalsIgnoreCase(key)).map(Pair::getValue).collect(Collectors.toList());
        }

        public String getBody()
        {
            return body;
        }
    }

    // CustomResponseHandler uses 4.x ResponseHandler and HttpResponse
    private static class CustomResponseHandler implements ResponseHandler<String>
    {
        private final String uri;

        public CustomResponseHandler(String uri)
        {
            super();
            this.uri = uri;
        }

        // Changed ClassicHttpResponse to HttpResponse
        @Override
        public String handleResponse(final HttpResponse response) throws IOException
        {
            final HttpEntity entity = response.getEntity();
            
            // Changed getCode() to getStatusLine().getStatusCode() for 4.x
            int statusCode = response.getStatusLine().getStatusCode();
            
            if (statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) // SC_REDIRECTION is 300, using SC_MULTIPLE_CHOICES
            {
                String body = null;
                if (entity != null)
                {
                    try
                    {
                        // ParseException is a checked exception in 5.x but RuntimeException in 4.x, 
                        // so we handle IOException and generic Exception
                        body = EntityUtils.toString(entity);
                    }
                    catch (final Exception ignore)
                    {
                        // ignore additional exceptions reading the body
                    }
                }

                EntityUtils.consume(entity);

                // Replaced 'var' and List.toList() with explicit types and Collectors.toList()
                List<Pair<String, String>> headers = Stream.of(response.getAllHeaders())
                        .map(h -> new Pair<>(h.getName(), h.getValue()))
                        .collect(Collectors.toList());
                        
                throw new WebAccessException(buildMessage(uri, statusCode), statusCode, headers, body);
            }

            if (entity == null)
                return null;

            try
            {
                // In 4.x, EntityUtils.toString() only throws IOException (not ParseException)
                return EntityUtils.toString(entity);
            }
            catch (final Exception ex) // Catch-all for possible RuntimeExceptions in 4.x
            {
                throw new ClientProtocolException(ex);
            }
        }
    }

    // Timeouts are specified as milliseconds (int) in 4.x, not Timeout objects.
    public static final RequestConfig defaultRequestConfig = RequestConfig.custom()
             // Timeout.ofSeconds(20) becomes 20 * 1000 ms
            .setSocketTimeout(20 * 1000)
            .setConnectTimeout(5 * 1000)
            .setConnectionRequestTimeout(5 * 1000) 
            .setCookieSpec(CookieSpecs.STANDARD) // StandardCookieSpec becomes CookieSpecs.STANDARD
            .build();

    // ConnectionConfig is not typically used on the client builder in 4.x like this.
    // Timeouts are set on the RequestConfig or the ConnectionManager.
    // The connection timeout is moved above.
    // We keep this placeholder, but the content is merged into RequestConfig.
    public static final Integer defaultConnectionTimeoutMs = 5 * 1000; 
    
    // Changed URIBuilder to org.apache.http.client.utils.URIBuilder
    private final URIBuilder builder;
    private List<Header> headers = new ArrayList<>();
    private String userAgent = getUserAgent();

    // Constructors and fluent methods remain largely the same, only the URIBuilder class changes.
    public WebAccess(String host, String path)
    {
        this.builder = new URIBuilder();
        this.builder.setScheme("https"); //$NON-NLS-1$
        this.builder.setHost(Objects.requireNonNull(host).trim());
        this.builder.setPath(Objects.requireNonNull(path).trim());
    }

    public WebAccess(String url) throws URISyntaxException
    {
        this.builder = new URIBuilder(url);
    }

    public WebAccess withScheme(String scheme)
    {
        this.builder.setScheme(Objects.requireNonNull(scheme).trim());
        return this;
    }

    public WebAccess withPort(Integer port)
    {
        this.builder.setPort(port != null ? port : -1);
        return this;
    }

    public WebAccess withFragment(String fragment)
    {
        this.builder.setFragment(Objects.requireNonNull(fragment).trim());
        return this;
    }

    public WebAccess addParameter(String param, String value)
    {
        this.builder.addParameter(param, value);
        return this;
    }

    public WebAccess addHeader(String param, String value)
    {
        this.headers.add(new BasicHeader(param, value));
        return this;
    }

    public WebAccess addBearer(String bearer)
    {
        this.headers.add(new BasicHeader("Authorization", "Bearer " + bearer)); //$NON-NLS-1$ //$NON-NLS-2$
        return this;
    }

    public WebAccess addUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
        return this;
    }

    public String get() throws IOException
    {
        // Replaced 'var' with explicit type String
        String response = executeWith(uri -> new HttpGet(uri));

        // the response handler can return a null string.
        if (response == null)
            throw new IOException("No message entity in response: " + builder.toString()); //$NON-NLS-1$
        return response;
    }

    public void post(String body) throws IOException
    {
        executeWith(uri -> {
            HttpPost request = new HttpPost(uri);
            StringEntity userEntity = new StringEntity(body);
            request.setEntity(userEntity);
            return request;
        });
    }

    public String postReturn(String body) throws IOException
    {
        return executeWith(uri -> {
            HttpPost request = new HttpPost(uri);
            StringEntity userEntity = new StringEntity(body);
            request.setEntity(userEntity);
            return request;
        });

    }

    public String postUrlEncoding(List<NameValuePair> body) throws Exception
    {
        return executeWith(uri -> {
            HttpPost request = new HttpPost(uri);
            // In 4.x, UrlEncodedFormEntity requires Charset, but we omit for default
            UrlEncodedFormEntity userEntity = new UrlEncodedFormEntity(body);
            request.setEntity(userEntity);
            return request;
        });
    }

    private String executeWith(Request function) throws IOException
    {
        try
        {
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            // In 4.x, connection settings are typically set here:
            // connectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(defaultRequestConfig.getSocketTimeout()).build());
            // We use the default.
            
            CloseableHttpClient client = HttpClientBuilder.create() //
                    .setConnectionManager(connectionManager) //
                    .setDefaultRequestConfig(defaultRequestConfig) //
                    .setDefaultHeaders(this.headers) //
                    .setUserAgent(this.userAgent) //
                    .useSystemProperties() //
                    .build();

            URI uri = builder.build();
            // HttpUriRequestBase replaced with HttpRequestBase
            HttpRequestBase request = function.create(uri);

            // ResponseHandler is the 4.x equivalent of HttpClientResponseHandler
            return client.execute(request, new CustomResponseHandler(uri.toString()));
        }
        catch (HttpResponseException e)
        {
            // HttpResponseException in 4.x uses getStatusCode()
            throw new WebAccessException(buildMessage(builder.toString(), e.getStatusCode()), e.getStatusCode(),
                                new ArrayList<>(), null);
        }
        catch (URISyntaxException e)
        {
            throw new IOException(e);
        }
    }

    private static String getUserAgent()
    {
        return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.3.1 Safari/605.1.15"; // NOSONAR
    }


    private static String buildMessage(String uri, int statusCode)
    {
        String message = String.valueOf(statusCode);
        try
        {
            // This class is the same in 4.x and 5.x
            String reason = EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, Locale.getDefault());
            if (reason != null)
                message += " " + reason; //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            // ignore -> unable to retrieve message
        }
        message += " --> " + uri; //$NON-NLS-1$
        return message;
    }

    public String getURL() throws URISyntaxException
    {
        return builder.build().toASCIIString();
    }
}