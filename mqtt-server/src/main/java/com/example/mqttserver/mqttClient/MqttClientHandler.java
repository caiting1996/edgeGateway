package com.example.mqttserver.mqttClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import org.springframework.stereotype.Component;

@Component
public class MqttClientHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        MqttMessage mqttMessage = (MqttMessage) msg;
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        switch (mqttFixedHeader.messageType()) {
            case CONNACK:
                // 连接报文的响应
                System.out.println("连接成功");
                break;
            case SUBACK:
                // 订阅报文的响应
                break;
            case UNSUBACK:
                // 取消订阅报文的响应
                break;
            case PUBLISH:
                MqttClientMsgBack.puback(ctx.channel(),mqttMessage);
                // 收到消息报文
                break;
            case PUBACK:
                // 发布消息报文响应
                // qos = 1的发布才有该回应
                break;
            case PUBREC:
                // qos = 2的发布才参与
                break;
            case PUBREL:
                // qos = 2的发布才参与
                break;
            case PUBCOMP:
                // qos = 2的发布才参与
                break;
            case PINGRESP:
                // ping报文响应
                break;
            default:
                break;
        }
    }
    /**
     * Handler活跃状态，表示连接成功
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与服务端连接成功");
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.CONNECT,false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttConnectVariableHeader mqttConnectVariableHeader=new MqttConnectVariableHeader(MqttVersion.MQTT_3_1.protocolName(),MqttVersion.MQTT_3_1.protocolLevel(),false,false,false,0,false,true,60);

        MqttConnectPayload payload=new MqttConnectPayload("test",null, (byte[]) null,null,null);
        MqttConnectMessage mqttConnectMessage=new MqttConnectMessage(mqttFixedHeaderBack,mqttConnectVariableHeader,payload);
        System.out.println(ctx.channel());
        System.out.println(ctx.channel().isActive());
        ctx.channel().writeAndFlush(mqttConnectMessage);
    }
}
