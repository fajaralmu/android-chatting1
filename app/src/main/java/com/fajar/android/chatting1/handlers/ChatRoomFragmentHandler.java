package com.fajar.android.chatting1.handlers;

import android.os.AsyncTask;

import com.fajar.android.chatting1.activities.fragments.ChatRoomFragment;
import com.fajar.android.chatting1.activities.fragments.ChattingListFragment;
import com.fajar.android.chatting1.service.ChattingService;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.android.chatting1.util.MapUtil;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChatRoomFragmentHandler extends BaseHandler<ChatRoomFragment> {

    private static ChatRoomFragmentHandler instance;


    private ChatRoomFragmentHandler(ChatRoomFragment f){
        super(f);
    }

    public static ChatRoomFragmentHandler getInstance(ChatRoomFragment fragment){
        if(null == instance) instance = new ChatRoomFragmentHandler(fragment);
        return instance;
    }

    public void getChattingMessages(String partnerId, String requestKey, MyConsumer<WebResponse> callback){
        getChattingMessagesTask(callback).execute(partnerId, requestKey);
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

                //convert data type from Map to RegisteredRequest.class
                if(null != webResponse && webResponse.getResultList().size() > 0){
                    List results = webResponse.getResultList();
                    List<Message> partners = new ArrayList<>();
                    for (Object result :
                            results) {
                        if (result instanceof Map){
                            Message message = MapUtil.mapToObject((Map) result, Message.class);
                            partners.add(message);
                        }
                    }
                    webResponse.setResultList(partners);
                }

                callback.accept(webResponse, exception);
            }
        };
    }


}
