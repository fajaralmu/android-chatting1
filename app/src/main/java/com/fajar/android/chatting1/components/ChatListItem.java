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
import com.fajar.android.chatting1.service.ImageViewWithURL;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.RegisteredRequest;

import java.util.Date;

public class ChatListItem extends LinearLayout {
    private ImageView imageThumbnail;
    private TextView newsTitle;
    private TextView newsDate;
    private ImageView buttonNewsLink;
    private RegisteredRequest partnerAccount;

    final boolean loadImage;
    final HomeActivity parentActivity;

    public ChatListItem(Context context, @Nullable AttributeSet attrs, boolean loadImage, Activity parentActivity) {
        super(context, attrs);
        this.loadImage = loadImage;
        if (parentActivity instanceof HomeActivity){
            this.parentActivity = (HomeActivity) parentActivity;
        }else {
            this.parentActivity = null;
        }
        init(context, attrs);
    }

    public ChatListItem(RegisteredRequest partnerAccount, boolean loadImage, Activity parentActivity) {
        super(parentActivity);
        this.loadImage = loadImage;
        if (parentActivity instanceof HomeActivity){
            this.parentActivity = (HomeActivity) parentActivity;
        }else {
            this.parentActivity = null;
        }
        init(parentActivity, null);
        this.partnerAccount = partnerAccount;
        populateContent(partnerAccount);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.component_chat_list_item, this);
        initComponents();
        initEvents();
        if (null != attrs) {
            int[] sets = {R.attr.imageLabelDrawableId, R.attr.imageLabelText};
            TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                String attributeName = attrs.getAttributeName(i);
                switch (attributeName) {
                    case "newsItemThumbnail":
                        int value = attrs.getAttributeResourceValue(i, R.drawable.ic_home_black_24dp);
                        imageThumbnail.setImageResource(value);
                        break;
                    case "newsItemTitle":
                        String labelText = attrs.getAttributeValue(i);
                        newsTitle.setText(labelText);
                        break;
                    case "newsItemDate":
                        labelText = attrs.getAttributeValue(i);
                        newsTitle.setText(labelText);
                        break;
                }
                Logs.log(i, " attr: ", attrs.getAttributeName(i), "=", attrs.getAttributeValue(i));
            }
            typedArray.recycle();
        }
    }

    public void populateContent(RegisteredRequest partnerAccount) {
        if (isLoadImage()) {
            loadImage();
        }
//        if (parentActivity != null && parentActivity.getPostBitmap(post.getId()) != null) {
//            imageThumbnail.setImageBitmap(parentActivity.getPostBitmap(post.getId()));
//        }

        setTitle(partnerAccount.getUsername());
        setNewsDate(partnerAccount.getCreated() == null? new Date().toString():partnerAccount.getCreated().toString());
    }

    public void loadImage() {
        if (null == partnerAccount){// || null == partnerAccount.getImages()) {
            return;
        }
        return;

//        String url = partnerAccount.getImages().getThumbnail();
//        Logs.log("START load image:", url);
//        ImageViewWithURL imageViewContents = new ImageViewWithURL(imageThumbnail, url, bitmapHandler());
////        downloadImageTask =
//                imageViewContents.populate();
//        Logs.log("END load image:", url);

    }

    private ImageViewWithURL.HandleBitmapResult bitmapHandler() {

        return new ImageViewWithURL.HandleBitmapResult() {
            @Override
            public void handleBitmap(Bitmap bitmap) {
                if (null != parentActivity){}
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
        buttonNewsLink.setImageResource((android.R.drawable.ic_menu_info_details));


    }

    private void initEvents() {
        buttonNewsLink.setOnClickListener(showPopupMenu());
        imageThumbnail.setImageResource(android.R.drawable.ic_menu_camera);
    }

    private OnClickListener showPopupMenu() {

        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.setOnMenuItemClickListener(popupMenuOnClick());
                popupMenu.inflate(R.menu.news_item_popup_menu);
                popupMenu.show();
            }

        };
    }

    private void openLink() {
        if (null == partnerAccount) {
            return;
        }
       // Navigate.openLink(partnerAccount.newsLink(), getContext());
    }

    private PopupMenu.OnMenuItemClickListener popupMenuOnClick() {
        return new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_news_item_open_link:
                        openLink();
                        break;
                    case R.id.menu_news_item_share:
                        shareLink();
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
