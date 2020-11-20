package com.fajar.android.chatting1.service.websocket;

import android.os.AsyncTask;

import com.fajar.android.chatting1.handlers.MyConsumer;
import com.fajar.android.chatting1.service.Commons;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.MessageMapper;
import com.fajar.livestreaming.dto.WebResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.glassfish.tyrus.client.ClientManager;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public class AppClientEndpoint /*extends Endpoint */ {

    private static CountDownLatch latch;

    private Session thisSession;
    private CustomMessageHandler CustomMessageHandler;

    private final String sockJsId;
    private final String wsURL;
    private final boolean withSockJS;
    private final Map<String, Object> subscribedTopics = new HashMap<>();
    private final String subscriptionPrefix;
    private MyConsumer onOpenCallback, onConnectCallback;

    private boolean connected = false;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public AppClientEndpoint(String wsURL, boolean withSockJs, String sockJsId, String subscriptionPrefix) {
        this.wsURL = wsURL;
        this.sockJsId = sockJsId;
        this.withSockJS = withSockJs;
        this.subscriptionPrefix = subscriptionPrefix;
    }

    public void setOnOpenCallback(MyConsumer onOpenCallback) {
        this.onOpenCallback = onOpenCallback;
    }

    public void setOnConnectCallback(MyConsumer onConnectCallback) {
        this.onConnectCallback = onConnectCallback;
    }

    public void setCustomMessageHandler(CustomMessageHandler h) {
        this.CustomMessageHandler = h;
    }

    public String getClientId() {
        if (withSockJS) {
            return sockJsId;
        } else {
            return thisSession.getId();
        }
    }

    static String normalize(String payload) {
        payload = payload.substring(1);
        payload = payload.substring(0, payload.length() - 1);
        return payload;
    }

    public void connect() {
        if (!withSockJS) {
            System.out.println("Not Sock JS");
            // MyDialog.info("connect is for SockJS");
            return;
        }
        System.out.println("Connecting...");
        String template = "[\"CONNECT\\naccept-version:1.1,1.0\\nheart-beat:10000,10000\\n\\n\\u0000\"]";
        sessionSend(template);
    }

    /**
     * for SockJS only
     */
    public void subscribe(String path) {
        if (!withSockJS) {
            System.out.println("Not Sock JS");
            // MyDialog.info("Subscribe is for SockJS");
            return;
        }
        if (subscribedTopics.get(path) != null) {
            System.out.println("Client has subscribed " + path);
            return;
        }
        System.out.println("Subscribe to topic: " + subscriptionPrefix + "/" + path);

        String template = "[\"SUBSCRIBE\\nid:sub-" + com.fajar.android.chatting1.util.StringUtil.randomNumber(100, 900) + "\\ndestination:/" + subscriptionPrefix + "/" + path +   "\\n\\n\\u0000\"]";
        sessionSend(template);
        subscribedTopics.put(path, "TOPIC");

    }

    private String sockJsPayload(String payload, String destination) {
       // String payload = MessageMapper.constructMessage(getClientId(), destination, message);
        try {
            payload = Commons.getObjectMapper().writeValueAsString(payload);
            payload = normalize(payload);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(AppClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(destination.startsWith("/") == false){
            destination = "/"+destination;
        }
        System.out.println("payload: " + payload);
        String sendingTemplate = "[\"MESSAGE\\ndestination:/app"+destination+"\\ncontent-type:application/json;charset=UTF-8\\nsubscription:sub-0\\nmessage-id:1itomv18-933\\n\\n" + payload + "\\u0000\"]";
        System.out.println("sendingTemplate: " + sendingTemplate);
        return sendingTemplate;
    }

    public void sendMessage(String message, String destination, MyConsumer<String> callback) {

        String payload;
        if (withSockJS) {
            payload = sockJsPayload(message, destination);
        } else {
            payload = MessageMapper.constructMessage(getClientId(), destination, message);
        }
        boolean messageSent = sessionSend(payload);
        if (messageSent) {
            if(null != callback) {
                callback.accept(message, null);
            }
        }
    }

    private boolean sessionSend(String text) {
        try {
            Logs.log("SESSION SEND:", text);
            thisSession.getBasicRemote().sendText(text);
            return true;
        } catch (Exception ex) {
            Logs.log("Error Session Sending: ", ex);
            ex.printStackTrace();
            Logger.getLogger(AppClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        thisSession = session;
        Logs.log("Opened Session : " + session.getId());
        if (onOpenCallback == null) {
            onOpenCallback = (_session, exception) -> {
                connect();
            };
        }

        onOpenCallback.accept(session, null);
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        System.out.println("On Error:");
        thr.printStackTrace();
//        MyDialog.error(thr.getMessage());
//        super.onError(session, thr); //To change body of generated methods, choose Tools | Templates.
    }


    @OnMessage
    public void onMessage(final String message, Session session) {
         onMessageTask().execute(message, session);
    }

    private boolean isSockJsMessageResponse(String raw) {
        return raw.startsWith("a[\"MESSAGE");
    }

    private void handleOnConnect(Session session, String message) {
        if (null == onConnectCallback) {
            onConnectCallback = (sess, error) -> {
                Logs.log("Empty On Connect Callback");
            };
        }
        setConnected(true);
        onConnectCallback.accept(session, null);
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {

        logger.info(String.format("Session %s close because of %s", session.getId(), closeReason));
        latch.countDown();

    }

    public boolean isConnected() {
        return connected;
    }

    public void start() {
        if(isConnected()){
            return;
        }

        latch = new CountDownLatch(1);
        ClientManager client = ClientManager.createClient();

        try {
            System.out.println("client will connect to server: " + wsURL);
            client.connectToServer(this, new URI(wsURL));

            latch.await();

        } catch (Exception e) {
//            MyDialog.error(e.getMessage());
            setConnected(false);
            throw new RuntimeException(e);

        }
    }

    public void disconnect() {
//        if (MyDialog.confirm(null, "Want to Disconnect?")) {
//            if (withSockJS) {
//                sessionSend("[\"DISCONNECT\\n\\n\\u0000\"]");
//            } else {
//                sessionSend("quit");
//            }
//
//        }

    }

    public boolean isWithSockJs() {
        return withSockJS;
    }

    private Session handleOnMessageOnBackground(String message, Session session){
        logger.info("Received Message: " + message);
        try {
            WebResponse messagePayload = null;

            if (isWithSockJs() && isSockJsMessageResponse(message)) {
                messagePayload = MessageMapper.parseSockJsResponse(message, WebResponse.class);
            } else {
                messagePayload = MessageMapper.getMessage(message);
            }

            if (null != CustomMessageHandler && messagePayload != null) {
                CustomMessageHandler.handleOnMessage(messagePayload, this);
            } else if (null != CustomMessageHandler) {
                CustomMessageHandler.handleOnMessage(message, this);
            }

            if (message.startsWith("a[\"CONNECTED")) {
                handleOnConnect(session, message);
            }
        } catch (Exception e) {

            Logs.log("Error on MESSAGE: ", e);
            e.printStackTrace();


        }
        return session;
    }

    private AsyncTask<Object, Void, Session> onMessageTask(){
        return new AsyncTask<Object, Void, Session>() {
            @Override
            protected Session doInBackground(Object... params) {

                return handleOnMessageOnBackground((String)params[0],(Session) params[1]);
            }
        };
    }
}