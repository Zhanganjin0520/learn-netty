package com.netty.learn.time.server.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * @author Zhang Anjin
 * @description 读取完成的回调
 * @date 2023/10/24 22:05
 */
@Slf4j
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private final AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel result) {
        this.channel = result;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        String req = new String(body, StandardCharsets.UTF_8);
    }

    @Override
    public void failed(Throwable throwable, ByteBuffer byteBuffer) {
        try {
            this.channel.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
