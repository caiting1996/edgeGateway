package com.example.mqttserver.mqttClient;

import io.netty.channel.Channel;
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
        Thread.sleep(3000);
        //subscribe("/home");
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
