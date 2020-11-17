package com.fajar.android.chatting1.handlers;

import android.os.AsyncTask;

import com.fajar.android.chatting1.activities.fragments.BaseFragment;
import com.fajar.android.chatting1.service.AccountService;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.WebResponse;


public class HomeFragmentHandler extends BaseHandler {

    private static HomeFragmentHandler instance;

    private HomeFragmentHandler(BaseFragment f){
        super(f);
    }

    public static HomeFragmentHandler getInstance(BaseFragment fragment){
        if(null == instance) instance = new HomeFragmentHandler(fragment);
        return instance;
    }




}
