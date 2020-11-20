package com.fajar.android.chatting1.handlers;

import android.os.AsyncTask;

import com.fajar.android.chatting1.activities.HomeActivity;
import com.fajar.android.chatting1.activities.fragments.ChatRoomFragment;
import com.fajar.android.chatting1.service.ChattingService;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;


public class ChatRoomFragmentHandler extends BaseHandler<ChatRoomFragment> {

    private static ChatRoomFragmentHandler instance;


    private ChatRoomFragmentHandler(ChatRoomFragment f) {
        super(f);
    }

    public static ChatRoomFragmentHandler getInstance(ChatRoomFragment fragment) {
        if (null == instance) instance = new ChatRoomFragmentHandler(fragment);
        return instance;
    }

    public void getChattingMessages(String partnerId, String requestKey, MyConsumer<WebResponse> callback) {
        getChattingMessagesTask(callback).execute(partnerId, requestKey);
    }

    public void sendMessage(String partnerId, String requestKey, String messageBody, MyConsumer<WebResponse> callback) {
        sendMessageTask(callback).execute(partnerId, requestKey, messageBody);
    }

    private AsyncTask<String, Void, WebResponse> getChattingMessagesTask(MyConsumer<WebResponse> callback) {
        return new AsyncTask<String, Void, WebResponse>() {

            private Exception exception;

            @Override
            protected WebResponse doInBackground(String... strings) {
                try {
                    return ChattingService.instance().getChattingMessages(strings[0], strings[1]);
                } catch (Exception e) {
                    this.exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(WebResponse webResponse) {
                //     stopLoading();
                Logs.log("onPostExecute getChattingMessagesTask");

                callback.accept(webResponse, exception);
            }
        };
    }

    private AsyncTask<String, Void, WebResponse> sendMessageTask(MyConsumer<WebResponse> callback) {
        return new AsyncTask<String, Void, WebResponse>() {

            private Exception exception;

            @Override
            protected WebResponse doInBackground(String... strings) {
                try {
                    return ChattingService.instance().sendMessage(strings[0], strings[1], strings[2]);
                } catch (Exception e) {
                    this.exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(WebResponse webResponse) {
                //     stopLoading();
                Logs.log("onPostExecute sendMessageTask");
                callback.accept(webResponse, exception);
            }
        };
    }


    public void markMessageAsRead(RegisteredRequest partner) {
        RegisteredRequest account = fragment.getMyAccount();
        try {
            GeneralApplicationHandler.instance((HomeActivity) fragment.getActivity()).markMessageAsRead(account, partner);

        } catch (Exception e) {
            Logs.log("error markMessageAsRead: ", e);
        }
    }
}
