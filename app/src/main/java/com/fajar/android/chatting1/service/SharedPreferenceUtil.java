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

    private static final String SHARED_AGENDA = "shared_agenda";
    private static final String SHARED_NEWS = "shared_news";
//    private static final String IS_EXIST_SHARED_AGENDA = "is_shared_agenda_exist";
//    private static final String IS_EXIST_SHARED_NEWS = "is_shared_news_exist";
//    private static final String EXIST = "EXIST";
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void storeAgendaData(SharedPreferences sharedPreferences, PostResponse agendaData){
        try {
            putString(sharedPreferences, SHARED_AGENDA, agendaData == null ? null: objectMapper.writeValueAsString(agendaData));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAgendaExist(SharedPreferences sharedPreferences){
        boolean exist =getValue(sharedPreferences, SHARED_AGENDA).equals("") == false;
        Logs.log("agenda exist in shared preference: ", exist);
        return exist;
    }

    public static PostResponse getAgendaData(SharedPreferences sharedPreferences){
        String rawValue = getValue(sharedPreferences, SHARED_AGENDA);

        if(null == rawValue || "".equals(rawValue.trim())){
            Logs.log("getAgendaData return null");
            return null;
        }
        try{
            Logs.log("parsing agenda data");
            PostResponse response = objectMapper.readValue(rawValue, PostResponse.class);
            if (response.getPosts() instanceof List) {
                List rawPosts = (List) response.getPosts();
                List<Post> posts = (MapUtil.convertMapList(rawPosts, Post.class));
                response.setAgendas(posts);

            }
            Logs.log("success return agenda data");
            return response;
        }catch (Exception e){
            Logs.log("ERROR get agenda from shared preferences: ", e);
            storeAgendaData(sharedPreferences, null);
        }

        return null;
    }


    ///////////////////////////// NEWS//////////////////////////////
    public static void storeNewsData(SharedPreferences sharedPreferences, PostResponse agendaData){
        try {
            Logs.log("storeNewsData page: ", agendaData.getCurrentPageInt2());
            putString(sharedPreferences, SHARED_NEWS,agendaData == null ? null: objectMapper.writeValueAsString(agendaData));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNewsExist(SharedPreferences sharedPreferences){
        boolean exist = getValue(sharedPreferences, SHARED_NEWS).equals("") == false;
        Logs.log("news exist in shared preference: ", exist);
        return exist;
    }

    public static PostResponse getNewsData(SharedPreferences sharedPreferences){
        String rawValue = getValue(sharedPreferences, SHARED_NEWS);

        if(null == rawValue || "".equals(rawValue.trim())){
            Logs.log("getNewsData return null");
            return null;
        }
        try{
            Logs.log("parsing news data");
            PostResponse response = objectMapper.readValue(rawValue, PostResponse.class);
            if ( response.getPosts() instanceof Map) {
                String json = objectMapper.writeValueAsString(response.getPosts());
                NewsPost newsPost = objectMapper.readValue(json, NewsPost.class);
                response.setNewsPost(newsPost);
            }
            Logs.log("success return news data");
            return response;
        }catch (Exception e){
            Logs.log("ERROR get news from shared preferences: ", e);
            storeNewsData(sharedPreferences, null);
        }

        return null;
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

    public static void putSessionData(SharedPreferences sharedpreferences, WebResponse response) {

        putObject(sharedpreferences, "session_data", response);
    }

    public static WebResponse getSessionData(SharedPreferences sharedPreferences){
        return getObject(sharedPreferences, "session_data", WebResponse.class);
    }
}
