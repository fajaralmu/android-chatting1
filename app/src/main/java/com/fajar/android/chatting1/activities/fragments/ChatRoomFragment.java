package com.fajar.android.chatting1.activities.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.components.ChatMessageItem;
import com.fajar.android.chatting1.handlers.ChatRoomFragmentHandler;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.ChattingData;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

import java.util.List;

public class ChatRoomFragment extends BaseFragment<ChatRoomFragmentHandler> {

    private LinearLayout messagesLayout;
    private EditText inputMessage;
    private ImageButton buttonSendMessage, buttonReloadMessage;
    private ScrollView scrollView;

    private RegisteredRequest partner, myAccount;

    public ChatRoomFragment() {
        setHandler(ChatRoomFragmentHandler.getInstance(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_room, container, false);
        setSharedpreferences();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        initComponents();
        initEvents();
    }

    private void loadMessages(String partnerId) {
        loader.setVisibility(View.VISIBLE);
        handler.getChattingMessages(partnerId, getRequestKey(), this::handleGetMessages);
    }

    public void scrollToDown() {
        if (null != scrollView)
            scrollView.post(() -> {
                Logs.log("Scroll to TOP");
                scrollView.fullScroll(View.FOCUS_DOWN);
            });
    }

    private void handleGetMessages(WebResponse response, Exception e) {
        stopLoading();
        if (null != e) {
            return;
        }

        SharedPreferenceUtil.setChattingData(sharedpreferences, partner, response);
        populateMessages(response.getMessageList());
        scrollToDown();
        markAsRead();
    }

    private void markAsRead() {
        handler.markMessageAsRead(partner);
    }

    private void populateMessages(List<Message> messages) {
        messagesLayout.removeAllViews();
        for (Message message : messages) {
            ChatMessageItem chatMessageItem = new ChatMessageItem( message, getActivity(), myAccount, partner);
            messagesLayout.addView(chatMessageItem);
        }
    }

    private void initComponents() {
        messagesLayout = findById(R.id.chat_room_message_list);
        loader = findById(R.id.loader_chat_room);
        inputMessage = findById(R.id.chat_room_message_input);
        buttonSendMessage = findById(R.id.button_send_message);
        buttonReloadMessage = findById(R.id.button_reload_message);
        scrollView = findById(R.id.chat_room_scroll);
        myAccount = SharedPreferenceUtil.getSessionData(sharedpreferences).getRegisteredRequest();
        partner = SharedPreferenceUtil.getObject(sharedpreferences, "chat_partner", RegisteredRequest.class);
    }

    public RegisteredRequest getMyAccount() {
        return myAccount;
    }

    private void initEvents() {
        setLoaderGone();

        inputMessage.setText("");
        inputMessage.addTextChangedListener(inputMessageTextChangedListener());
        buttonSendMessage.setOnClickListener(this::sendMessage);
        buttonReloadMessage.setOnClickListener((v) -> {
            loadMessages();
        });
        try {
            getActivity().runOnUiThread(() -> {
                checkStoredMessages();
            });
        } catch (Exception e) {
            //
        }
    }

    private TextWatcher inputMessageTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.sendTypingStatus(partner, true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.sendTypingStatus(partner, false);
            }
        };
    }

    private void checkStoredMessages() {
        ChattingData chattingData = SharedPreferenceUtil.getChattingData(sharedpreferences, partner.getRequestId());
        if (null == chattingData || chattingData.getMessages().size() == 0 || chattingData.hasUnreadMessage()) {
            loadMessages();
            return;
        }
        removeUnreadMessage(chattingData);
        populateMessages(chattingData.getMessages());
    }

    private void removeUnreadMessage(ChattingData chattingData) {
        chattingData.setUnreadMessages(0);
        SharedPreferenceUtil.setChattingData(sharedpreferences, partner.getRequestId(), chattingData);
    }

    private void sendMessage(View view) {
        String message = inputMessage.getText().toString();
        if (null == message || message.isEmpty()) {
            return;
        }
        loader.setVisibility(View.VISIBLE);
        scrollToDown();
        handler.sendMessage(partner.getRequestId(), getRequestKey(), message, this::handleSendMessage);
    }

    private void handleSendMessage(WebResponse response, Exception e) {
        stopLoading();
        if (null != e) {
            return;
        }
        inputMessage.setText("");
        appendNewChatMessage(response);
    }

    public String getPartnerRequestId() {
        return partner.getRequestId();
    }

    public void appendNewChatMessage(WebResponse response) {
        Logs.log("appendNewChatMessage......");
        getActivity().runOnUiThread(() -> {
            ChatMessageItem chatMessageItem = new ChatMessageItem(response.getChatMessage(), getActivity(), myAccount, partner);
            messagesLayout.addView(chatMessageItem);

            SharedPreferenceUtil.addChattingMessage(sharedpreferences, partner, response.getChatMessage(), false);
            handler.markMessageAsRead(partner);
            scrollToDown();
        });
    }

    private void loadMessages() {

        loadMessages(partner.getRequestId());
    }


}
