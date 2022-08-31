package com.example.cloud.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import model.DeviceModel;
import model.MsgModel;
import netty.AbstractNettyServer;
import netty.NettyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import util.JsonUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Component
public class NettyServer extends AbstractNettyServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    /**
     * webSocket协议名
     */
    private static final String WEBSOCKET_PROTOCOL = "WebSocket";

    /**
     * 端口号
     */
    @Value("${webSocket.netty.port:58080}")
    private int port;

    /**
     * webSocket路径
     */
    @Value("${webSocket.netty.path:/webSocket}")
    private String webSocketPath;

    @Autowired
    private ServerHandler webSocketHandler;
    @Autowired
    private ServerInitializer initializer;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    public NettyServer(ServerInitializer initializer) {
        //super.initializer=initializer;
        super(initializer);
    }

    /**
     * 启动
     * @throws InterruptedException
     */
    private void start() throws InterruptedException{
        bootstrap.localAddress(new InetSocketAddress(port));
        ChannelFuture channelFuture = bootstrap.bind().sync();
        log.info("Server started and listen on:{}",channelFuture.channel().localAddress());

        Map map= new HashMap();
        map.put("key","value");
        DeviceModel deviceModel=new DeviceModel();
        deviceModel.setDeviceId("111");
        deviceModel.setProductId("222");
        deviceModel.setDeviceInfo(map);
        MsgModel msgModel=new MsgModel();
        msgModel.setType("upload");
        msgModel.setDeviceModel(deviceModel);
        System.out.println(JsonUtil.obj2String(msgModel));
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
