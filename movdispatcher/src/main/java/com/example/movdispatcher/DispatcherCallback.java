package com.example.movdispatcher;

import com.example.movdispatcher.models.Dispatcher;

public interface DispatcherCallback {
    void onDispatcherReceivedCallback(Dispatcher dispatcher);
    void onDispatcherNotFoundCallback(String error);
}
