package com.example.cloud.service.impl;

import api.DeviceService;
import api.MessageService;
import com.example.cloud.config.NettyConfig;
import enums.MsgType;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import model.DeviceModel;
import model.MsgModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeviceServiceImpl implements DeviceService {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(DeviceServiceImpl.class);
    @Autowired
    private MessageService messageService;

    @Override
    public void registerDevice(DeviceModel deviceModel) {

    }

    @Override
    public void deleteDevice(DeviceModel deviceModel) {

    }

    @Override
    public void operateDevice(DeviceModel deviceModel) {
        Map map=new HashMap();
        map.put("channel", NettyConfig.getUserChannelMap().get(NettyConfig.getDeviceChannelMap().get(deviceModel.getDeviceId())));
        MsgModel msgModel=new MsgModel();
        msgModel.setType(MsgType.DOWNLOAD.getType());
        msgModel.setDeviceModel(deviceModel);
        map.put("msg",msgModel);
        log.info("发送数据{}",map);
        messageService.sendMsg(map);
    }
}
