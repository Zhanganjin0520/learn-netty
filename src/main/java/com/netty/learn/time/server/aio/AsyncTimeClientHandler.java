package com.netty.learn.time.server.aio;

/**
 * @author Zhang Anjin
 * @description TimeClient 客户端
 * @date 2023/10/26 22:30
 */
public class AsyncTimeClientHandler implements Runnable {
    private String host;
    private Integer port;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {

    }
}
