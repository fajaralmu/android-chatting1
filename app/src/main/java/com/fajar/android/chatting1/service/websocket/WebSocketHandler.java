package com.fajar.android.chatting1.service.websocket;

import android.os.AsyncTask;

import com.fajar.android.chatting1.handlers.MyConsumer;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.StringUtil;
import com.fajar.android.chatting1.util.ThreadUtil;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

public class WebSocketHandler implements CustomMessageHandler {

    final String wsURL;
    private static final String SUBS_PREFIX = "wsResp";

    private final RegisteredRequest account;
    private MyConsumer<WebResponse> newChatMessageCallback;

    public WebSocketHandler(String wsURL, RegisteredRequest account) {
        this.wsURL = wsURL;
        this.account = account;
    }

    private AppClientEndpoint appClientEndpoint;

    public void setNewChatMessageCallback(MyConsumer<WebResponse> newChatMessageCallback) {
        this.newChatMessageCallback = newChatMessageCallback;
    }

    public void register() {
        String requestId = account.getRequestId();
        String wsEndpoint = wsURL + "/" + StringUtil.randomNumber() + "/" + requestId + "/websocket";

        appClientEndpoint = new AppClientEndpoint(wsEndpoint, true, requestId, SUBS_PREFIX);
        appClientEndpoint.setCustomMessageHandler(this);
        appClientEndpoint.setOnConnectCallback(this::onConnect);

        System.out.println("connecting to: " + wsURL);
        runnintTask().execute();
    }

    private void onConnect(Object o, Exception e) {
        Logs.log("ON CONNECT");
        subscribeNewChatMessage();
    }

    private void subscribeNewChatMessage() {
        appClientEndpoint.subscribe("newchatting/" + account.getRequestId());
    }


    private AsyncTask<String, Void, String> runnintTask() {
        return new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                ThreadUtil.runAndStart(() -> {
                    appClientEndpoint.start();
                });
                return null;
            }
        };
    }


    @Override
    public void handleOnMessage(Object payload, AppClientEndpoint client) {
        Logs.log("WEBSOCKET HANDLER handleOnMessage:" + payload);
        if (!(payload instanceof WebResponse)) {
            //Recently Connected
            if (payload instanceof String && payload.toString().startsWith("[ID]")) {
                // updateSessionId(payload);
            } else {
                // showPlainMessage(payload);
            }
            return;
        }

        showMessage((WebResponse) payload);
    }

    public void showMessage(WebResponse message) {
        if(null != newChatMessageCallback){
            newChatMessageCallback.accept(message, null);
        }
    }

}
