package com.netty.learn.time.server.netty.echo.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Zhang Anjin
 * @description client handler
 * @date 2023/11/14 22:18
 */
@Slf4j
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private int counter;
    private static final String ECHO_REQ = "Hi, Li Linfeng. Welcome to Netty.$_";

    public EchoClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("This is {} times receive server:[{}]", ++counter, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
