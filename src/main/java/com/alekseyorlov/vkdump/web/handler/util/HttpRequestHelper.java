package com.alekseyorlov.vkdump.web.handler.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;

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
        
        // So,
        // Behavior of URLEncodedUtils is broken in case UTF-8 data is being submitted from browser, because no browser
        // is adding character set data to Content-Type header and UrlEncodedUtils "successfully" assumes charset is
        // ISO_8859_1.
        // That is why this hack is necessary to force the character set for the content type of http entity.
        if (entity instanceof BasicHttpEntity) {
            ((BasicHttpEntity)entity).setContentType("application/x-www-form-urlencoded;charset=utf-8");
        }
        
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
