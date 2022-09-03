package com.example.edge.config;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NettyConfig {
    private static final String userId="test";
    private static final String token="token";
    /**
     * 定义一个channel组，管理所有的channel
     * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(
            GlobalEventExecutor.INSTANCE);

    /**
     * 存放设备与Chanel的对应信息，用于给指定设备发送消息
     */
    private static ConcurrentHashMap<Channel,List> channelTopicMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, List> topicMap=new ConcurrentHashMap<>();
    /**
     * 存放websocket通道
     */
    private static Channel channel;
    private NettyConfig() {}

    /**
     * 获取channel组
     * @return
     */
    public static ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    /**
     * 获取用户channel map
     * @return
     * @param
     */
    public static ConcurrentHashMap<String, List> getTopicMap(){
        return topicMap;
    }

    public static ConcurrentHashMap<Channel, List> getChannelTopicMap() {
        return channelTopicMap;
    }

    public static Channel getChannel(){
        return channel;
    }

    public static void setChannel(Channel channel) {
        NettyConfig.channel = channel;
    }

    public static String getToken() {
        return token;
    }

    public static String getUserId() {
        return userId;
    }
}
