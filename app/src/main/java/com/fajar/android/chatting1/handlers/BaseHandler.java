package com.fajar.android.chatting1.handlers;

import com.fajar.android.chatting1.activities.fragments.BaseFragment;

public abstract class BaseHandler<F extends BaseFragment> {

    protected  final F fragment;
    public BaseHandler(F fragment){
        this.fragment = fragment;
    }

    protected void startLoading(){
        fragment.startLoading();
    }

    protected void stopLoading(){
        fragment.stopLoading();
    }
}
