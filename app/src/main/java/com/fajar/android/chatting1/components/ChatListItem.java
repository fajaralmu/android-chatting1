package com.fajar.android.chatting1.components;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.activities.HomeActivity;
import com.fajar.android.chatting1.models.ChattingData;
import com.fajar.android.chatting1.service.ImageViewWithURL;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.RegisteredRequest;

import java.util.Date;

public class ChatListItem extends LinearLayout {
    private ImageView imageThumbnail;
    private TextView newsTitle;
    private TextView labelUnreadMessage;
    private TextView newsDate;
    private ImageView buttonNewsLink;
    private RegisteredRequest partnerAccount;

    final boolean loadImage;
    final ChattingData chattingData;
    final HomeActivity parentActivity;

    public ChatListItem(RegisteredRequest partnerAccount, boolean loadImage, Activity parentActivity, ChattingData chattingData) {
        super(parentActivity);
        this.loadImage = loadImage;
        this.chattingData = chattingData;
        this.partnerAccount = partnerAccount;

        if (parentActivity instanceof HomeActivity) {
            this.parentActivity = (HomeActivity) parentActivity;
        } else {
            this.parentActivity = null;
        }
        init(parentActivity, null);
        populateContent();

    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.component_chat_list_item, this);
        initComponents();
        initEvents();
    }

    public void populateContent() {
        if (isLoadImage()) {
            loadImage();
        }
//        if (parentActivity != null && parentActivity.getPostBitmap(post.getId()) != null) {
//            imageThumbnail.setImageBitmap(parentActivity.getPostBitmap(post.getId()));
//        }

        setTitle(partnerAccount.getUsername());
        setNewsDate(partnerAccount.getCreated() == null ?
                "+" + new Date() :
                partnerAccount.getCreated().toString());
        if (null != chattingData) {
            setUnreadMessage((chattingData.getUnreadMessages()));
        }
    }

    public void setUnreadMessage(int unreadMessage) {
        labelUnreadMessage.setText("Unread message(s):" + (unreadMessage));
    }

    public void loadImage() {
        if (null == partnerAccount) {// || null == partnerAccount.getImages()) {
            return;
        }
        return;

    }

    private ImageViewWithURL.HandleBitmapResult bitmapHandler() {

        return new ImageViewWithURL.HandleBitmapResult() {
            @Override
            public void handleBitmap(Bitmap bitmap) {
                if (null != parentActivity) {
                }
                //   parentActivity.addPostBitmap(partnerAccount.getId(), bitmap);
            }
        };
    }

    public void setTitle(String title) {
        newsTitle.setText(title);
    }

    public void setNewsDate(String date) {
        newsDate.setText(date);
    }

    private void initComponents() {
        imageThumbnail = findViewById(R.id.chat_item_thumbnail);
        newsTitle = findViewById(R.id.chat_item_partner_name);
        newsDate = findViewById(R.id.chat_item_last_message_date);
        buttonNewsLink = findViewById(R.id.chat_list_item_options);
        buttonNewsLink.setImageResource((R.drawable.ic_more_vert_black_24dp));
        labelUnreadMessage = findViewById(R.id.chat_item_unread_message_ciunt);

    }

    private void initEvents() {
        buttonNewsLink.setOnClickListener(this::showPopupMenu);
        imageThumbnail.setImageResource(android.R.drawable.stat_notify_chat);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.setOnMenuItemClickListener(popupMenuOnClick());
        popupMenu.inflate(R.menu.chat_item_popup_menu);
        popupMenu.show();

    }

    private void enterChatRoom() {
        if (null == partnerAccount) {
            return;
        }
        parentActivity.enterChatRoom(partnerAccount);
        // Navigate.openLink(partnerAccount.newsLink(), getContext());
    }

    private PopupMenu.OnMenuItemClickListener popupMenuOnClick() {
        return new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_chat_item_enter_room:
                        enterChatRoom();
                        break;
                    default:
                        break;
                }
                return false;
            }
        };
    }

    private void shareLink() {
        // Navigate.shareText(this.getContext(), partnerAccount.getTitle() + " kunjungi link:" + partnerAccount.newsLink());
    }

    public boolean isLoadImage() {
        return loadImage;
    }
}
