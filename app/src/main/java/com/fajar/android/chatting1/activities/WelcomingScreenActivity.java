package com.fajar.android.chatting1.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.constants.Extras;
import com.fajar.android.chatting1.service.AccountService;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.AlertUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.Navigate;
import com.fajar.livestreaming.dto.ChattingData;
import com.fajar.livestreaming.dto.WebResponse;

import java.io.Serializable;
import java.util.List;

public class WelcomingScreenActivity extends BaseActivity {

    private LinearLayout registerForm;
    private EditText inputUsername;
    private Button buttonRegister;

    public WelcomingScreenActivity() {
        super(R.layout.activity_splash_screen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIntentExras();

        new ShowSplashTask().execute(this);
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
        loader = findViewById(R.id.splash_loader);
        registerForm = findViewById(R.id.register_form);
        buttonRegister = findViewById(R.id.button_register);
        inputUsername = findViewById(R.id.input_name_register);
    }

    @Override
    protected void initEvent() {

        registerForm.setVisibility(View.GONE);
        buttonRegister.setOnClickListener(this::register);

        inputUsername.setText(android.os.Build.MODEL.replace(" ", "_"));
    }

    @Override
    public void onBackPressed() {
        finishActivity(1);
        System.exit(1);
    }

    private void checkUser() {
        String existingRequestKey = SharedPreferenceUtil.getRequestKey(sharedPreferences);
        if (existingRequestKey.isEmpty()) {
            stopLoading();
            registerForm.setVisibility(View.VISIBLE);
            return;
        }
        getUser().execute(existingRequestKey);
    }

    private void goToHomePage() {
        Navigate.navigate(this, HomeActivity.class);
    }


    private class ShowSplashTask extends AsyncTask<Context, String, String> {

        @Override
        protected String doInBackground(Context... contexts) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            checkUser();
        }
    }

    private void register(View v) {
        if (inputUsername.getText().toString().isEmpty()) {
            AlertUtil.YesAlert(this, "Invalid Username");
            return;
        }
        startLoading();
        registerForm.setVisibility(View.GONE);
        registerTask().execute(inputUsername.getText().toString());
    }

    private void getUserCallback(WebResponse response) {
        if (null == response) {
            AlertUtil.YesAlert(this, "Please Register Your Account");
            stopLoading();
            registerForm.setVisibility(View.VISIBLE);
            return;
        }

        updateSession(response);
        goToHomePage();
    }

    private void registerCallback(WebResponse response, Exception e) {
        if (e != null) {
            AlertUtil.YesAlert(this, "Error Registering Account", e.getMessage());
            registerForm.setVisibility(View.VISIBLE);
            stopLoading();
            return;
        }
        SharedPreferenceUtil.putRequestKey(sharedPreferences, response.getMessage());
        updateSession(response);
        goToHomePage();
    }

    private void updateSession(WebResponse response) {
        SharedPreferenceUtil.putSessionData(sharedPreferences, response);
        SharedPreferenceUtil.putChattingPartnersData(sharedPreferences, response);
        List<ChattingData> chattingDataList = response.getChattingDataList();
        for(ChattingData chattingData:chattingDataList){
            try {
               SharedPreferenceUtil.setChattingData(sharedPreferences, chattingData.getPartner().getRequestId(), chattingData);
            }catch (Exception ex){
                Logs.log("ERROR SET chattingData:",ex);
            }
        }
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

                getUserCallback(response);
            }
        };
    }

    private AsyncTask<String, Void, WebResponse> registerTask() {
        return new AsyncTask<String, Void, WebResponse>() {

            private Exception exception;

            @Override
            protected WebResponse doInBackground(String... strings) {
                try {
                    return AccountService.instance().register(strings[0]);
                } catch (Exception e) {
                    this.exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(WebResponse webResponse) {

                Logs.log("onPostExecute register task");
                registerCallback(webResponse, exception);
            }
        };
    }
}
