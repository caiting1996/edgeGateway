package com.example.mqttserver.server;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.NettyInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttInitializer extends NettyInitializer {
    @Autowired
    private MqttServerHandler handler;

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new MqttDecoder());
        ch.pipeline().addLast(MqttEncoder.INSTANCE);
        ch.pipeline().addLast(handler);
        super.initChannel(ch);
    }
}
