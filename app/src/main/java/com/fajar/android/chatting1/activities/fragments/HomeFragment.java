package com.fajar.android.chatting1.activities.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.activities.WelcomingScreenActivity;
import com.fajar.android.chatting1.constants.SharedPreferencesConstants;
import com.fajar.android.chatting1.handlers.HomeFragmentHandler;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.AlertUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.Navigate;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

public class HomeFragment extends BaseFragment<HomeFragmentHandler> {
    protected SharedPreferences sharedpreferences;

    private TextView accountName, accountId, accountRegisteredDate;
    private Button buttonInvalidate;

    public HomeFragment() {
        setHandler(HomeFragmentHandler.getInstance(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedpreferences = super.getSharedPreferences();
        initComponents();
        initEvents();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void startLoading() {
        buttonInvalidate.setVisibility(View.GONE);
        super.startLoading();
    }

    @Override
    public void stopLoading() {
        buttonInvalidate.setVisibility(View.VISIBLE);
        super.stopLoading();
    }

    private void initComponents() {
        accountId = findById(R.id.account_id);
        accountName = findById(R.id.account_name);
        accountRegisteredDate = findById(R.id.account_date);
        buttonInvalidate = findById(R.id.button_invalidate);
        loader = findById(R.id.loader_home);
    }

    private void initEvents() {
        Logs.log("Home Fragment initEvents");

        buttonInvalidate.setOnClickListener(this::invalidate);
        
        if (SharedPreferenceUtil.getValue(sharedpreferences, "session_data").isEmpty() == false) {
            WebResponse sessionData = SharedPreferenceUtil.getSessionData(sharedpreferences);
            populateSessionData(sessionData);
        } else {
           goToWelcomingScreen();
        }
    }
    
    private void invalidate(View v){
        AlertUtil.confirm(getActivity(), "Invalidate Session?",  this::invalidateConfirmed);
    }

    private void invalidateConfirmed(DialogInterface dialogInterface, int i) {
        String requestKey = SharedPreferenceUtil.getRequestKey(sharedpreferences);
        handler.invalidate(requestKey, this::handleInvalidate);
    }

    private void handleInvalidate(WebResponse response, Exception e) {
        if(e != null){
            AlertUtil.YesAlert(getActivity(), "Error Invalidate", e.getMessage());
        }

        goToWelcomingScreen();
    }

    private void goToWelcomingScreen() {
        Navigate.navigate(getActivity(), WelcomingScreenActivity.class);
    }

    private void populateSessionData(WebResponse sessionData) {
        RegisteredRequest account = sessionData.getRegisteredRequest();
        accountName.setText(account.getUsername());
        accountId.setText(account.getRequestId());
        accountRegisteredDate.setText(account.getCreated().toString());
    }


}
