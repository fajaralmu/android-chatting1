package com.fajar.android.chatting1.service;

import android.content.SharedPreferences;

import com.fajar.livestreaming.dto.WebResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fajar.android.chatting1.models.NewsPost;
import com.fajar.android.chatting1.models.Post;
import com.fajar.android.chatting1.models.PostResponse;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.MapUtil;
import com.fajar.android.chatting1.util.ThreadUtil;

public class SharedPreferenceUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();


    public static WebResponse getChattingPartnersData(SharedPreferences sharedPreferences){
        return getObject(sharedPreferences, "chatting_partners", WebResponse.class);
    }

    public static void putChattingPartnersData(SharedPreferences sharedPreferences, WebResponse data){
        putObject(sharedPreferences, "chatting_partners", data);
    }

    public static String getValue(SharedPreferences sharedPreferences, String key){
        try {
            return sharedPreferences.getString(key, "");
        }catch (Exception e){
            Logs.log("Error get ", key," from sharedPreference");
            return "";
        }
    }

    public static <T extends  Serializable> T getObject(SharedPreferences sharedPreferences, String key, Class<T> _class){
        String value = getValue(sharedPreferences, key);
        try {
            return objectMapper.readValue(value, _class);
        }catch (Exception e){
            return null;
        }
    }

    public static void putObject(SharedPreferences sharedPreferences, String key, Serializable object){
        try {
            String value = objectMapper.writeValueAsString(object);
            putString(sharedPreferences, key, value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * put to shared reference, null value will remove key
     * @param sharedPreferences
     * @param key
     * @param value
     */
    public  static void putString(SharedPreferences sharedPreferences, String key, String value){
        ThreadUtil.runAndStart(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (null == key) {//|| key.isEmpty()) {
                editor.remove(key);
                Logs.log("remove key: ", key);
            }
            Logs.log("PUT key: ", key);
            editor.putString(key, value);
            editor.commit();

            Logs.log("end Put key: ", key);
        });

    }

    public static void putRequestKey(SharedPreferences sharedPreferences, String requestKey){
        putString(sharedPreferences, "request_key", requestKey);
    }

    public static String getRequestKey(SharedPreferences sharedPreferences){
        return getValue(sharedPreferences, "request_key");
    }

    public static void putSessionData(SharedPreferences sharedpreferences, WebResponse response) {

        putObject(sharedpreferences, "session_data", response);
    }

    public static WebResponse getSessionData(SharedPreferences sharedPreferences){
        return getObject(sharedPreferences, "session_data", WebResponse.class);
    }
}
