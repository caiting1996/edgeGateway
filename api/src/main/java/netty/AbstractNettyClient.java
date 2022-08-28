package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class AbstractNettyClient {
    protected Bootstrap bootstrap;
    protected ChannelGroup channelGroup;
    protected EventLoopGroup workerGroup;
    protected NettyInitializer initializer;

    public AbstractNettyClient(NettyInitializer initializer) {
        this.initializer=initializer;
        initNetty();
    }
    private void initNetty() {
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.handler(initializer);
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }
}
