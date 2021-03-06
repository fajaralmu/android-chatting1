package com.fajar.android.chatting1.activities.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.components.ChatListItem;
import com.fajar.android.chatting1.constants.Actions;
import com.fajar.android.chatting1.handlers.ChattingListFragmentHandler;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.AlertUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.ChattingData;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChattingListFragment extends BaseFragment<ChattingListFragmentHandler> {

    private LinearLayout chattingListLayout, infoLabelWrapper;
    private TextView infoLabel;
    private ImageButton buttonLoadChattingList;
    private ImageView buttonCloseInfoLabel;
    private Map<String, ChatListItem> chatListItems = new LinkedHashMap<>();

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

    private void addChatListItem(String partnerId, ChatListItem chatListItem){
        chatListItems.put(partnerId, chatListItem);
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
        updateChattingData(response);
        populateChattingPartners(response);
    }

    private void updateChattingData(WebResponse response) {
        List<ChattingData> chattingDataList = response.getChattingDataList();
        for(ChattingData chattingData:chattingDataList){
            try {
                SharedPreferenceUtil.setChattingData(sharedpreferences, chattingData.getPartner().getRequestId(), chattingData);
            }catch (Exception e){

            }
        }
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
        if (response.getChattingPartnerList().size() == 0) {
            showInfoEmpty();
            SharedPreferenceUtil.removeChattingPartnersData(sharedpreferences);
            return;
        }
        SharedPreferenceUtil.putChattingPartnersData(sharedpreferences, response);

        List<RegisteredRequest> partners = response.getChattingPartnerList();
        populateChattingListLayout(partners);

    }

    private void populateChattingListLayout(List<RegisteredRequest> partners) {
        for (RegisteredRequest partner :
                partners) {
            ChattingData chattingData = SharedPreferenceUtil.getChattingData(sharedpreferences, partner.getRequestId());
            ChatListItem chatListItem = new ChatListItem((RegisteredRequest) partner, false, getActivity(), chattingData);
            chattingListLayout.addView(chatListItem);
            addChatListItem(partner.getRequestId(), chatListItem);
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

    public void addUnreadMessage(Message chatMessage) {
        String partnerId = chatMessage.getRequestId();
        if(chatListItems.get(partnerId) == null){
            return;
        }

        chatListItems.get(partnerId).addUnreadMessage();
    }
}
