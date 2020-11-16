package com.fajar.android.chatting1.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.Serializable;

import com.fajar.android.chatting1.constants.Extras;
import com.fajar.android.chatting1.service.AccountService;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.Navigate;
import com.fajar.android.chatting1.R;
import com.fajar.livestreaming.dto.WebResponse;

public class WelcomingScreenActivity extends BaseActivity {


    public WelcomingScreenActivity() {
        super(R.layout.activity_splash_screen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIntentExras();
        checkUser();
    }


    private void checkIntentExras() {
        Intent intent = getIntent();
        //if exist
        Serializable exitExtras = intent.getSerializableExtra(Extras.EXIT_APP_KEY.value);
        Logs.log("exitExtras: ", exitExtras);
        if (null != exitExtras && exitExtras.equals(Extras.EXIT_APP_VALUE)) {
            Intent mainActivity = new Intent(Intent.ACTION_MAIN);
            mainActivity.addCategory(Intent.CATEGORY_HOME);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivity);
            finish();
            int id = android.os.Process.myPid();
            android.os.Process.killProcess(id);
        }
    }

    @Override
    protected void initComponent() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onBackPressed() {
        finishActivity(1);
        System.exit(1);
    }

    private void checkUser() {
        String existingRequestKey = SharedPreferenceUtil.getValue(sharedpreferences, "request_key");
        if (existingRequestKey.isEmpty()) {
            goToHomePage();
            return;
        }
        getUser().execute(existingRequestKey);
    }

    private void goToHomePage() {
        Navigate.navigate(this, HomeActivity.class);
//        new GoHome().execute(this);
    }


    private class GoHome extends AsyncTask<Context, String, String> {

        @Override
        protected String doInBackground(Context... contexts) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Navigate.navigate(contexts[0], HomeActivity.class);
            return null;
        }
    }

    private void handleGetUser(WebResponse response) {
        if (null == response) {
            goToHomePage();
            return;
        }

        SharedPreferenceUtil.putObject(sharedpreferences, "session_data", response);
        goToHomePage();
    }

    private AsyncTask<String, Void, WebResponse> getUser() {
        return new AsyncTask<String, Void, WebResponse>() {
            @Override
            protected WebResponse doInBackground(String... strings) {
                try {
                    WebResponse response = AccountService.instance().getUser(strings[0]);

                    return response;
                } catch (Exception e) {
                }
                Logs.log("Get user returns null");
                return null;
            }

            @Override
            protected void onPostExecute(WebResponse response) {
                handleGetUser(response);
            }
        };
    }
}
