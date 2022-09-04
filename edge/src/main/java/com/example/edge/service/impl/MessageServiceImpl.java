package com.example.edge.service.impl;

import api.MessageService;
import com.example.edge.cache.Cache;
import com.example.edge.config.NettyConfig;
import com.example.edge.db.DataStorageContext;
import enums.MsgType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import model.MsgModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.JsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(MessageServiceImpl.class);
    @Override
    public void sendMsg(Object obj) {
        MsgModel msgModel= (MsgModel) obj;
        String topic="/"+msgModel.getDeviceModel().getDeviceId();
        List<Channel> lists= NettyConfig.getTopicMap().get(topic);
        if(lists==null || lists.size()==0) return;
        MqttPublishVariableHeader mqttPublishVariableHeader=new MqttPublishVariableHeader(topic,1);
        byte[] bytes=msgModel.toString().getBytes();
        ByteBuf byteBuf= ByteBufAllocator.DEFAULT.buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        //	构建返回报文， 固定报头
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.PUBLISH,false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        //	构建PUBACK消息体
        MqttPublishMessage publish = new MqttPublishMessage(mqttFixedHeaderBack,mqttPublishVariableHeader,byteBuf);
        System.out.println(topic);
        for (Channel channel:lists){
            System.out.println(channel);
            channel.writeAndFlush(publish);
        }
    }

    @Override
    public void receiveMsg(MsgModel msgModel) throws IOException {
        System.out.println(msgModel.getType());
        msgModel.setUserId(NettyConfig.getUserId());
        if(msgModel.getType().equals(MsgType.REGISTER.getType())){
            if(NettyConfig.getChannel()!=null && NettyConfig.getChannel().isActive()){
                log.info("缓存注册数据{}",msgModel);
                storageMsg(msgModel);
                log.info("注册数据{}到云端",msgModel);
                TextWebSocketFrame frame = new TextWebSocketFrame(JsonUtil.obj2String(msgModel));
                NettyConfig.getChannel().writeAndFlush(frame);
            }else {
                //如果云端和边缘端断开，将消息先缓存起来
                log.info("云边连接断开，缓存数据{}",msgModel);
                Cache.getMap().put(msgModel.getDeviceModel().getDeviceId(),new HashMap<String,MsgModel>().put(msgModel.getType(),msgModel));
            }

        }else if(msgModel.getType().equals(MsgType.UPLOAD.getType())){
            if(NettyConfig.getChannel()!=null && NettyConfig.getChannel().isActive()){
                System.out.println(NettyConfig.getChannel());
                TextWebSocketFrame frame = new TextWebSocketFrame(JsonUtil.obj2String(msgModel));
                log.info("上传数据{}到云端",msgModel);
                NettyConfig.getChannel().writeAndFlush(frame);
                log.info("缓存上传数据{}",msgModel);
                storageMsg(msgModel);
            }else {
                log.info("云边连接断开，缓存数据{}",msgModel);
                Cache.getMap().put(msgModel.getDeviceModel().getDeviceId(),new HashMap<String,MsgModel>().put(msgModel.getType(),msgModel));
            }
        }else {
            log.info("下传数据{}",msgModel);
            String topic="/"+msgModel.getDeviceModel().getDeviceId();
            sendMsg(msgModel);

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
