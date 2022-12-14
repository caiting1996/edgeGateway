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
        //netty????????????????????????
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
                        //????????????????????????
                        //pipeline.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer(delimiter.getBytes())));
                        //pipeline.addLast(new DelimiterBasedMessageEncoder(delimiter));
                    }
                });
        //websocke???????????????
        URI websocketURI = new URI("ws://localhost:58080/webSocket");
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        //????????????
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
        //???????????????????????????????????????final???????????????????????????
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
        //??????????????????????????????
        handler.handshakeFuture().sync();
        System.out.println("????????????");
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    }

    public void start() throws URISyntaxException, InterruptedException {
        String url="ws://localhost:58080/webSocket?".concat("userId=").concat(NettyConfig.getUserId()).concat("&token=").concat(NettyConfig.getToken());
        URI websocketURI = new URI(url);
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        httpHeaders.add("userId",NettyConfig.getUserId());
        httpHeaders.add("token", NettyConfig.getToken());

        //????????????
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
        //???????????????????????????????????????final???????????????????????????
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
        //??????????????????????????????
        handler.handshakeFuture().sync();
        System.out.println("????????????");
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
        //?????????????????????????????????????????????
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
