package com.fajar.android.chatting1.handlers;

import android.os.AsyncTask;

import com.fajar.android.chatting1.activities.fragments.HomeFragment;
import com.fajar.android.chatting1.activities.fragments.SearchFragment;
import com.fajar.android.chatting1.service.AccountService;
import com.fajar.android.chatting1.service.ChattingService;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.WebResponse;


public class SearchFragmentHandler extends BaseHandler<SearchFragment> {

    private static SearchFragmentHandler instance;


    private SearchFragmentHandler(SearchFragment f){
        super(f);
    }

    public static SearchFragmentHandler getInstance(SearchFragment fragment){
        if(null == instance) instance = new SearchFragmentHandler(fragment);
        return instance;
    }

    public void getPartner(String partnerId, String requestKey, MyConsumer<WebResponse> callback){
        startLoading();
        getPartnerTask(callback).execute(partnerId, requestKey);
    }

    public void initializeChat(String partnerId, String requestKey, MyConsumer<WebResponse> myConsumer) {
        startLoading();
        initializeChatTask(myConsumer).execute(partnerId, requestKey);
    }

    private AsyncTask<String, Void, WebResponse> initializeChatTask(MyConsumer<WebResponse> callback) {
        return new AsyncTask<String, Void, WebResponse>() {

            private Exception exception;

            @Override
            protected WebResponse doInBackground(String... strings) {
                try {
                    return ChattingService.instance().initializeChat(strings[0], strings[1]);
                } catch (Exception e) {
                    this.exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(WebResponse webResponse) {
                Logs.log("onPostExecute initializeChatTask");
                callback.accept(webResponse, exception);
            }
        };
    }

    private AsyncTask<String, Void, WebResponse> getPartnerTask(MyConsumer<WebResponse> callback) {
        return new AsyncTask<String, Void, WebResponse>() {

            private Exception exception;

            @Override
            protected WebResponse doInBackground(String... strings) {
                try {
                    return ChattingService.instance().getPartner(strings[0], strings[1]);
                } catch (Exception e) {
                    this.exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(WebResponse webResponse) {

                Logs.log("onPostExecute getPartnerTask");
                callback.accept(webResponse, exception);
            }
        };
    }

}
