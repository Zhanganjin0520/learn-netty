package com.netty.learn.time.server.aio;

/**
 * @author Zhang Anjin
 * @description AIO TimeClient
 * @date 2023/10/26 22:23
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHandler-001").start();
    }
}
