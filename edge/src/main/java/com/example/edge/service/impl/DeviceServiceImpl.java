package com.example.edge.service.impl;

import api.DeviceService;
import api.MessageService;
import com.example.edge.config.NettyConfig;
import com.example.edge.db.DataStorageContext;
import enums.MsgType;
import io.netty.channel.Channel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import model.DeviceModel;
import model.MsgModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
@Service
public class DeviceServiceImpl implements DeviceService {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(DeviceServiceImpl.class);
    @Autowired
    private MessageService messageService;
    @Override
    public void registerDevice(DeviceModel deviceModel) throws IOException {
        MsgModel msgModel=new MsgModel();
        msgModel.setDeviceModel(deviceModel);
        msgModel.setType(MsgType.REGISTER.getType());
        messageService.receiveMsg(msgModel);

    }

    @Override
    public void deleteDevice(DeviceModel deviceModel) {
        DataStorageContext.getFileStorage().deleteData(deviceModel.getDeviceId());
        String topic="/"+deviceModel.getDeviceId();
        List<Channel> lists= NettyConfig.getTopicMap().get(topic);
        if(lists==null || lists.size()==0) return;
        for (Channel channel:lists){
            channel.close();
        }
    }

    @Override
    public void operateDevice(DeviceModel deviceModel) {
        MsgModel msgModel=new MsgModel();
        msgModel.setDeviceModel(deviceModel);
        msgModel.setType(MsgType.DOWNLOAD.getType());
        messageService.sendMsg(msgModel);
    }
}
