package com.fajar.android.chatting1.models;

import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChattingData implements Serializable {

    private RegisteredRequest partner;
    private List<Message> messages;
    private int unreadMessages;
    public void addUnreadMessage(){
        setUnreadMessages(unreadMessages+1);
    }
    public void removeUnreadMessage(){
        setUnreadMessages(0);
    }

    public void addMessage(Message message){
        if(null == messages){
            messages = new ArrayList<>();
        }
        messages.add(message);
    }
}
