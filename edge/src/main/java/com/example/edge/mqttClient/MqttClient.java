package com.example.edge.mqttClient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import netty.AbstractNettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@Component
public class MqttClient extends AbstractNettyClient {
    private Channel channel;
    @Autowired
    private MqttClientInitializer initializer;
    public MqttClient(MqttClientInitializer initializer) throws URISyntaxException {
        super(initializer);
    }
    URI mqttURI = new URI("tcp://localhost:1883");
    public void start() throws URISyntaxException, InterruptedException, ExecutionException {
        System.out.println("开始连接MQTT服务端");
        Channel channel = bootstrap.connect(mqttURI.getHost(),mqttURI.getPort()).sync().channel();
        this.channel=channel;
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.CONNECT,false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttConnectVariableHeader mqttConnectVariableHeader=new MqttConnectVariableHeader("MQTT",4,false,false,false,0,false,true,60);

        MqttConnectPayload payload=new MqttConnectPayload("test",null, (byte[]) null,null,null);
        MqttConnectMessage mqttConnectMessage=new MqttConnectMessage(mqttFixedHeaderBack,mqttConnectVariableHeader,payload);
        System.out.println(channel);
        System.out.println(channel.isActive());
        channel.writeAndFlush(mqttConnectMessage);
        Thread.sleep(3000);
        subscribe("/home");
        publish("/home/1","111");
    }


    @PostConstruct
    public void init() throws URISyntaxException, InterruptedException {
        new Thread(() -> {

            try {
                start();
            } catch (InterruptedException | URISyntaxException | ExecutionException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void publish(String topic,String msg) throws ExecutionException, InterruptedException {
        MqttClientMsgBack.publish(topic,msg,channel);
    }
    public void subscribe(String topic ) throws ExecutionException, InterruptedException {
        System.out.println("订阅topic"+topic);
        MqttClientMsgBack.subscribe(topic,channel);
    }
}
