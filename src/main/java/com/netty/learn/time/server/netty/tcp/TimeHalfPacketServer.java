package com.netty.learn.time.server.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Zhang Anjin
 * @description tcp 粘包处理
 * @date 2023/11/6 22:29
 */
@Slf4j
public class TimeHalfPacketServer {
    /**
     * bind port
     *
     * @param port .
     */
    public void bind(int port) {
        //配置服务端的 NIO 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        //绑定线程组
        bootstrap.group(bossGroup, workerGroup)
                //设置 Channel 类型
                .channel(NioServerSocketChannel.class)
                //设置 TCP 参数
                .option(ChannelOption.SO_BACKLOG, 1024)
                //配置 Handler
                .childHandler(new ChildChannelHandler());
        try {
            //绑定端口，同步等待成功
            //异步操作的通知回调
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务端监听端口
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("", e);
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8088;
        new TimeHalfPacketServer().bind(port);
    }

    /**
     * handler
     */
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
            ch.pipeline().addLast(new StringDecoder());
            ch.pipeline().addLast(new TimeServerHalfPacketHandler());
        }
    }
}
