package com.netty.learn.time.server.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author Zhang Anjin
 * @description 读取完成的 Handler
 * @date 2023/10/24 22:05
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    public ReadCompletionHandler(AsynchronousSocketChannel result) {
    }

    @Override
    public void completed(Integer integer, ByteBuffer byteBuffer) {

    }

    @Override
    public void failed(Throwable throwable, ByteBuffer byteBuffer) {

    }
}
