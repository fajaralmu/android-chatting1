package com.fajar.android.chatting1.handlers;

import com.fajar.android.chatting1.activities.HomeActivity;
import com.fajar.android.chatting1.constants.Endpoints;
import com.fajar.android.chatting1.service.websocket.WebSocketHandler;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;

public class GeneralApplicationHandler {
    final HomeActivity activity;

    private WebSocketHandler webSocketHandler;

    private static GeneralApplicationHandler instance;

    public static GeneralApplicationHandler instance(HomeActivity homeActivity){
        if(null == instance){
            instance = new GeneralApplicationHandler(homeActivity);
        }
        return instance;
    }

    private GeneralApplicationHandler(HomeActivity activity){
        this.activity = activity;
    }

    public void initializeWebsocket(RegisteredRequest account){
        String wsUrl = Endpoints.WS_HOST+"realtime-app";
        webSocketHandler = new WebSocketHandler(wsUrl, account);
        webSocketHandler.register();
        webSocketHandler.setNewChatMessageCallback(this::handleNewChatMessage);
        webSocketHandler.setOnConnectCallback(this::handleOnConnect);
    }

    private void handleOnConnect(Object o, Exception e) {
        activity.onConnectedWebsocket();
    }

    private void handleNewChatMessage(WebResponse response, Exception e) {
        activity.showNewChatMessage(response);
    }

    public void markMessageAsRead(RegisteredRequest account, RegisteredRequest partner){
        WebRequest payload = new WebRequest();
        payload.setDestination(partner.getRequestId());
        payload.setPartnerId(partner.getRequestId());
        payload.setOriginId(account.getRequestId());
        webSocketHandler.sendMessage("/chatting/markmessageasread", payload);
    }
}
