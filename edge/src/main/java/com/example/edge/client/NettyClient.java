package com.example.edge.client;

import cn.hutool.json.JSONObject;
import com.example.edge.cache.Cache;
import com.example.edge.config.NettyConfig;
import com.example.edge.db.DataStorageContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Data;
import model.MsgModel;
import netty.AbstractNettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Component
public class NettyClient extends AbstractNettyClient {
    @Autowired
    private ClientHandler handler;
    private Channel channel;
    @Autowired
    private ClientInitializer initializer;
    public NettyClient(ClientInitializer initializer){
        super(initializer);
    }

    public Channel getChannel() {
        return channel;
    }

    public void start1() throws InterruptedException, URISyntaxException {
        String delimiter = "_$";
        EventLoopGroup group = new NioEventLoopGroup();
        //netty基本操作，启动类
        Bootstrap boot = new Bootstrap();
        boot.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        boot.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(group)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("http-codec",new HttpClientCodec());
                        pipeline.addLast("aggregator",new HttpObjectAggregator(1024*1024*10));
                        pipeline.addLast("hookedHandler", handler);
                        //解决粘包拆包问题
                        //pipeline.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer(delimiter.getBytes())));
                        //pipeline.addLast(new DelimiterBasedMessageEncoder(delimiter));
                    }
                });
        //websocke连接的地址
        URI websocketURI = new URI("ws://localhost:58080/webSocket");
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        //进行握手
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
        //客户端与服务端连接的通道，final修饰表示只会有一个
        try {
            final Channel channel = boot.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
            this.channel=channel;
        }catch (Exception e){
            e.printStackTrace();
            channel.closeFuture().sync();
            group.shutdownGracefully();
            return;

        }
        ClientHandler handler = (ClientHandler) channel.pipeline().get("hookedHandler");
        handler.setHandshaker(handshaker);
        handshaker.handshake(channel);
        //阻塞等待是否握手成功
        handler.handshakeFuture().sync();
        System.out.println("握手成功");
        //给服务端发送的内容，如果客户端与服务端连接成功后，可以多次调用这个方法发送消息
    }

    public void start() throws URISyntaxException, InterruptedException {
        URI websocketURI = new URI("ws://localhost:58080/webSocket");
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        //进行握手
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
        //客户端与服务端连接的通道，final修饰表示只会有一个
        try {
            final Channel channel = bootstrap.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
            this.channel=channel;
        }catch (Exception e){
            e.printStackTrace();
            channel.closeFuture().sync();
            workerGroup.shutdownGracefully();
            return;

        }
        //ClientHandler handler = (ClientHandler) channel.pipeline().get("hookedHandler");
        handler.setHandshaker(handshaker);
        handshaker.handshake(channel);
        //阻塞等待是否握手成功
        handler.handshakeFuture().sync();
        System.out.println("握手成功");
        NettyConfig.setChannel(channel);
        handleCache(channel);

    }


    @PostConstruct
    public void init() throws URISyntaxException, InterruptedException {
        new Thread(() -> {

            try {
                start();
            } catch (InterruptedException | URISyntaxException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void handleCache(Channel channel){
        //重连时将缓存中的数据同步到云端
        Map device=Cache.getMap();
        if(device!=null){
            Set set=device.entrySet();
            Iterator<Map.Entry<String, Map>> iterator = set.iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Map> entry= iterator.next();
                Map type= (Map) device.get(entry.getKey());
                Set typeSet=type.entrySet();
                Iterator<Map.Entry<String,MsgModel>> typeIterator = typeSet.iterator();
                while (typeIterator.hasNext()){
                    Map.Entry<String,MsgModel> typeEntry=typeIterator.next();
                    channel.writeAndFlush(typeEntry.getValue());
                    type.remove(typeEntry.getKey());
                }
                device.remove(entry.getKey());
            }

        }
    }

}
