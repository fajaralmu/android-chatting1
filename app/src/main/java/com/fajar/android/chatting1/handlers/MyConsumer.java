package com.fajar.android.chatting1.handlers;

public interface MyConsumer<T> {

    void accept(T t, Exception error);


}