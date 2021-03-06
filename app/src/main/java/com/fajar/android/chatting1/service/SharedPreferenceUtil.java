package com.fajar.android.chatting1.service;

import android.content.SharedPreferences;

import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.ChattingData;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class SharedPreferenceUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();


    public static WebResponse getChattingPartnersData(SharedPreferences sharedPreferences) {
        WebResponse webResponse = getObject(sharedPreferences, "chatting_partners", WebResponse.class);

        return webResponse;
    }

    public static void putChattingPartnersData(SharedPreferences sharedPreferences, WebResponse data) {
        putObject(sharedPreferences, "chatting_partners", WebResponse.builder().chattingPartnerList(data.getChattingPartnerList()).build());
    }

    public static String getValue(SharedPreferences sharedPreferences, String key) {
        try {
            return sharedPreferences.getString(key, "");
        } catch (Exception e) {
            Logs.log("Error get ", key, " from sharedPreference");
            return "";
        }
    }

    public static <T extends Serializable> T getObject(SharedPreferences sharedPreferences, String key, Class<T> _class) {
        String value = getValue(sharedPreferences, key);
        try {
            return objectMapper.readValue(value, _class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void putObject(SharedPreferences sharedPreferences, String key, Serializable object) {
        try {
            String value = objectMapper.writeValueAsString(object);
            putString(sharedPreferences, key, value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void setWebsocketConnected(SharedPreferences sharedPreferences, boolean connected){
        putString(sharedPreferences, "websocket_connection", String.valueOf(connected));
    }

    public static boolean isWebsocketConnected(SharedPreferences sharedPreferences){
        String value = getValue(sharedPreferences, "websocket_connection");
        return value.equals("true");
    }

    /**
     * put to shared reference, null value will remove key
     *
     * @param sharedPreferences
     * @param key
     * @param value
     */
    public static void putString(SharedPreferences sharedPreferences, String key, String value) {
        if(null == sharedPreferences){ return; }
        // ThreadUtil.runAndStart(() -> {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (null == key) {//|| key.isEmpty()) {
            editor.remove(key);
            Logs.log("remove key: ", key);
        }
        Logs.log("PUT key: ", key);
        editor.putString(key, value);
        editor.commit();

        Logs.log("end Put key: ", key);
        //  });

    }

    public static void remove(SharedPreferences sharedPreferences, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
        Logs.log("Remove Shared Preferences: ", key);
        String deletedValue = (sharedPreferences.getString(key, "NULL"));
        Logs.log("Deleted Value: ", deletedValue);

    }

    public static void putRequestKey(SharedPreferences sharedPreferences, String requestKey) {
        putString(sharedPreferences, "request_key", requestKey);
    }

    public static String getRequestKey(SharedPreferences sharedPreferences) {
        return getValue(sharedPreferences, "request_key");
    }

    public static void putSessionData(SharedPreferences sharedpreferences, WebResponse response) {

        putObject(sharedpreferences, "session_data", response);
    }

    public static WebResponse getSessionData(SharedPreferences sharedPreferences) {
        return getObject(sharedPreferences, "session_data", WebResponse.class);
    }

    public static void removeChattingPartnersData(SharedPreferences sharedpreferences) {
        remove(sharedpreferences, "chatting_partners");
    }

    public static void setChattingData(SharedPreferences sharedPreferences, RegisteredRequest partner, WebResponse webResponse) {
        ChattingData chattingData = new ChattingData();
        chattingData.setPartner(partner);
        chattingData.setMessages(webResponse.getMessageList());
        putObject(sharedPreferences, "chatting_data_" + partner.getRequestId(), chattingData);
    }

    public static void setChattingData(SharedPreferences sharedPreferences, String partnerId, ChattingData chattingData) {

        putObject(sharedPreferences, "chatting_data_" + partnerId, chattingData);
    }

    public static ChattingData getChattingData(SharedPreferences sharedPreferences, String partnerId) {
        return getObject(sharedPreferences, "chatting_data_" + partnerId, ChattingData.class);
    }

    public static void addChattingMessage(SharedPreferences sharedPreferences, RegisteredRequest partner, Message message, boolean unread) {
        ChattingData chattingData = getChattingData(sharedPreferences, partner.getRequestId());
        if (null == chattingData) {
            chattingData = new ChattingData();
            chattingData.setPartner(partner);
        }
        chattingData.addMessage(message);
        Logs.log("addChattingMessage:", unread);
        if (unread) {

            chattingData.addUnreadMessage();
        } else {
            chattingData.setUnreadMessages(0);
        }
        setChattingData(sharedPreferences, partner.getRequestId(), chattingData);
    }

    public static void addChattingMessage(SharedPreferences sharedPreferences, String partnerId, Message message, boolean unread) {
        Logs.log("addChattingMessage partnerId: ", partnerId, "unread: ", unread);
        RegisteredRequest partner = getChattingPartner(sharedPreferences, partnerId);
        addChattingMessage(sharedPreferences, partner, message, unread);

    }

    public static void removeUnreadMessage(SharedPreferences sharedPreferences, RegisteredRequest partner) {
        ChattingData chattingData = getChattingData(sharedPreferences, partner.getRequestId());
        if (null == chattingData) {
            return;
        }
        chattingData.removeUnreadMessage();
        setChattingData(sharedPreferences, partner.getRequestId(), chattingData);
    }

    public static RegisteredRequest getChattingPartner(SharedPreferences sharedPreferences, String partnerId) {
        WebResponse response = getChattingPartnersData(sharedPreferences);
        for (RegisteredRequest partner : response.getChattingPartnerList()) {

            if (partnerId.equals(partner.getRequestId())) {
                return partner;
            }

        }

        return null;
    }
}
