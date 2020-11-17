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
            Logs.log("getPartner: ", response.getRegisteredRequest());
            return response;
        }catch ( Exception ex){
            Logs.log("getPartner: ", ex);
            throw ex;
        }
    }
}
