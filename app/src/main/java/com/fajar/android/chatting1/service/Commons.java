package com.fajar.android.chatting1.service;

import com.fajar.android.chatting1.util.Logs;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Commons {
    private static RestTemplate restTemplate;
    private static ObjectMapper objectMapper;

    public static RestTemplate getRestTemplate(){
        if(null == restTemplate) {
            ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());

              restTemplate = new RestTemplate(factory);
            //restTemplate = new RestTemplate();
//            restTemplate.setErrorHandler(errorHandler());
        }
        return restTemplate;
    }

    public static ObjectMapper getObjectMapper(){
        if(null == objectMapper){
             objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    private static ResponseErrorHandler errorHandler() {
        return new CustomErrorHandler();
    }

    public static <T> HttpEntity<T> httpEntity(T payload){
        return httpEntity(payload,null);

    }

    public static <T> HttpEntity<T> httpEntityWithRequestKey(T payload, String requestKey){
        Logs.log("--------------- requestKey: ", requestKey);
        Map<String , String> map = new HashMap<>();
        map.put("request_key", requestKey);
        return httpEntity(payload,map);
    }

    public static <T> HttpEntity<T> httpEntity(T payload, Map<String, String> header) {
        HttpHeaders headers = new HttpHeaders();
        headers.setConnection("close");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.add("Accept-Encoding", "identity");
        headers.add("Accept", "application/json");
        if(null != header){
            for (Map.Entry<String, String> entry:
                    header.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }
//        headers.add("user-agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<T> entity = new HttpEntity<T>( payload,headers);
        return entity;

    }
}
