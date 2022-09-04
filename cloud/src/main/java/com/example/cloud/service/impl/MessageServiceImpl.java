package com.example.cloud.service.impl;

import api.MessageService;
import com.example.cloud.config.NettyConfig;
import com.example.cloud.db.DataStorageContext;
import com.example.cloud.server.ServerHandler;
import com.example.cloud.server.ServerHeartbeatHandler;
import enums.MsgType;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
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
    private static final InternalLogger log = InternalLoggerFactory.getInstance(MessageServiceImpl.class);
    @Autowired
    private ServerHandler webSocketHandler;

    @Override
    public void sendMsg(Object obj) {
        Map map=(Map)obj;
        Channel channel= (Channel) map.get("channel");
        MsgModel msgModel= (MsgModel) map.get("msg");
        channel.writeAndFlush(new TextWebSocketFrame(JsonUtil.obj2String(msgModel)));
    }

    @Override
    public void receiveMsg(MsgModel msgModel) throws IOException {
        System.out.println(msgModel.getType());
        if(msgModel.getType().equals(MsgType.REGISTER.getType())){
            NettyConfig.getDeviceChannelMap().put(msgModel.getDeviceModel().getDeviceId(),msgModel.getUserId());
            log.info("缓存注册数据{}",msgModel);
            storageMsg(msgModel);
        }else if(msgModel.getType().equals(MsgType.UPLOAD.getType())){
            log.info("缓存上传数据{}",msgModel);
            NettyConfig.getDeviceChannelMap().put(msgModel.getDeviceModel().getDeviceId(),msgModel.getUserId());
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
