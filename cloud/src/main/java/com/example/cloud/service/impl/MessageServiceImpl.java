package com.example.cloud.service.impl;

import com.example.cloud.server.ServerHandler;
import com.example.cloud.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private ServerHandler webSocketHandler;
    @Override
    public void sendMsg(String msg, String uid) throws ExecutionException, InterruptedException {
          webSocketHandler.sendMsg(msg,uid);
    }
}
