package com.netty.learn.time.server.aio;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author Zhang Anjin
 * @description 与客户端建立完成连接的回调
 * @date 2023/10/24 21:22
 */
@Slf4j
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        //调用 AsynchronousServerSocketChannel 可以接收成千上万个客户端
        //所以继续调用它的 accept 方法，接收其他的客户端连接 最终形成一个循环
        //每当接收一个客户端读连接成功之后，再异步接收一个新的客户端连接
        attachment.socketChannel.accept(attachment, this);
        //缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //第二个 buffer 通知回调的入参
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable throwable, AsyncTimeServerHandler attachment) {
        log.error("", throwable);
        attachment.countDownLatch.countDown();
    }
}
