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

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

public abstract class AbstractNettyServer {

    protected ServerBootstrap bootstrap;
    protected EventLoopGroup workerGroup;
    protected EventLoopGroup bossGroup;
    protected NettyInitializer initializer;

    public AbstractNettyServer(NettyInitializer initializer){
        this.initializer=initializer;
        initNetty();
    }

    protected void initNetty() {
        workerGroup = new NioEventLoopGroup();
        bossGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(initializer);

    }

    protected void destroy() throws InterruptedException {
        if(bossGroup != null){
            bossGroup.shutdownGracefully().sync();
        }
        if(workerGroup != null){
            workerGroup.shutdownGracefully().sync();
        }
    }
}
