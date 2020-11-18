package com.fajar.android.chatting1.service.websocket;

public interface CustomMessageHandler {

    public void handleOnMessage(Object message, AppClientEndpoint client);

}