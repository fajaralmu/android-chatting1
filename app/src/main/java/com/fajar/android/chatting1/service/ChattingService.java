package com.fajar.android.chatting1.service;

import com.fajar.android.chatting1.constants.Endpoints;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.WebResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ChattingService {
    private static ChattingService instance = null;
    private ChattingService(){

    }

    public static  ChattingService instance(){
        if(instance == null){
            instance = new ChattingService();
        }
        return instance;
    }
    public WebResponse getPartner(String partnerId, String requestKey){
        String endPoint = Endpoints.ENDPOINT_GET_PARTNER+partnerId;

        try {
            ResponseEntity<WebResponse> responseEntity = Commons.getRestTemplate().exchange(endPoint, HttpMethod.POST, Commons.httpEntityWithRequestKey(null, requestKey),
                    WebResponse.class);
            WebResponse response =  responseEntity.getBody();
            Logs.log("SUCCESS getPartner: ", response.getRegisteredRequest());
            return response;
        }catch ( Exception ex){
            Logs.log("ERROR getPartner: ", ex);
            throw ex;
        }
    }
    public WebResponse initializeChat(String partnerId, String requestKey){
        String endPoint = Endpoints.ENDPOINT_INITIALIZE_CHAT+partnerId;

        try {
            ResponseEntity<WebResponse> responseEntity = Commons.getRestTemplate().exchange(endPoint, HttpMethod.POST, Commons.httpEntityWithRequestKey(null, requestKey),
                    WebResponse.class);
            WebResponse response =  responseEntity.getBody();
            Logs.log("SUCCESS initializeChat");
            return response;
        }catch ( Exception ex){
            Logs.log("ERROR initializeChat: ", ex);
            throw ex;
        }
    }

    public WebResponse getChattingPartners(String requestKey) {
        String endPoint = Endpoints.ENDPOINT_GET_CHATTING_PARTNERS;

        try {
            ResponseEntity<WebResponse> responseEntity = Commons.getRestTemplate().exchange(endPoint, HttpMethod.POST, Commons.httpEntityWithRequestKey(null, requestKey),
                    WebResponse.class);
            WebResponse response = responseEntity.getBody();
            if(response.getResultList().size()>0){
                Object listItem = response.getResultList().get(0);
                Logs.log(listItem.getClass());
            }
            Logs.log("SUCCESS getChattingPartners");
            return response;
        }catch ( Exception ex){
            Logs.log("ERROR getChattingPartners: ", ex);
            throw ex;
        }
    }
}