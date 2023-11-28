package com.netty.learn.time.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @author Zhang Anjin
 * @description msgpack decoder
 * @date 2023/11/28 22:07
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        int length = byteBuf.readableBytes();
        byte[] bytes = new byte[length];
        byteBuf.getBytes(byteBuf.readerIndex(), bytes, 0, length);
        MessagePack messagePack = new MessagePack();
        out.add(messagePack.read(bytes));
    }
}
