package com.netty.learn.time.server.aio;

/**
 * @author Zhang Anjin
 * @description AIO Time Server
 * @date 2023/10/24 21:10
 */
public class TimeServer {
    /**
     * @param args .
     */
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        AsyncTimeServerHandler timeServerHandler = new AsyncTimeServerHandler(port);
        new Thread(timeServerHandler, "AIO-AysncTimeServerHandler-001").start();
    }
}
