package com.fajar.android.chatting1.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.fajar.android.chatting1.constants.SharedPreferencesConstants;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.Navigate;

public abstract class BaseActivity extends AppCompatActivity {

    protected final int LAYOUT_ID;
    protected ProgressBar loader;
    protected SharedPreferences sharedpreferences;
    public BaseActivity(int layoutId){
        LAYOUT_ID = layoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.log("Super onCreate LAYOUT_ID: ", LAYOUT_ID);
        try {


            setContentView(LAYOUT_ID);
            sharedpreferences = getSharedPreferences(SharedPreferencesConstants.SHARED_CONTENT.value, MODE_PRIVATE);

            initComponent();
            initEvent();
        }catch (Exception e){
            Logs.log("ERROR : ", e);
            e.printStackTrace();
            throw e;
        }
    }

    protected View.OnClickListener navigate(final Class<? extends Activity>  activityClass) {

        final Context context = this;

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigate.navigate(context, activityClass);
            }
        };
    }

    public  void startLoading(){
        if(null == loader){
            return;
        }
        loader.setVisibility(View.VISIBLE);
    }

    public void stopLoading(){

        setLoaderGone();
    }

    protected void setLoaderGone(){
        if(null == loader){
            return;
        }
        loader.setVisibility(View.GONE);
    }
    protected abstract void initComponent();
    protected abstract void initEvent();
}
