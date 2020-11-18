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
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

import java.util.List;

public class ChatRoomFragment extends BaseFragment<ChatRoomFragmentHandler> {

    private LinearLayout messagesLayout;
    private EditText inputMessage;
    private ImageButton buttonSendMessage;
    private ScrollView scrollView;

    private RegisteredRequest partner;
    private RegisteredRequest myAccount;

    public ChatRoomFragment() {
        setHandler(ChatRoomFragmentHandler.getInstance(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_room, container, false);
        setSharedpreferences();
        initComponents();
        initEvents();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    private void getMessages(String partnerId) {
        loader.setVisibility(View.VISIBLE);
        handler.getChattingMessages(partnerId, getRequestKey(), this::handleGetMessages);
    }

    public void scrollToDowm() {
        if(null!=scrollView)
            scrollView.post(new Runnable() {
                public void run() {
                    Logs.log("Scroll to TOP");
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
    }

    private void handleGetMessages(WebResponse response, Exception e) {
        stopLoading();
        if (null != e) {
            return;
        }
        messagesLayout.removeAllViews();
        List messages = response.getResultList();
        for (Object message :
                messages) {
            ChatMessageItem chatMessageItem = new ChatMessageItem((Message) message, getActivity(), myAccount, partner);
            messagesLayout.addView(chatMessageItem);
        }
        scrollToDowm();
    }

    private void initComponents() {
        messagesLayout = findById(R.id.chat_room_message_list);
        loader = findById(R.id.loader_chat_room);
        inputMessage = findById(R.id.chat_room_message_input);
        buttonSendMessage = findById(R.id.button_send_message);
        scrollView = findById(R.id.chat_room_scroll);
        myAccount = SharedPreferenceUtil.getSessionData(sharedpreferences).getRegisteredRequest();
    }

    private void initEvents() {
        setLoaderGone();
        getMessages();
        inputMessage.setText("");
        buttonSendMessage.setOnClickListener(this::sendMessage);
    }

    private void sendMessage(View view) {
        String message = inputMessage.getText().toString();
        if(null == message || message.isEmpty()){
            return;
        }
        loader.setVisibility(View.VISIBLE);
        scrollToDowm();
        handler.sendMessage(partner.getRequestId(), getRequestKey(), message, this::handleSendMessage);
    }

    private void handleSendMessage(WebResponse response, Exception e) {
        stopLoading();
        if(null != e){
            return;
        }
        inputMessage.setText("");
        ChatMessageItem chatMessageItem = new ChatMessageItem(response.getChatMessage(), getActivity(), myAccount, partner);
        messagesLayout.addView(chatMessageItem);
        scrollToDowm();
    }

    private void getMessages() {
        this.partner = SharedPreferenceUtil.getObject(sharedpreferences, "chat_partner", RegisteredRequest.class);
        getMessages(partner.getRequestId());
    }


}
