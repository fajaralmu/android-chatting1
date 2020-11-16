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


    public WebResponse register(String username){
        String endPoint = Endpoints.ENDPOINT_REGISTER;
        System.out.println("call register to: "+endPoint);
        try {
            ResponseEntity<WebResponse> response = Commons.getRestTemplate().exchange(endPoint, HttpMethod.POST, Commons.httpEntity(WebRequest.builder().username(username).build()),
                    WebResponse.class);
            return  response.getBody();
        }catch ( Exception ex){
            Logs.log("ERROR register: ", ex);
            throw ex;
        }
    }
}
