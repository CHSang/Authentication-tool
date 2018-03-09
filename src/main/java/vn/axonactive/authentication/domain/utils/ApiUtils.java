package vn.axonactive.authentication.domain.utils;


import java.security.cert.X509Certificate;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public final class ApiUtils {

	private static final Logger logger = LoggerFactory.getLogger(ApiUtils.class);

	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String ACCEPT = "Accept";
	private static final String CANNOT_CONNECT_ERROR = "Can not connect to server: ";
	
	private static Object locker = new Object();
	
	private ApiUtils() {
	}
	
	private static CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setSSLSocketFactory(ssl()).build();
	}
	
	private static SSLConnectionSocketFactory ssl() { 
        try {
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    List<X509Certificate> certificateList = Collections.<X509Certificate>emptyList();
                    X509Certificate[] certificateArray = new X509Certificate[0];
                    certificateList.toArray(certificateArray);
                    return certificateArray;
                }
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    return;
                }
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    return;
                }

            } };
            

            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            return new SSLConnectionSocketFactory(sslcontext);
        } catch (Exception e) {
            logger.error(StringUtils.EMPTY, e);
            return null;
        }
       
    }

    @SafeVarargs
    public static void callGetAsync(String url, SimpleEntry<String, String>... entries) {
        new Thread(() -> getMethod(url, entries)).start();
    }
	
	@SafeVarargs
	private static Map<String, Object> simpleEntryToMap(SimpleEntry<String, String>... entries) {
		Map<String, Object> map = new HashMap<>();
		for (SimpleEntry<String, String> entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	// GET METHOD
	@SafeVarargs
	private static Optional<HttpResponse<JsonNode>> getMethod(String apiURL, SimpleEntry<String, String>... entries) {
		Map<String, Object> params = simpleEntryToMap(entries);
		try {		    
		    HttpResponse<JsonNode> httpResponse;

            synchronized (locker) {
                Unirest.setHttpClient(getHttpClient());
                httpResponse = Unirest
                        .get(apiURL)
                        .header(ACCEPT, CONTENT_TYPE_JSON)
                        .queryString(params)
                        .asJson();
            }
            
            return Optional.of(httpResponse);
		} catch (UnirestException e) {
			logger.error(CANNOT_CONNECT_ERROR + apiURL + " ", e);
			return Optional.empty();
		}
	}

	@SafeVarargs
	public static Optional<JSONArray> getJsonArrayFromGetAPI(String apiURL, SimpleEntry<String, String>... entries) {
		Optional<HttpResponse<JsonNode>> httpResponse = getMethod(apiURL, entries);
		if (httpResponse.isPresent()) {
			return Optional.of(httpResponse.get().getBody().getArray());
		} else {
			return Optional.empty();
		}
	}
	
	// POST METHOD
	@SafeVarargs
	private static Optional<HttpResponse<JsonNode>> postMethod(String apiURL, SimpleEntry<String, String>... entries) {
		Map<String, Object> params = simpleEntryToMap(entries);
		try {
            HttpResponse<JsonNode> httpResponse;

            synchronized (locker) {
                Unirest.setHttpClient(getHttpClient());
                httpResponse = Unirest
                        .post(apiURL)
                        .header(ACCEPT, CONTENT_TYPE_JSON)
                        .fields(params)
                		.asJson();
            }
            
			return Optional.of(httpResponse);
		} catch (UnirestException e) {
			logger.error(CANNOT_CONNECT_ERROR + apiURL + " ", e);
			return Optional.empty();
		}
	}

	@SafeVarargs
	public static Optional<JSONArray> getJsonArrayFromPostAPI(String apiURL, SimpleEntry<String, String>... entries) {
		Optional<HttpResponse<JsonNode>> httpResponse = postMethod(apiURL, entries);
		if (httpResponse.isPresent()) {
			return Optional.of(httpResponse.get().getBody().getArray());
		} else {
			return Optional.empty();
		}
	}

	@SafeVarargs
	public static Optional<JSONObject> getJsonObjectFromPostAPI(String apiURL, SimpleEntry<String, String>... entries) {
		Optional<HttpResponse<JsonNode>> httpResponse = postMethod(apiURL, entries);
		if (httpResponse.isPresent()) {
			return Optional.of(httpResponse.get().getBody().getObject());
		} else {
			return Optional.empty();
		}
	}
}
