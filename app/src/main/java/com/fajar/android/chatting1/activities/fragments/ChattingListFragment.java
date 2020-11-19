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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.activities.HomeActivity;
import com.fajar.android.chatting1.components.ChatListItem;
import com.fajar.android.chatting1.constants.Actions;
import com.fajar.android.chatting1.constants.SharedPreferencesConstants;
import com.fajar.android.chatting1.handlers.ChattingListFragmentHandler;
import com.fajar.android.chatting1.models.ChattingData;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.AlertUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

import java.util.ArrayList;
import java.util.List;

public class ChattingListFragment extends BaseFragment<ChattingListFragmentHandler> {

    private LinearLayout chattingListLayout, infoLabelWrapper;
    private TextView infoLabel;
    private ImageButton buttonLoadChattingList;
    private ImageView buttonCloseInfoLabel;
    private List<ChatListItem> chatListItems = new ArrayList<>();

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
        chattingListLayout = findById(R.id.chat_list_layout);
        buttonLoadChattingList = findById(R.id.button_load_chatting_list);
        loader = view.findViewById(R.id.loader_chatting_list);
        infoLabel = findById(R.id.chatting_list_info);
        infoLabelWrapper = findById(R.id.chatting_list_info_label_wrapper);
        buttonCloseInfoLabel = findById(R.id.button_chatting_list_info_label_close);
    }

    private void initEvents() {
        setLoaderGone();
        chatListItems.clear();
        infoLabelWrapper.setVisibility(View.GONE);
        chattingListLayout.removeAllViews();
        buttonLoadChattingList.setOnClickListener(loadChattingPartnerListener());
        buttonCloseInfoLabel.setOnClickListener((v) -> {
            infoLabelWrapper.setVisibility(View.GONE);
        });
        //  if(!checkInitialAction()) {
        checkChattingPartners();
        //   }
        checkInitialAction();

    }

    private void addChatListItem(ChatListItem chatListItem){
        chatListItems.add(chatListItem);
    }

    private boolean checkInitialAction() {
        doByAction(getInitialAction());
        return false;
    }

    @Override
    public void doByAction(Actions action) {
        Logs.log("Chatting List Fragment doByAction: ", action);
        if (action.equals(Actions.RELOAD)) {
//            AlertUtil.YesAlert(getActivity(), "Info", "You have new chatting partner.. Please Reload");
            // return true;
            setInfoLabelText("You have new chatting partner.. Please Reload");
        }
    }

    private void checkChattingPartners() {
        WebResponse chattingPartnersData = SharedPreferenceUtil.getChattingPartnersData(sharedpreferences);
        if (null != chattingPartnersData) {
            populateChattingPartners(chattingPartnersData);
        } else {
            showInfoEmpty();
        }
    }

    private void getChattingPartners() {
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
            SharedPreferenceUtil.removeChattingPartnersData(sharedpreferences);
            return;
        }
        SharedPreferenceUtil.putChattingPartnersData(sharedpreferences, response);

        List partners = response.getResultList();
        populateChattingListLayout(partners);

    }

    private void populateChattingListLayout(List partners) {
        for (Object object :
                partners) {
            RegisteredRequest partner = (RegisteredRequest) object;
            ChattingData chattingData = SharedPreferenceUtil.getChattingData(sharedpreferences, partner.getRequestId());
            ChatListItem chatListItem = new ChatListItem((RegisteredRequest) partner, false, getActivity(), chattingData);
            chattingListLayout.addView(chatListItem);
            addChatListItem(chatListItem);
        }
    }

    private void showInfoEmpty() {
        setInfoLabelText("No Partner yet. Please Search for Partner to Chat With");

    }

    private void setInfoLabelText(String message) {
        getActivity().runOnUiThread(() -> {
            infoLabelWrapper.setVisibility(View.VISIBLE);
            infoLabel.setText(message);
            Logs.log(" setInfoLabelText(String message)", infoLabel.getText());
        });
    }
}
