package com.alekseyorlov.vkdump.web.handler.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public final class HttpRequestHelper {

    private final static String QUERY_PARAMS_REGEX = "^(?:[^=]+=[^=]*&)*(?:[^=]+=[^=]*)$";

    public static Map<String, String> getQueryParams(HttpRequest request) {
        Map<String, String> collectedQueryParams = null;
        String requestUri = request.getRequestLine().getUri();

        if (requestUri.indexOf("?") != -1) {
            String queryParamsLine = requestUri.substring(requestUri.indexOf("?") + 1);
            if (queryParamsLine.matches(QUERY_PARAMS_REGEX)) {
                List<NameValuePair> queryParams = URLEncodedUtils.parse(queryParamsLine, Charset.defaultCharset());
                collectedQueryParams = collect(queryParams);
            }
        }

        return collectedQueryParams;
    }
    
    public static Map<String, String> getRequestParams(HttpEntityEnclosingRequest request) throws IOException {
        Map<String, String> collectedFormParams = null;
        
        HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
        
        List<NameValuePair> formParams = URLEncodedUtils.parse(entity);
        if (formParams != null) {
            collectedFormParams = collect(formParams);
        }
        
        return collectedFormParams;
    }
    
    private static Map<String, String> collect(List<NameValuePair> pairs) {
        Map<String, String> collected = new HashMap<String, String>();
        for(NameValuePair pair: pairs) {
            collected.put(pair.getName(),pair.getValue());
        }
        
        return collected;
    }
}
