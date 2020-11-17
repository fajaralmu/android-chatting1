package com.fajar.android.chatting1.constants;

public class Endpoints {

//    public static final String HOST = "https://realtime-videocall.herokuapp.com/";
    public static final String HOST = "http://192.168.43.241:8080/livestreaming/";
    public static final String ENDPOINT_REGISTER = HOST+"api/stream/register";
    public static final String ENDPOINT_GET_USER = HOST+"api/stream/getuser";
    public static final String ENDPOINT_GET_PARTNER = HOST+"api/chatting/partnerinfo/"; // add /{partnerID}
    public static final String ENDPOINT_INVALIDATE_USER = HOST+"api/stream/invalidate";

}
