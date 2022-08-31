package com.example.mqttserver.mqttClient;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import netty.NettyInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttClientInitializer extends NettyInitializer {
    @Autowired
    private MqttClientHandler handler;
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new MqttDecoder());
        ch.pipeline().addLast(MqttEncoder.INSTANCE);
        ch.pipeline().addLast(handler);
        super.initChannel(ch);
    }
}
