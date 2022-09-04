package com.example.edge.server;


import api.MessageService;
import com.example.edge.cache.Cache;
import com.example.edge.config.NettyConfig;
import com.example.edge.db.DataStorageContext;
import enums.MsgType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.mqtt.*;
import model.MsgModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
@Component
public class BootMqttMsgBack {
    private static final Logger log = LoggerFactory.getLogger(BootMqttMsgBack.class);
    @Autowired
    private MessageService messageService;

    /**
     * 	确认连接请求
     * @param channel
     * @param mqttMessage
     */
    public void connack (Channel channel, MqttMessage mqttMessage) {
        MqttConnectMessage mqttConnectMessage = (MqttConnectMessage) mqttMessage;
        MqttFixedHeader mqttFixedHeaderInfo = mqttConnectMessage.fixedHeader();
        MqttConnectVariableHeader mqttConnectVariableHeaderInfo = mqttConnectMessage.variableHeader();

        //	构建返回报文， 可变报头
        MqttConnAckVariableHeader mqttConnAckVariableHeaderBack = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, mqttConnectVariableHeaderInfo.isCleanSession());
        //	构建返回报文， 固定报头
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.CONNACK,mqttFixedHeaderInfo.isDup(), MqttQoS.AT_MOST_ONCE, mqttFixedHeaderInfo.isRetain(), 0x02);
        //	构建CONNACK消息体
        MqttConnAckMessage connAck = new MqttConnAckMessage(mqttFixedHeaderBack, mqttConnAckVariableHeaderBack);
        log.info("back--"+connAck.toString());
        channel.writeAndFlush(connAck);
    }

    /**
     * 	根据qos发布确认
     * @param channel
     * @param mqttMessage
     */
    public void puback (Channel channel, MqttMessage mqttMessage) throws ExecutionException, InterruptedException, IOException {
        MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) mqttMessage;
        MqttFixedHeader mqttFixedHeaderInfo = mqttPublishMessage.fixedHeader();
        MqttQoS qos = (MqttQoS) mqttFixedHeaderInfo.qosLevel();
        byte[] headBytes = new byte[mqttPublishMessage.payload().readableBytes()];
        mqttPublishMessage.payload().readBytes(headBytes);
        String data = new String(headBytes);
        System.out.println("publish data--"+data);
        MqttPublishVariableHeader mqttPublishVariableHeaderInfo=mqttPublishMessage.variableHeader();
        MsgModel msgModel= JsonUtil.string2Obj(data,MsgModel.class);
        System.out.println(msgModel);
        if (msgModel !=null){
            System.out.println(msgModel);
            messageService.receiveMsg(msgModel);
        }
        switch (qos) {
            case AT_MOST_ONCE: 		//	至多一次
                break;
            case AT_LEAST_ONCE:		//	至少一次
                //	构建返回报文， 可变报头
                MqttMessageIdVariableHeader mqttMessageIdVariableHeaderBack = MqttMessageIdVariableHeader.from(mqttPublishMessage.variableHeader().packetId());
                //	构建返回报文， 固定报头
                MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.PUBACK,mqttFixedHeaderInfo.isDup(), MqttQoS.AT_MOST_ONCE, mqttFixedHeaderInfo.isRetain(), 0x02);
                //	构建PUBACK消息体
                MqttPubAckMessage pubAck = new MqttPubAckMessage(mqttFixedHeaderBack, mqttMessageIdVariableHeaderBack);
                log.info("back--"+pubAck.toString());
                channel.writeAndFlush(pubAck);
                break;
            case EXACTLY_ONCE:		//	刚好一次
                //	构建返回报文， 固定报头
                MqttFixedHeader mqttFixedHeaderBack2 = new MqttFixedHeader(MqttMessageType.PUBREC,false, MqttQoS.AT_LEAST_ONCE,false,0x02);
                //	构建返回报文， 可变报头
                MqttMessageIdVariableHeader mqttMessageIdVariableHeaderBack2 = MqttMessageIdVariableHeader.from(mqttPublishMessage.variableHeader().packetId());
                MqttMessage mqttMessageBack = new MqttMessage(mqttFixedHeaderBack2,mqttMessageIdVariableHeaderBack2);
                log.info("back--"+mqttMessageBack.toString());
                channel.writeAndFlush(mqttMessageBack);
                break;
            default:
                break;
        }
    }

    /**
     * 	发布完成 qos2
     * @param channel
     * @param mqttMessage
     */
    public void pubcomp (Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        //	构建返回报文， 固定报头
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.PUBCOMP,false, MqttQoS.AT_MOST_ONCE,false,0x02);
        //	构建返回报文， 可变报头
        MqttMessageIdVariableHeader mqttMessageIdVariableHeaderBack = MqttMessageIdVariableHeader.from(messageIdVariableHeader.messageId());
        MqttMessage mqttMessageBack = new MqttMessage(mqttFixedHeaderBack,mqttMessageIdVariableHeaderBack);
        log.info("back--"+mqttMessageBack.toString());
        channel.writeAndFlush(mqttMessageBack);
    }

    /**
     * 	订阅确认
     * @param channel
     * @param mqttMessage
     */
    public void suback(Channel channel, MqttMessage mqttMessage) {
        MqttSubscribeMessage mqttSubscribeMessage = (MqttSubscribeMessage) mqttMessage;
        MqttMessageIdVariableHeader messageIdVariableHeader = mqttSubscribeMessage.variableHeader();
        //	构建返回报文， 可变报头
        MqttMessageIdVariableHeader variableHeaderBack = MqttMessageIdVariableHeader.from(messageIdVariableHeader.messageId());
        Set<String> topics = mqttSubscribeMessage.payload().topicSubscriptions().stream().map(mqttTopicSubscription -> mqttTopicSubscription.topicName()).collect(Collectors.toSet());
        //log.info(topics.toString());
        List<Integer> grantedQoSLevels = new ArrayList<>(topics.size());
        for (int i = 0; i < topics.size(); i++) {
            grantedQoSLevels.add(mqttSubscribeMessage.payload().topicSubscriptions().get(i).qualityOfService().value());
        }
        for (String topic:topics){
            System.out.println(topic);
            List<Channel> list= NettyConfig.getTopicMap().getOrDefault(topic, new ArrayList<Channel>());
            list.add(channel);
            NettyConfig.getTopicMap().put(topic,list);
            log.info("topic已绑定channel",topic);
            System.out.println(NettyConfig.getTopicMap().get(topic));
            List<String> topicList=NettyConfig.getChannelTopicMap().getOrDefault(channel, new ArrayList<String>());
            topicList.add(topic);
            NettyConfig.getChannelTopicMap().put(channel,topicList);

        }
        System.out.println(NettyConfig.getChannelTopicMap());
        System.out.println(NettyConfig.getTopicMap());
        //	构建返回报文	有效负载
        MqttSubAckPayload payloadBack = new MqttSubAckPayload(grantedQoSLevels);
        //	构建返回报文	固定报头
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 2+topics.size());
        //	构建返回报文	订阅确认
        MqttSubAckMessage subAck = new MqttSubAckMessage(mqttFixedHeaderBack,variableHeaderBack, payloadBack);
        log.info("back--"+subAck.toString());
        channel.writeAndFlush(subAck);
    }

    /**
     * 	取消订阅确认
     * @param channel
     * @param mqttMessage
     */
    public void unsuback(Channel channel, MqttMessage mqttMessage) {
        log.info(mqttMessage.toString());
        MqttUnsubscribeMessage mqttUnsubscribeMessage=(MqttUnsubscribeMessage) mqttMessage;
        MqttUnsubscribePayload payload=mqttUnsubscribeMessage.payload();
        for (String topic :payload.topics()){
            List<Channel> list=NettyConfig.getTopicMap().get(topic);
            list.remove(channel);
            NettyConfig.getTopicMap().put(topic,list);
        }


        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        //	构建返回报文	可变报头
        MqttMessageIdVariableHeader variableHeaderBack = MqttMessageIdVariableHeader.from(messageIdVariableHeader.messageId());
        //	构建返回报文	固定报头
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 2);
        //	构建返回报文	取消订阅确认
        MqttUnsubAckMessage unSubAck = new MqttUnsubAckMessage(mqttFixedHeaderBack,variableHeaderBack);
        log.info("back--"+unSubAck.toString());
        channel.writeAndFlush(unSubAck);
    }

    /**
     * 	心跳响应
     * @param channel
     * @param mqttMessage
     */
    public void pingresp (Channel channel, MqttMessage mqttMessage) {
        //	心跳响应报文	11010000 00000000  固定报文
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessage mqttMessageBack = new MqttMessage(fixedHeader);
        log.info("back--"+mqttMessageBack.toString());
        channel.writeAndFlush(mqttMessageBack);
    }
}
