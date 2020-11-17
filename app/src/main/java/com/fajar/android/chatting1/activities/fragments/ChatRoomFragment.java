package com.fajar.android.chatting1.activities.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.components.ChatMessageItem;
import com.fajar.android.chatting1.handlers.ChatRoomFragmentHandler;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

import java.util.List;

public class ChatRoomFragment extends BaseFragment<ChatRoomFragmentHandler> {

    private LinearLayout messagesLayout;

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

    private void handleGetMessages(WebResponse response, Exception e) {
        stopLoading();
        if (null != e) {
            return;
        }
        messagesLayout.removeAllViews();
        List messages = response.getResultList();
        for (Object message :
                messages) {
            ChatMessageItem chatMessageItem = new ChatMessageItem((Message) message, getActivity());
            messagesLayout.addView(chatMessageItem);
        }
    }

    private void initComponents() {
        messagesLayout = findById(R.id.chat_room_message_list);
        loader = findById(R.id.loader_chat_room);
    }

    private void initEvents() {
        setLoaderGone();
        getMessages();
    }

    private void getMessages() {
        RegisteredRequest partner = SharedPreferenceUtil.getObject(sharedpreferences, "chat_partner", RegisteredRequest.class);
        getMessages(partner.getRequestId());
    }


}
