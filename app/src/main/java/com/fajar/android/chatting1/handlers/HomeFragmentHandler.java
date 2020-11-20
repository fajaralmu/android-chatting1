package com.fajar.android.chatting1.handlers;

import android.os.AsyncTask;

import com.fajar.android.chatting1.activities.fragments.HomeFragment;
import com.fajar.android.chatting1.service.AccountService;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.WebResponse;


public class HomeFragmentHandler extends BaseHandler<HomeFragment> {

    private static HomeFragmentHandler instance;



    private HomeFragmentHandler(HomeFragment f){
        super(f);
    }



    public static HomeFragmentHandler getInstance(HomeFragment fragment){
        if(null == instance) instance = new HomeFragmentHandler(fragment);
        return instance;
    }


    public void invalidate(String requestKey, MyConsumer<WebResponse> callback) {
        Logs.log("invalidate session: ",requestKey);
        startLoading();
        invalidateTask(callback).execute(requestKey);
    }

    private AsyncTask<String, Void, WebResponse> invalidateTask(MyConsumer<WebResponse> callback) {
        return new AsyncTask<String, Void, WebResponse>() {

            private Exception exception;

            @Override
            protected WebResponse doInBackground(String... strings) {
                try {
                    return AccountService.instance().invalidateUser(strings[0]);
                } catch (Exception e) {
                    this.exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(WebResponse webResponse) {
           //     stopLoading();
                Logs.log("onPostExecute invalidte task");
                callback.accept(webResponse, exception);
            }
        };
    }
}
