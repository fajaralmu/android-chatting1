package com.fajar.android.chatting1.constants;

public enum SharedPreferencesConstants {
    KEY_CONTENT ( "key_content"),
    SHARED_CONTENT ("SHARED_CONTENT");

    public String value;
    private SharedPreferencesConstants(String value){
       this. value = value;
    }
}
