package com.macrotel.zippyworld_test.config;

import com.macrotel.zippyworld_test.pojo.BaseResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.util.Map;

import static com.macrotel.zippyworld_test.config.AppConstants.EMPTY_RESULT;

public class ThirdPartyAPI {

    public Object callAPI(String url, HttpMethod method, Map<String, String> customHeaders, Object requestBody) {
        try {
            RestTemplate restTemplate = createRestTemplate();
            HttpHeaders headers = new HttpHeaders();
            if (customHeaders != null) {
                headers.setAll(customHeaders);
            }
            HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

            return restTemplate.exchange(url, method, requestEntity, Object.class).getBody();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            // Handle exceptions
        }
        return createErrorResponse();
    }

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                if (connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(createTrustAllSSLSocketFactory());
                }
                super.prepareConnection(connection, httpMethod);
            }
        });
        return restTemplate;
    }

    private SSLSocketFactory createTrustAllSSLSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("Error creating TrustAllSSLSocketFactory", e);
        }
    }


    private BaseResponse createErrorResponse() {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus_code("500");
        baseResponse.setMessage("Internal Server Error");
        baseResponse.setResult(EMPTY_RESULT);
        return baseResponse;
    }
}
