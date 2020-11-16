package com.fajar.android.chatting1.activities.fragments.post;

import com.fajar.android.chatting1.models.PostResponse;

public interface PostContentPage {
    PostResponse getPost(Object...params);

    void handleGetPost(PostResponse postResponse, Exception getPostError);
}
