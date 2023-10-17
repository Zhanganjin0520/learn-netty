package com.netty.learn.time.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Zhang Anjin
 * @description BIO TimeServer
 * @date 2023/10/17 21:11
 */
@Slf4j
public class TimeServer {
    /**
     * @param args .
     */
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (null != args && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            log.info("The time server is start in port:{}", port);
            Socket socket = null;
            while (true) {
                socket = server.accept();
                new Thread(new TimeServerHandler(socket)).start();
            }
        } finally {
            if (server != null) {
                log.info("The time server close");
                server.close();
                server = null;
            }
        }

    }
}
