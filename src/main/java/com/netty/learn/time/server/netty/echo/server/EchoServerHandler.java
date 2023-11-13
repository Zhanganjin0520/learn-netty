package com.netty.learn.time.server.netty.echo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Zhang Anjin
 * @description Echo Server Handler
 * @date 2023/11/13 21:37
 */
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    private int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        log.info("This is {} times receive client :[{}]", ++counter, body);
        body += "$_";
        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
        ctx.writeAndFlush(echo);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("error:{}", cause.getMessage());
        ctx.close();
    }
}
