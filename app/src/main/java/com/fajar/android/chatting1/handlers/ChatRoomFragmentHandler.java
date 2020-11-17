package com.fajar.android.chatting1.handlers;

import com.fajar.android.chatting1.activities.fragments.ChatRoomFragment;
import com.fajar.android.chatting1.activities.fragments.ChattingListFragment;


public class ChatRoomFragmentHandler extends BaseHandler<ChatRoomFragment> {

    private static ChatRoomFragmentHandler instance;


    private ChatRoomFragmentHandler(ChatRoomFragment f){
        super(f);
    }

    public static ChatRoomFragmentHandler getInstance(ChatRoomFragment fragment){
        if(null == instance) instance = new ChatRoomFragmentHandler(fragment);
        return instance;
    }



}
