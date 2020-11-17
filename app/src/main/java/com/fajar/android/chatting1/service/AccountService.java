package com.fajar.android.chatting1.service;

import com.fajar.android.chatting1.constants.Endpoints;
import com.fajar.android.chatting1.models.PostResponse;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class AccountService {
    private static AccountService instance = null;
    private AccountService(){

    }

    public static  AccountService instance(){
        if(instance == null){
            instance = new AccountService();
        }
        return instance;
    }

    public WebResponse getUser(String requestKey){
        String endPoint = Endpoints.ENDPOINT_GET_USER;
        System.out.println("call register to: "+endPoint);

        try {
            ResponseEntity<WebResponse> responseEntity = Commons.getRestTemplate().exchange(endPoint, HttpMethod.POST, Commons.httpEntityWithRequestKey(null, requestKey),
                    WebResponse.class);
            WebResponse response =  responseEntity.getBody();
            Logs.log("get user: ", response.getRegisteredRequest());
            return response;
        }catch ( Exception ex){
            Logs.log("ERROR getUser: ", ex);
            throw ex;
        }
    }
    public WebResponse invalidateUser(String requestKey){
        String endPoint = Endpoints.ENDPOINT_INVALIDATE_USER;
        System.out.println("call invalidate to: "+endPoint);

        try {
            ResponseEntity<WebResponse> responseEntity = Commons.getRestTemplate().exchange(endPoint, HttpMethod.POST, Commons.httpEntityWithRequestKey(null, requestKey),
                    WebResponse.class);
            WebResponse response =  responseEntity.getBody();
            Logs.log("invalidate user: ", response.getRegisteredRequest());
            return response;
        }catch ( Exception ex){
            Logs.log("ERROR invalidate: ", ex);
            throw ex;
        }
    }


    public WebResponse register(String username){
        String endPoint = Endpoints.ENDPOINT_REGISTER;
        System.out.println("call register to: "+endPoint);

        try {
            ResponseEntity<WebResponse> responseEntity = Commons.getRestTemplate().exchange(endPoint, HttpMethod.POST, Commons.httpEntity(WebRequest.builder().username(username).build()),
                    WebResponse.class);
            WebResponse response =  responseEntity.getBody();
            List<String> requestKey = responseEntity.getHeaders().get("request_key");
            Logs.log("requestKey: ", requestKey.get(0));
            Logs.log("response: ", response);
            response.setMessage(requestKey.get(0));
            return response;
        }catch ( Exception ex){
            Logs.log("ERROR register: ", ex);
            throw ex;
        }
    }
}
