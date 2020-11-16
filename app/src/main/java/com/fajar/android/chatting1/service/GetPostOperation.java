package com.fajar.android.chatting1.service;

import android.os.AsyncTask;

import com.fajar.android.chatting1.activities.fragments.post.PostContentPage;
import com.fajar.android.chatting1.models.PostResponse;

public class GetPostOperation extends AsyncTask<Object, Void, PostResponse> {
    final PostContentPage parent;
    private  Exception getPostError;

    public GetPostOperation(PostContentPage parent){
        this.parent = parent;
    }
    @Override
    protected PostResponse doInBackground(Object... params) {
        try {
            return parent.getPost(params);
        }catch (Exception e){
            getPostError = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(PostResponse postResponse) {
        parent.handleGetPost(postResponse, getPostError);
    }
}
