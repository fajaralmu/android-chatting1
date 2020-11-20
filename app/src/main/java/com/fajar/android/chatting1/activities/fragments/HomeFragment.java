package com.fajar.android.chatting1.activities.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.activities.HomeActivity;
import com.fajar.android.chatting1.activities.WelcomingScreenActivity;
import com.fajar.android.chatting1.handlers.HomeFragmentHandler;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.AlertUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.Navigate;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

import java.util.Date;

public class HomeFragment extends BaseFragment<HomeFragmentHandler> {

    private TextView accountName, accountId, accountRegisteredDate, labelWebsocketConnectionStatus, labelWebsocketUpdatedDate;
    private Button buttonInvalidate, buttonResetWebsocket;
    private LinearLayout buttonPanels;

    public HomeFragment() {
        setHandler(HomeFragmentHandler.getInstance(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        setSharedpreferences();
        initComponents();
        initEvents();
    }

    private void initComponents() {
        accountId = findById(R.id.account_id);
        accountName = findById(R.id.account_name);
        accountRegisteredDate = findById(R.id.account_date);
        labelWebsocketConnectionStatus = findById(R.id.label_websocket_connection);
        labelWebsocketUpdatedDate = findById(R.id.label_websocket_connection_date);
        buttonPanels = findById(R.id.home_button_panels);
        buttonResetWebsocket = findById(R.id.button_refresh_websocket_connection);
        buttonInvalidate = findById(R.id.button_invalidate);
        loader = findById(R.id.loader_home_fragment);

    }

    private void initEvents() {

        setLoaderGone();
        buttonInvalidate.setOnClickListener(this::invalidate);
        buttonResetWebsocket.setOnClickListener(this::refreshWebsocketConnection);

        if (SharedPreferenceUtil.getValue(sharedpreferences, "session_data").isEmpty() == false) {
            WebResponse sessionData = SharedPreferenceUtil.getSessionData(sharedpreferences);
            populateSessionData(sessionData);
        } else {
            relaunchApp();
        }


        updateWebsocketConnectionInfo();

    }

    private void refreshWebsocketConnection(View view) {
        AlertUtil.confirm(getActivity(), "Refresh Websocket?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    SharedPreferenceUtil.setWebsocketConnected(sharedpreferences, false);
                    updateWebsocketConnectionInfo();
                    ((HomeActivity) getActivity()).initializeWebsocket();
                }catch (Exception e){  }
            }
        });
    }


    public void updateWebsocketConnectionInfo(){
        boolean connected = (SharedPreferenceUtil.isWebsocketConnected(sharedpreferences));
        try {
            componentHandler.postDelayed(()-> {
                labelWebsocketConnectionStatus.setText("WebSocket Connection:" + (connected ? "Connected" : "Disconnected"));
                if(getActivity() instanceof  HomeActivity) {
                    Date connectedDate = ((HomeActivity) getActivity()).getWebsocketConnectedAt();
                    labelWebsocketUpdatedDate.setText("updated at " + (connectedDate == null ? "-":connectedDate.toString()));
                } else {
                    Logs.log("Current activity not instanceof HomeActivity: ", getActivity().getClass());
                }
                Logs.log("labelWebsocketConnectionStatus : ", labelWebsocketConnectionStatus.getText());

            }, 1);
        }catch (Exception e){

        }
    }

    private void invalidate(View v) {
        AlertUtil.confirm(getActivity(), "Invalidate Session?", this::invalidateConfirmed);
    }

    private void invalidateConfirmed(DialogInterface dialogInterface, int i) {
        buttonPanels.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        handler.invalidate(getRequestKey(), this::handleInvalidate);
    }

    private void handleInvalidate(WebResponse response, Exception e) {
        String title = e == null ? "Info" : "Error Invalidate";
        String content = e == null ? "Invalidated" : e.getMessage();
        AlertUtil.YesAlert(getActivity(), title, content, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopLoading();
                if (e != null) {
                    buttonPanels.setVisibility(View.VISIBLE);
                } else {
                    relaunchApp();
                    SharedPreferenceUtil.removeChattingPartnersData(sharedpreferences);
                }
            }
        });
    }


    private void relaunchApp() {
        Intent mStartActivity = new Intent(getActivity(), WelcomingScreenActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private void populateSessionData(WebResponse sessionData) {
        RegisteredRequest account = sessionData.getRegisteredRequest();
        accountName.setText(account.getUsername());
        accountId.setText(account.getRequestId());
        accountRegisteredDate.setText(account.getCreated().toString());
        Logs.log("user-agent:", account.getUserAgent());
    }


}
