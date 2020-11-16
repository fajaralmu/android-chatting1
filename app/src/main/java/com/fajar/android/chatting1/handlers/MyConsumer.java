package com.fajar.android.chatting1.handlers;

import java.util.Objects;

public interface MyConsumer<T> {

    void accept(T t, Exception error);


}