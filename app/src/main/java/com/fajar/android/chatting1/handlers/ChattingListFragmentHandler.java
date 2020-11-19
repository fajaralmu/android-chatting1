package com.fajar.android.chatting1.handlers;

import android.os.AsyncTask;

import com.fajar.android.chatting1.activities.fragments.ChattingListFragment;
import com.fajar.android.chatting1.activities.fragments.HomeFragment;
import com.fajar.android.chatting1.service.AccountService;
import com.fajar.android.chatting1.service.ChattingService;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.MapUtil;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChattingListFragmentHandler extends BaseHandler<ChattingListFragment> {

    private static ChattingListFragmentHandler instance;


    private ChattingListFragmentHandler(ChattingListFragment f){
        super(f);
    }

    public static ChattingListFragmentHandler getInstance(ChattingListFragment fragment){
        if(null == instance) instance = new ChattingListFragmentHandler(fragment);
        return instance;
    }


    public void getChattingPartners(String requestKey, MyConsumer<WebResponse> callback) {
        Logs.log("getChattingPartners");
        startLoading();
        getChattingPartnersTask(callback).execute(requestKey);
    }

    private AsyncTask<String, Void, WebResponse> getChattingPartnersTask(MyConsumer<WebResponse> callback) {
        return new AsyncTask<String, Void, WebResponse>() {

            private Exception exception;

            @Override
            protected WebResponse doInBackground(String... strings) {
                try {
                    return ChattingService.instance().getChattingPartners(strings[0]);
                } catch (Exception e) {
                    this.exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(WebResponse webResponse) {
           //     stopLoading();
                Logs.log("onPostExecute getChattingPartnersTask");

                //convert data type from Map to RegisteredRequest.class


                callback.accept(webResponse, exception);
            }
        };
    }
}
