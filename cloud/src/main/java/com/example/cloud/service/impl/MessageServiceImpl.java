package com.example.cloud.service.impl;

import api.MessageService;
import com.example.cloud.config.NettyConfig;
import com.example.cloud.db.DataStorageContext;
import com.example.cloud.server.ServerHandler;
import enums.MsgType;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import model.MsgModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.JsonUtil;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private ServerHandler webSocketHandler;

    @Override
    public void sendMsg(Object obj) {
        Map map=(Map)obj;
        Channel channel= (Channel) map.get("channel");
        MsgModel msgModel= (MsgModel) map.get("msg");
        channel.writeAndFlush(msgModel);
    }

    @Override
    public void receiveMsg(MsgModel msgModel) throws IOException {
        System.out.println(msgModel.getType());
        if(msgModel.getType().equals(MsgType.REGISTER.getType())){
            NettyConfig.getDeviceChannelMap().put(msgModel.getDeviceModel().getDeviceId(),NettyConfig.getUserChannelMap().get(msgModel.getUserId()));
            storageMsg(msgModel);
        }else if(msgModel.getType().equals(MsgType.UPLOAD.getType())){
                storageMsg(msgModel);
        }
    }

    @Override
    public void storageMsg(MsgModel msgModel) throws IOException {
        DataStorageContext.getFileStorage().storageData(msgModel.getDeviceModel());
    }

    @Override
    public void handleMsg(MsgModel msgModel) {

    }
}
