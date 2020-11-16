package com.example.calltest.kit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title :
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Author :欧瑞荫 2017/12/7
 * </p>
 * <p>
 * Department : 研发部
 * </p>
 * <p> Copyright : ©江苏汇鑫融智软件科技有限公司 </p>
 */
public class HttpKit {

    public static String service(CloseableHttpClient httpclient
            , String url
            , Map<String, String> headers
            , String method
            , Map<String, String> parameters
            , String mediaType
            , Object payload
            , RequestConfig requestConfig) throws Exception {
        if (httpclient == null) {
            httpclient = HttpClients.createDefault();
        }
        CloseableHttpResponse httpResponse = null;
        try {
            if (requestConfig == null) {
                requestConfig = RequestConfig.custom()
                        .setConnectTimeout(3 * 1000)
                        .setConnectionRequestTimeout(65 * 1000)
                        .setSocketTimeout(65 * 1000).build();
            }
            if (RequestMethod.GET.name().equals(method)) {
                URIBuilder uriBuilder = new URIBuilder(url);
                if (parameters != null) {
                    for (Map.Entry<String, String> entry : parameters.entrySet()) {
                        uriBuilder.addParameter(entry.getKey(), entry.getValue());
                    }
                }
                HttpGet httpGet = new HttpGet(uriBuilder.build());
                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        httpGet.setHeader(entry.getKey(), entry.getValue());
                    }
                }
                httpGet.setConfig(requestConfig);
                httpResponse = httpclient.execute(httpGet);
            } else {
                HttpPost httpPost = new HttpPost(url);
                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        httpPost.setHeader(entry.getKey(), entry.getValue());
                    }
                }
                AbstractHttpEntity entity;
                if (mediaType == null || mediaType.startsWith("application/x-www-form-urlencoded")) {
                    List<NameValuePair> paramList = new ArrayList<>();
                    for (Map.Entry<String, String> entry : parameters.entrySet()) {
                        paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                    }
                    entity = new UrlEncodedFormEntity(paramList, "UTF-8");
                } else if (mediaType.startsWith(ContentType.APPLICATION_OCTET_STREAM.getMimeType())) {
                    byte[] bytes = (byte[]) payload;
                    entity = new ByteArrayEntity(bytes, ContentType.create(mediaType));
                } else {
                    entity = new StringEntity((String) payload, ContentType.create(mediaType, "UTF-8"));
                }

                httpPost.setEntity(entity);
                httpPost.setConfig(requestConfig);
                httpResponse = httpclient.execute(httpPost);
            }
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                return EntityUtils.toString(httpEntity);//取出应答字符串
            } else {
                throw new Exception("HTTP处理异常,错误代码:" + statusCode);
            }
        } finally {
            httpclient.close();
            httpResponse.close();
            httpResponse.close();
        }
    }

    public static String service(String url
            , Map<String, String> headers
            , String method
            , Map<String, String> parameters
            , String mediaType
            , Object payload
            , RequestConfig requestConfig) throws Exception {
        return service(null, url, headers, method, parameters, mediaType, payload, requestConfig);
    }

    public static String service(String url
            , Map<String, String> headers
            , String method
            , Map<String, String> parameters
            , String mediaType
            , Object payload) throws Exception {
        return service(null, url, headers, method, parameters, mediaType, payload, null);
    }

    public static String customHttpsService(String url
            , Map<String, String> headers
            , String method
            , Map<String, String> parameters
            , String mediaType
            , String payload
            , RequestConfig requestConfig) throws IOException, URISyntaxException, Exception {
        return service(getHttpClient(), url, headers, method, parameters, mediaType, payload, requestConfig);
    }

    /**
     * 避免HttpClient的”SSLPeerUnverifiedException: peer not authenticated”异常
     * 不用导入SSL证书
     */
    public static CloseableHttpClient getHttpClient() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(ssf).build();
            return httpclient;
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpClients.createDefault();
        }
    }

    public static String postForm(String url
            , Map<String, String> headers
            , Map<String, String> parameters) throws Exception {
        return service(url, headers, HttpMethod.POST.name(), parameters, null, null);
    }

    public static String postForm(String url
            , Map<String, String> headers
            , Map<String, String> parameters
            , RequestConfig requestConfig) throws Exception {
        return service(url, headers, HttpMethod.POST.name(), parameters, null, null, requestConfig);
    }

    public static String postBody(String url
            , Map<String, String> headers
            , String mediaType
            , Object payload) throws Exception {
        return service(url, headers, HttpMethod.POST.name(), null, mediaType, payload);
    }

    public static String postBody(String url
            , Map<String, String> headers
            , String mediaType
            , Object payload
            , RequestConfig requestConfig) throws Exception {
        return service(url, headers, HttpMethod.POST.name(), null, mediaType, payload, requestConfig);
    }

    public static String get(String url, Map<String, String> headers) throws Exception {
        return service(url, headers, HttpMethod.GET.name(), null, null, null);
    }

    public static String fastGet(String url) throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        return service(url, null, HttpMethod.GET.name(), null, null, null, requestConfig);
    }

    public static void head(String heartUrl) throws Exception {
        URL url = new URL(heartUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");
        Map<String, List<String>> headerMap = conn.getHeaderFields();
        Iterator<String> iterator = headerMap.keySet().iterator();
        List<String> firstLine = headerMap.get(null);
        if (!firstLine.get(0).contains("200")) {
            throw new Exception("服务未正确响应");
        }
    }

    public static void testUrl(String originUrl) throws Exception {
        int index = StringUtils.ordinalIndexOf(originUrl, "/", 3);
        String heartUrl = originUrl.substring(0, index + 1);
        URL url = new URL(heartUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");
        conn.setConnectTimeout(5000);
        Map<String, List<String>> headerMap = conn.getHeaderFields();
        List<String> firstLine = headerMap.get(null);
        if (firstLine == null) {
            throw new Exception("订阅方网络不通");
        }
    }

}
