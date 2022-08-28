package com.example.mqttserver.server;

import io.netty.channel.ChannelFuture;
import netty.AbstractNettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Component
public class MqttServer extends AbstractNettyServer {
    private static final Logger log = LoggerFactory.getLogger(MqttServer.class);
    @Autowired
    private MqttInitializer initializer;
    public MqttServer(MqttInitializer initializer) {
        super(initializer);
    }
    private void start() throws InterruptedException{
        bootstrap.localAddress(new InetSocketAddress(1883));
        ChannelFuture channelFuture = bootstrap.bind().sync();
        log.info("Server started and listen on:{}",channelFuture.channel().localAddress());
    }

    /**
     * 释放资源
     * @throws InterruptedException
     */
    @PreDestroy
    public void destroy() throws InterruptedException {
        super.destroy();
    }
    @PostConstruct()
    public void init() throws InterruptedException {
        //需要开启一个新的线程来执行netty server 服务器
        new Thread(() -> {
            try {
                start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
