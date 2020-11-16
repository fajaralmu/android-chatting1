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

    public void register(String username, MyConsumer<WebResponse> callback){
        fragment.startLoading();
        registerTask(callback).execute(username);

    }

    private AsyncTask<String, Void, WebResponse> registerTask(final MyConsumer<WebResponse> callback){
        return new AsyncTask<String, Void, WebResponse>() {

            private Exception exception;
            @Override
            protected WebResponse doInBackground(String... strings) {
                try {
                    return AccountService.instance().register(strings[0]);
                } catch (Exception e){
                    this.exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(WebResponse webResponse) {
                fragment.stopLoading();
                Logs.log("onPostExecute register task");
                callback.accept(webResponse, exception);
            }
        };
    }
}
