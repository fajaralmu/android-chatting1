package com.fajar.android.chatting1.constants;

public class Endpoints {
    private static final boolean local =
            true
                    && false
                    ;
    static String host(){
        if(local){
            return  "http://"+HOST_ADDRESS+"/";
        }
        return "https://"+HOST_ADDRESS+"/";
    }
    public static final String HOST_LOCAL = "192.168.1.21:8080/livestreaming";
    public static final String HOST_ADDRESS = local ? HOST_LOCAL : "realtime-videocall.herokuapp.com";

//    public static final String HOST = "https://"+HOST_ADDRESS+"/";
    public static final String HOST = host();
    public static final String WS_HOST = "ws://"+HOST_ADDRESS+"/";
//    public static final String HOST = "http://192.168.1.21:8080/livestreaming/";
    public static final String ENDPOINT_REGISTER = HOST+"api/stream/register";
    public static final String ENDPOINT_GET_USER = HOST+"api/stream/getuser";
    public static final String ENDPOINT_GET_PARTNER = HOST+"api/chatting/partnerinfo/"; // + partnerID
    public static final String ENDPOINT_INITIALIZE_CHAT = HOST+"api/chatting/initialize/"; // + partnerID
    public static final String ENDPOINT_GET_CHATTING_PARTNERS = HOST+"api/chatting/chattinglist";
    public static final String ENDPOINT_GET_CHATTING_MESSAGES = HOST+"api/chatting/messages/"; // + partnerID
    public static final String ENDPOINT_SEND_CHATTING_MESSAGES = HOST+"api/chatting/send/"; // + partnerID
    public static final String ENDPOINT_INVALIDATE_USER = HOST+"api/stream/invalidate";

}
