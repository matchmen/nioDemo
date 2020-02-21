package com.mt.nio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * author: liqm
 * 2020-02-21
 */
public class IOServer {

    private final static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {

        //绑定端口
        ServerSocket serverSocket = new ServerSocket(9999);

        System.out.println("开启9999端口服务");

        while (true){
            //获取新连接
            Socket acceptConnection = serverSocket.accept(); //阻塞1---> 如果没有连接，accept()会一直等待，直到来了新连接

            System.out.println("收到新连接");

            threadPoolExecutor.submit(() -> {

                InputStream inputStream = acceptConnection.getInputStream();

                byte[] request = new byte[4096];

                inputStream.read(request); //阻塞2--->  如果没有数据,read()会一直等待，直到来了数据

                System.out.println(new String(request));

                inputStream.close();

                return null;

            });
        }

    }
}
