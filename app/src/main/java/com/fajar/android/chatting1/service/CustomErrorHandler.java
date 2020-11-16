package com.fajar.android.chatting1.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import com.fajar.android.chatting1.util.Logs;

class CustomErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        boolean HAS_ERROR = response.getStatusCode()!= HttpStatus.OK;
        Logs.log("Has Error: ", HAS_ERROR);
        System.out.println("HAS ERROR: "+HAS_ERROR);
        return HAS_ERROR;
//        return false;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        Logs.log("Handle Error: ", response.getStatusCode());

    }
}
