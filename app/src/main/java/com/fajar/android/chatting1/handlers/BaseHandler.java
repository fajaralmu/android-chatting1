package com.fajar.android.chatting1.handlers;

import com.fajar.android.chatting1.activities.fragments.BaseFragment;

public abstract class BaseHandler {

    protected  final BaseFragment fragment;
    public BaseHandler(BaseFragment fragment){
        this.fragment = fragment;
    }
}
