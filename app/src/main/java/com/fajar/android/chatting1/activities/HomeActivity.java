package com.fajar.android.chatting1.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.android.chatting1.activities.fragments.BaseFragment;
import com.fajar.android.chatting1.activities.fragments.ChatRoomFragment;
import com.fajar.android.chatting1.activities.fragments.ChattingListFragment;
import com.fajar.android.chatting1.constants.Actions;
import com.fajar.android.chatting1.constants.Extras;
import com.fajar.android.chatting1.constants.SharedPreferencesConstants;
import com.fajar.android.chatting1.handlers.HomeActivityHandler;
import com.fajar.android.chatting1.service.Commons;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.AlertUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.Navigate;
import com.fajar.android.chatting1.R;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import static android.view.View.*;

public class HomeActivity extends FragmentActivity {

    private int currentFragmentId;
    private Actions nextAction = Actions.NONE;
    private boolean insideCatalogPage;

    private BottomNavigationView bottomNavigationView;
    private TextView breadCumb;
    private BaseFragment currentFragment;


    private SharedPreferences sharedPreferences;
    private HomeActivityHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.handler = HomeActivityHandler.instance(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        sharedPreferences = getSharedPreferences(SharedPreferencesConstants.SHARED_CONTENT.value, Context.MODE_PRIVATE);
        StrictMode.setThreadPolicy(policy);
        Logs.log("ONCREATE....");
        setContentView(R.layout.activity_home);
        initComponent();
        initEvent();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        switchFragment(R.layout.fragment_home);
        super.onPostCreate(savedInstanceState);
    }

    protected void initComponent() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        breadCumb = findViewById(R.id.home_breadcumb);

    }

    public void setBreadCumbText(String text) {
        breadCumb.setText(text);
        if (null != breadCumb && text != null) {
            breadCumb.setVisibility(VISIBLE);
        } else {
            breadCumb.setVisibility(GONE);
        }

    }

    protected void initEvent() {
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener());
        switchHomePage();
        initializeWebsocket();
    }

    private void initializeWebsocket() {
        handler.initializeWebsocket(SharedPreferenceUtil.getSessionData(sharedPreferences).getRegisteredRequest());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        switchHomePage();

                        break;
                    case R.id.navigation_chatting_list:

                        switchChattingListPage();
                        break;

                    case R.id.navigation_search:
                        switchFragment(R.layout.fragment_search, "Search");
                        break;
                }
                setDefaultValues();
                return false;
            }
        };
    }

    private void setDefaultValues() {

        setInsideCatalogPage(false);
        setNextAction(Actions.NONE);
    }


    private void switchChattingListPage() {
        switchFragment(R.layout.fragment_chatting_list, "Chatting List");
    }

    private void switchHomePage() {
        switchFragment(R.layout.fragment_home, "Home");
    }

    public void switchFragmentInCatalogPage(int fragmentId, String breadCumbLabel) {
        setInsideCatalogPage(true);
        switchFragment(fragmentId, breadCumbLabel);
    }

    public void switchFragment(int fragmentId) {
        switchFragment(fragmentId, null);
    }

    public void switchFragment(int fragmentId, String breadCumbLabel) {
        switchFragment(fragmentId, breadCumbLabel, true);
    }

    public void switchFragment(int fragmentId, String breadCumbLabel, boolean withAnimation) {
        if (currentFragmentId == fragmentId) {
            return;
        }
        Logs.log("switchFragment: ", fragmentId);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction;

        //TODO: decide the animations
        if (false) {//withAnimation) {
            fragmentTransaction = fragmentManager.beginTransaction().
                    setCustomAnimations( //https://developer.android.com/training/basics/fragments/animate
                            R.anim.anim_slide_in,  // enter
                            R.anim.anim_fade_out,  // exit
                            R.anim.anim_fade_in,   // popEnter
                            R.anim.anim_slide_out  // popExit
                    );
            /**
             * R.anim.slide_in,  // enter  R.anim.fade_out,  // exit R.anim.fade_in,   // popEnter  R.anim.slide_out
             */
        } else {
            fragmentTransaction = fragmentManager.beginTransaction();
        }

        BaseFragment fragment = BaseFragment.newInstance(fragmentId, null, breadCumbLabel, getNextAction());

        fragmentTransaction.replace(R.id.home_common_content_container, fragment);
        fragmentTransaction.commit();

        setCurrentFragmentId(fragmentId);
        setCurrentFragment(fragment);

    }

    public void setCurrentFragment(BaseFragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    public void setCurrentFragmentId(int currentFragmentId) {
        this.currentFragmentId = currentFragmentId;
    }

    @Override
    public void onBackPressed() {

        //if in catalog
        if (isInsideCatalogPage()) {
            setInsideCatalogPage(false);
            bottomNavigationView.setSelectedItemId(R.id.navigation_chatting_list);

        } else if (currentFragmentId != R.layout.fragment_home) {
            switchHomePage();
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        } else if (currentFragmentId == R.layout.fragment_home) {
            AlertUtil.confirm(this, "Exit Application?", this::exitApplication);
        }

    }

    private void exitApplication(DialogInterface dialog, int which) {
        Navigate.navigate(this, WelcomingScreenActivity.class, Extras.EXIT_APP_KEY.value, Extras.EXIT_APP_VALUE);
    }

    public void setInsideCatalogPage(boolean insideCatalogPage) {
        this.insideCatalogPage = insideCatalogPage;
        bottomNavigationView.setVisibility(insideCatalogPage ? GONE : VISIBLE);
    }

    public boolean isInsideCatalogPage() {
        return insideCatalogPage;
    }

    public void setNextAction(Actions nextAction) {
        this.nextAction = nextAction;
    }

    public Actions getNextAction() {
        return nextAction;
    }

    public BaseFragment getCurrentFragment(){
        return currentFragment;
    }


    public void enterChatRoom(RegisteredRequest partner) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString("chat_partner", Commons.getObjectMapper().writeValueAsString(partner));
            editor.commit();
            switchFragmentInCatalogPage(R.layout.fragment_chat_room, "Chatroom::" + partner.getUsername());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    ////////////// WEBSOCKET HANDLERS //////////////////////
    public void showNewChatMessage(WebResponse response) {
        Logs.log("Websocket HANDLE showNewChatMessage");
        final Message chatMessage = response.getChatMessage();
        runOnUiThread(() -> {

            if (currentFragment instanceof ChatRoomFragment) {
                String partnerRequestId = ((ChatRoomFragment) currentFragment).getPartnerRequestId();
                if (chatMessage.getRequestId().equals(partnerRequestId) == false) {
                    return;
                }
                ((ChatRoomFragment) currentFragment).appendNewChatMessage(response);
            }
            if (currentFragment instanceof ChattingListFragment) {
                ((ChattingListFragment) currentFragment).addUnreadMessage(chatMessage);
            }

            if (isPartnerExist(chatMessage.getRequestId()) == false) {

                if (currentFragment instanceof ChattingListFragment) {
                    currentFragment.doByAction(Actions.RELOAD);
                    return;
                }

                Logs.log("WILL NOTIFY USER..........");
                setNextAction(Actions.RELOAD);
                bottomNavigationView.setSelectedItemId(R.id.navigation_chatting_list);

            } else {

                SharedPreferenceUtil.addChattingMessage(sharedPreferences, chatMessage.getRequestId(), chatMessage, true);
            }
        });
    }

    private boolean isPartnerExist(String partnerId) {
        WebResponse partnersData = SharedPreferenceUtil.getChattingPartnersData(sharedPreferences);
        if (partnersData != null && partnersData.getChattingPartnerList() != null) {
            List<RegisteredRequest> partners = partnersData.getChattingPartnerList();
            for (RegisteredRequest partner :  partners) {
                try {
                    if (partner.getRequestId().equals(partnerId)) {
                        return true;
                    }
                } catch (Exception e) { }
            }
        }
        return false;
    }
}
