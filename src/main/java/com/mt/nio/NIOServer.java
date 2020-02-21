package com.mt.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * author: liqm
 * 2020-02-21
 */
public class NIOServer {

    private final static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //绑定端口
        serverSocketChannel.bind(new InetSocketAddress(9999));

        System.out.println("启动9999端口服务");

        Selector selector = Selector.open();

        while (true) {

            SocketChannel newSocketConnection = serverSocketChannel.accept();//非阻塞模式，accept()可能返回null

            System.out.println("新连接" + newSocketConnection);

            if (Objects.isNull(newSocketConnection)) {
                continue;
            }

            newSocketConnection.configureBlocking(false);// 配置为非阻塞,(NIO还是保留了原有阻塞功能)

            newSocketConnection.register(selector, SelectionKey.OP_READ);// 注册一个事件通知机制,READ

            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {

                SelectionKey evnt = iterator.next();

                if (evnt.isReadable()) {//某一个连接有了新数据

                    SocketChannel socketChannel = (SocketChannel) evnt.channel();

                    threadPoolExecutor.submit(()->{

                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        try {
                            socketChannel.read(byteBuffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        byteBuffer.flip();//转换为读模式

                        System.out.println(new String(byteBuffer.array()));

                        socketChannel.close();

                        return null;

                    });
                }

            }
        }


    }


}
