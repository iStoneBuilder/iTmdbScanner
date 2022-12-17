package com.stone.it.micro.ifeast.util;

import com.stone.it.micro.ifeast.vo.ResponseVO;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

/**
 * @author cj.stone
 * @Date 2022/12/9
 * @Desc
 */
public class HttpRequestUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtil.class);

    private static final boolean isHttp = "true".equals(PropertiesUtil.getValue("micro.stone.client", "true"));

    private static HttpClient httpClient;

    private static HttpClient httpsClient;

    /**
     * 设置连接超时时间，单位毫秒。
     */
    private static final int CONNECT_TIMEOUT = 6000;

    /**
     * 设置连接超时时间，单位毫秒。
     */
    private static final int REQUEST_TIMEOUT = 6000;

    /**
     * 请求获取数据的超时时间(即响应时间)，单位毫秒。
     */
    private static final int SOCKET_TIMEOUT = 6000;

    public static ResponseVO doGet(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return execute("GET", url, null, headers, params);
    }

    private static ResponseVO execute(String methodType, String url, String body, Map<String, String> header, Map<String, String> params) throws Exception {
        LOGGER.info("HTTP REQUEST TYPE IS : " + methodType);
        // 处理请求参数
        url = handleUrl(url, params);
        LOGGER.info("HTTP REQUEST URL IS : " + url);
        // 获取客户端
        HttpClient client = isHttp ? getClient(url) : getHttpsClient(url);
        // 获取Base
        HttpRequestBase requestBase = getRequestBase(methodType, url, body, header);
        // 执行请求
        HttpResponse httpResponse = client.execute(requestBase);
        ResponseVO responseVO = new ResponseVO();
        LOGGER.info("HTTP RESPONSE STATUS CODE IS : " + httpResponse.getStatusLine().getStatusCode());
        responseVO.setStatusCode(String.valueOf(httpResponse.getStatusLine().getStatusCode()));
        String dataResponse = EntityUtils.toString(httpResponse.getEntity(), Charsets.UTF_8);
        responseVO.setResponseBody(dataResponse);
        LOGGER.info("HTTP RESPONSE DATA IS : " + dataResponse);
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            responseVO.setStatusMessage("success");
        } else {
            responseVO.setStatusMessage("fail");
        }
        return responseVO;
    }

    protected static String handleUrl(String url, Map<String, String> params) throws Exception {
        StringBuilder uriParams = new StringBuilder();
        Set<Map.Entry<String, String>> paramSet = params.entrySet();
        for (Map.Entry<String, String> next : paramSet) {
            uriParams.append("&").append(next.getKey()).append("=").append(next.getValue());
        }
        String paramUri = uriParams.toString();
        if (!url.contains("?")) {
            paramUri = paramUri.replaceFirst("&", "?");
        }
        LOGGER.info("http request params is : " + paramUri);
        return url + paramUri;
    }

    protected static HttpRequestBase getRequestBase(String methodType, String url, String body, Map<String, String> header) throws UnsupportedEncodingException {
        HttpRequestBase base = null;
        switch (methodType) {
            case "POST":
                base = new HttpPost(url);
                if (body != null) {
                    StringEntity httpEntity = new StringEntity(body);
                    ((HttpPost) base).setEntity(httpEntity);
                }
                break;
            case "PUT":
                base = new HttpPut(url);
                if (body != null) {
                    StringEntity httpEntity = new StringEntity(body);
                    ((HttpPut) base).setEntity(httpEntity);
                }
                break;
            case "DELETE":
                base = new HttpDelete(url);
                break;
            case "GET":
                base = new HttpGet(url);
                break;
            default:
                break;
        }
        // 设置请求头
        if (header != null) {
            setHeader(base, header);
        }
        return base;
    }

    private static void setHeader(HttpRequestBase base, Map<String, String> header) {
        // 设置默认请求头
        base.setHeader("Content-Type", "application/json;charset=utf-8");
        base.setHeader("Accept", "application/json");
        if (header.size() == 0) {
            return;
        }
        Set<Map.Entry<String, String>> headers = header.entrySet();
        for (Map.Entry<String, String> next : headers) {
            base.setHeader(next.getKey(), next.getValue());
        }
    }


    public static synchronized HttpClient getClient(String uri) {
        if (httpClient == null) {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setConnectionRequestTimeout(REQUEST_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpClient = HttpClientBuilder.create().setMaxConnTotal(200).setMaxConnPerRoute(100).setUserAgent("").setDefaultRequestConfig(config).disableAutomaticRetries().build();
        }
        return httpClient;
    }

    public static synchronized HttpClient getHttpsClient(String uri) {
        if (httpsClient == null) {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setConnectionRequestTimeout(REQUEST_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
            httpsClient = HttpClientBuilder.create().setMaxConnTotal(200).setMaxConnPerRoute(100).setUserAgent("").setDefaultRequestConfig(config).disableAutomaticRetries().build();
        }
        return httpsClient;
    }

}
