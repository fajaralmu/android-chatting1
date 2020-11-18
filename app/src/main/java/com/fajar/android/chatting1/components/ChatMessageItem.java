package com.fajar.android.chatting1.components;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.activities.HomeActivity;
import com.fajar.android.chatting1.service.ImageViewWithURL;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;

import java.util.Date;

import androidx.cardview.widget.CardView;

public class ChatMessageItem extends LinearLayout {

    private TextView messageBody;
    private TextView messageDate;
    private Message message;
    private CardView messageCard;
    final HomeActivity parentActivity;
    final RegisteredRequest sender, receiver;

    public ChatMessageItem(Message message, Activity parentActivity, RegisteredRequest sender, RegisteredRequest receiver) {
        super(parentActivity);

        if (parentActivity instanceof HomeActivity) {
            this.parentActivity = (HomeActivity) parentActivity;
        } else {
            this.parentActivity = null;
        }
        init(parentActivity, null);
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        populateContent(message);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.component_chat_message_item, this);
        initComponents();
        initEvents();

    }

    public void populateContent(Message message) {

        if(message.getRequestId().equals(sender.getRequestId())){
            messageCard.setCardBackgroundColor(Color.rgb(200,255,255));
        }
        messageBody.setText(message.getBody());
        messageDate.setText(message.getDate() == null ?
                "+" + new Date() :
                message.getDate().toString());
    }


    private void initComponents() {
        messageBody = findViewById(R.id.chat_message_body);
        messageCard = findViewById(R.id.message_card);
        messageDate = findViewById(R.id.chat_message_date);
    }

    private void initEvents() {

    }


}
