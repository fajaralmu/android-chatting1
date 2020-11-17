package com.fajar.android.chatting1.activities.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.activities.HomeActivity;
import com.fajar.android.chatting1.components.ChatListItem;
import com.fajar.android.chatting1.constants.SharedPreferencesConstants;
import com.fajar.android.chatting1.handlers.ChattingListFragmentHandler;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.AlertUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

import java.util.List;

public class ChattingListFragment extends BaseFragment<ChattingListFragmentHandler> {

    private LinearLayout chattingListLayout;
    private ImageButton buttonLoadChattingList;

    public ChattingListFragment() {

        setHandler(ChattingListFragmentHandler.getInstance(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chatting_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setSharedpreferences();
        initComponents();
        initEvents();
        super.onViewCreated(view, savedInstanceState);
    }


    private void initComponents() {
        chattingListLayout =  findById(R.id.chat_list_layout);
        buttonLoadChattingList = findById(R.id.button_load_chatting_list);
        loader =  view.findViewById(R.id.loader_chatting_list);
    }

    private void initEvents() {
        setLoaderGone();
        chattingListLayout.removeAllViews();
        buttonLoadChattingList.setOnClickListener(loadChattingPartnerListener());
        
        checkChattingPartners();
    }

    private void checkChattingPartners() {
        WebResponse chattingPartnersData = SharedPreferenceUtil.getChattingPartnersData(sharedpreferences);
        if(null != chattingPartnersData){
            populateChattingPartners(chattingPartnersData);
        }
    }

    private void getChattingPartners(){
        loader.setVisibility(View.VISIBLE);
        chattingListLayout.removeAllViews();
        handler.getChattingPartners(getRequestKey(), this::handleChattingPartners);
    }
    private void handleChattingPartners(WebResponse response, Exception e) {
        stopLoading();
        buttonLoadChattingList.setVisibility(View.VISIBLE);
        if (e != null) {
            AlertUtil.ErrorAlert(getActivity(), e);
            return;
        }

        populateChattingPartners(response);

    }
    private View.OnClickListener loadChattingPartnerListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLoadChattingList.setVisibility(View.GONE);
                getChattingPartners();
            }
        };
    }

    private void populateChattingPartners(WebResponse response) {
        chattingListLayout.removeAllViews();
        if (response.getResultList().size() == 0) {
            showInfoEmpty();
            return;
        }
        SharedPreferenceUtil.putChattingPartnersData(sharedpreferences, response);
        List partners = response.getResultList();
        for (Object partner :
                partners) {
            ChatListItem chatListItem = new ChatListItem((RegisteredRequest) partner, false, getActivity());
            chattingListLayout.addView(chatListItem);
        }
    }

    private void showInfoEmpty() {
        TextView textView = new TextView(getActivity());
        textView.setText("No Partner yet.");
        chattingListLayout.addView(textView);
    }


}
