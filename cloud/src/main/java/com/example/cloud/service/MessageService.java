package com.example.cloud.service;

import java.util.concurrent.ExecutionException;

public interface MessageService {
    void sendMsg(String msg,String uid) throws ExecutionException, InterruptedException;
}
