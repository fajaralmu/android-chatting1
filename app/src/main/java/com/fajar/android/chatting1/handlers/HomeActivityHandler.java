package com.fajar.android.chatting1.handlers;

import com.fajar.android.chatting1.activities.HomeActivity;
import com.fajar.android.chatting1.constants.Endpoints;
import com.fajar.android.chatting1.service.websocket.WebSocketHandler;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

public class HomeActivityHandler {
    final HomeActivity activity;

    private WebSocketHandler webSocketHandler;

    private static HomeActivityHandler homeActivityHandler;

    public static HomeActivityHandler instance(HomeActivity homeActivity){
        if(null == homeActivityHandler){
            homeActivityHandler = new HomeActivityHandler(homeActivity);
        }
        return homeActivityHandler;
    }

    private HomeActivityHandler(HomeActivity activity){
        this.activity = activity;
    }

    public void initializeWebsocket(RegisteredRequest account){
        String wsUrl = Endpoints.WS_HOST+"realtime-app";
        webSocketHandler = new WebSocketHandler(wsUrl, account);
        webSocketHandler.register();
        webSocketHandler.setNewChatMessageCallback(this::handleNewChatMessage);
    }

    private void handleNewChatMessage(WebResponse response, Exception e) {
        activity.showNewChatMessage(response);
    }
}
