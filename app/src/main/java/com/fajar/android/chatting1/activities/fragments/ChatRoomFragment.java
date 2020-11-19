package com.fajar.android.chatting1.activities.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.fajar.android.chatting1.models.ChattingData;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.Logs;
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

    public void scrollToDowm() {
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
        populateMessages(response.getResultList());
        scrollToDowm();
    }

    private void populateMessages(List messages) {
        messagesLayout.removeAllViews();
        for (Object message :
                messages) {
            ChatMessageItem chatMessageItem = new ChatMessageItem((Message) message, getActivity(), myAccount, partner);
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

    private void initEvents() {
        setLoaderGone();

        inputMessage.setText("");
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

    private void checkStoredMessages() {
        ChattingData chattingData = SharedPreferenceUtil.getChattingData(sharedpreferences, partner.getRequestId());
        if (null == chattingData || chattingData.getMessages().size() == 0 || chattingData.hasUnreadMessage()) {
            loadMessages();
            return;
        }
        chattingData.setUnreadMessages(0);
        SharedPreferenceUtil.setChattingData(sharedpreferences, partner.getRequestId(), chattingData);
        populateMessages(chattingData.getMessages());
    }

    private void sendMessage(View view) {
        String message = inputMessage.getText().toString();
        if (null == message || message.isEmpty()) {
            return;
        }
        loader.setVisibility(View.VISIBLE);
        scrollToDowm();
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

        getActivity().runOnUiThread(() -> {
            ChatMessageItem chatMessageItem = new ChatMessageItem(response.getChatMessage(), getActivity(), myAccount, partner);
            messagesLayout.addView(chatMessageItem);

            SharedPreferenceUtil.addChattingMessage(sharedpreferences, partner, response.getChatMessage(), false);
            scrollToDowm();
        });
    }

    private void loadMessages() {

        loadMessages(partner.getRequestId());
    }


}
