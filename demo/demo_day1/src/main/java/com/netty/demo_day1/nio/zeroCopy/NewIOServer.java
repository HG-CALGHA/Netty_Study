package com.netty.demo_day1.nio.zeroCopy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @ClassName NewIOServer
 * @Description TODO
 * @Author CGH
 * @Date 2021/6/22 19:34
 * @Version 1.0
 */
public class NewIOServer {

    public static void main(String[] args) {
        try {
            // 设置端口
            InetSocketAddress inetAddress = new InetSocketAddress(7001);
            // 创建ServerSocketChannel管道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 获取ServerSocket对象
            ServerSocket socket = serverSocketChannel.socket();
            // 绑定端口
            socket.bind(inetAddress);
            // 创建缓冲区
            ByteBuffer allocate = ByteBuffer.allocate(4096);

            while (true){
                // 与客户端对应的socketChannel管道
                SocketChannel socketChannel = serverSocketChannel.accept();
                int reads = 0;
                while (reads != -1){
                    // 读取管道数据
                     reads = socketChannel.read(allocate);
                }
                // 倒带 position 为0 mark 作废
                allocate.rewind();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
