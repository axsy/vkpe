package com.alekseyorlov.vkdump.web.handler.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.junit.Test;

public class HttpRequestHelperTest {

    @Test
    public final void shouldGetQueryParams() {
        
        // given
        HttpRequest request = new BasicHttpRequest("GET", "http:////www.example.org//service?a=1&b=2");
        
        Map<String, String> expectedQueryParams = new HashMap<String, String>();
        expectedQueryParams.put("a", "1");
        expectedQueryParams.put("b", "2");
        
        // when
        Map<String, String> actualQueryParams = HttpRequestHelper.getQueryParams(request);
        
        // then
        assertEquals(expectedQueryParams, actualQueryParams);
    }
    
    @Test
    public final void shouldReturnNullOnMissingQueryParams() {
        
        // given
        HttpRequest request = new BasicHttpRequest("GET", "http:////www.example.org//service");
        
        // when
        Map<String, String> actualQueryParams = HttpRequestHelper.getQueryParams(request);
        
        // then
        assertNull(actualQueryParams);
    }

    @Test
    public final void shouldReturnNullOnEmptyQueryParams() {
        
        // given
        HttpRequest request = new BasicHttpRequest("GET", "http:////www.example.org//service?");
        
        // when
        Map<String, String> actualQueryParams = HttpRequestHelper.getQueryParams(request);
        
        // then
        assertNull(actualQueryParams);
    }
    
    @Test
    public final void shouldReturnEmptyQueryParams() {
        
        // given
        HttpRequest request = new BasicHttpRequest("GET", "http:////www.example.org//service?a=");
        
        // when
        Map<String, String> actualQueryParams = HttpRequestHelper.getQueryParams(request);
        
        // then
        assertEquals(1, actualQueryParams.keySet().size());
        assertEquals("", actualQueryParams.get("a"));
    }
    
    @Test
    public final void shouldReturnNullOnBrokenQueryParams() {
        
        // given
        HttpRequest request = new BasicHttpRequest("GET", "http:////www.example.org//service?broken");
        
        // when
        Map<String, String> actualQueryParams = HttpRequestHelper.getQueryParams(request);
        
        // then
        assertNull(actualQueryParams);
    }
    
    @Test
    public final void shouldGetFormParams() throws IOException {
        
        // given
        String requestBody = "b=2&c=3";
        
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContentType("application/x-www-form-urlencoded");
        entity.setContent(new ByteArrayInputStream(requestBody.getBytes()));
        entity.setContentLength(requestBody.length());
        
        HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
                "POST",
                "http:////www.example.org//service?a=1");
        request.setEntity(entity);
        
        // when
        Map<String, String> actualFormParams;

        actualFormParams = HttpRequestHelper.getRequestParams(request);
  
        // then
        assertEquals(2, actualFormParams.keySet().size());
        
        assertTrue(actualFormParams.containsKey("b"));
        assertTrue(actualFormParams.containsKey("c"));
        
        assertEquals("2", actualFormParams.get("b"));
        assertEquals("3", actualFormParams.get("c"));
    }
    
    @Test
    public final void shouldReturnEmptyFormParams() throws IOException {
        
        // given
        String requestBody = "b=";
        
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContentType("application/x-www-form-urlencoded");
        entity.setContent(new ByteArrayInputStream(requestBody.getBytes()));
        entity.setContentLength(requestBody.length());
        
        HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
                "POST",
                "http:////www.example.org//service?a=1");
        request.setEntity(entity);
        
        // when
        Map<String, String> actualFormParams;

        actualFormParams = HttpRequestHelper.getRequestParams(request);
  
        // then
        assertEquals(1, actualFormParams.keySet().size());
        assertEquals("", actualFormParams.get("b"));
    }
}
