package com.netty.demo_day1.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName BIOServer
 * @Description TODO
 * @Author CGH
 * @Date 2021/6/16 14:26
 * @Version 1.0
 */
public class BIOServer {

    public static void main(String[] args) {
        // 线程池机制

        // 思路
        // 创建线程池
        // 如有客户端连接就创建一个线程，与之通讯
        ExecutorService executorService = Executors.newCachedThreadPool();

        // 创建ServerSocket
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            System.out.println("服务器已启动");
            while (true) {
                System.out.println("等待连接客户端1：");
                // 监听 等待客户端连接
                final Socket accept = serverSocket.accept();
                System.out.println("连接到一个客户端");
                // 创建线程与之通讯
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {

                        // 重写该方法与客户端通讯
                        handler(accept);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 编写方法与客户端通讯x
    public static void handler(Socket socket) {
        InputStream inputStream = null;
        try {
            System.out.println("线程详细信息1： id = "+Thread.currentThread().getId()+"name = "+Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            // 通过socket 获取输入流
            inputStream = socket.getInputStream();
            // 循环读取客户端发送的数据
            while (true){
                System.out.println("线程详细信息2： id = "+Thread.currentThread().getId()+"name = "+Thread.currentThread().getName());
                // 读取数据长度为bytes
                System.out.println("等待读取客户端信息1：");
                int read = inputStream.read(bytes);
                // 判断数据是否读取完毕
                if (read != -1){
                    // 打印数据
                    System.out.println(new String(bytes,0,read));
                }else {
                    // 跳出读取
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("关闭和client的连接");
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
