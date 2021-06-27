package com.netty.demo_day1.nio.groupChat;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @ClassName GroupChatServer
 * @Description TODO
 * @Author CGH
 * @Date 2021/6/21 10:39
 * @Version 1.0
 */
public class GroupChatServer {
    // 定义常用属性
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private static final int PORT = 6667;

    // 使用构造器完成初始化
    public GroupChatServer() {
        try {
            // 获取选择器
            selector = Selector.open();
            // 获取ServerSocketChannel
            serverSocketChannel = ServerSocketChannel.open();
            // 绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(GroupChatServer.PORT));
            // 设置非阻塞状态
            serverSocketChannel.configureBlocking(false);
            // 注册到selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 监听方法
    public void listen() {
        try {
            while (true) {
                if (selector.select(2000) > 0) {
                    // 遍历得到SelectionKey集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        // 取出一个SelectionKey
                        SelectionKey next = iterator.next();
                        // 判断SelectionKey的状态
                        if (next.isAcceptable()) {
                            // 获取SocketChannel对象
                            SocketChannel accept = serverSocketChannel.accept();
                            // 设置非阻塞
                            accept.configureBlocking(false);
                            // 将SocketChannel注册到selector上
                            accept.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
//                            accept.register(selector, SelectionKey.OP_WRITE, ByteBuffer.allocate(1024));
                            // 提示上线
                            System.out.println(accept.getRemoteAddress() + " 上线");
                        }
                        if (next.isReadable()) {
                            readData(next);
                        }
                        // 删除当前key
                        iterator.remove();
                    }
                } else {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    // 客户端读取数据方法
    public void readData(SelectionKey key) {
        // 定义SocketChannel
        SocketChannel socketChannel = null;
        try {
            //获取关联的channel
            socketChannel = (SocketChannel) key.channel();
            // 创建缓存区
            ByteBuffer allocate = (ByteBuffer) key.attachment();
            // 将数据读取到缓冲区
            int read = socketChannel.read(allocate);
            if (read > 0) {
                String msg = new String(allocate.array());
                // 打印读取的数据
                System.out.println("from 客户端 : "+msg);
                // 向其他客户端发送信息
                sendInfoToOtherClients(msg,socketChannel);
            }

        } catch (IOException e) {
            try {
                System.out.println(socketChannel.getRemoteAddress() + "已离线");
                // 取消注册
                key.cancel();
                // 关闭管道
                socketChannel.close();
            } catch (IOException ioException) {
                e.printStackTrace();
            }
        }

    }

    // 转发消息
    private void sendInfoToOtherClients(String msg, SocketChannel socketChannel) {
        System.out.println("服务器转发消息中....");
        // 遍历所有注册到selector上的SocketChannel 并排除socketChannel
        for(SelectionKey key:selector.keys()) {
            Channel channel =  key.channel();
            if (!(channel).equals(socketChannel) && channel instanceof SocketChannel) {
                try {
                    SocketChannel channel1 = (SocketChannel) channel;
                    ByteBuffer wrap = ByteBuffer.wrap(msg.getBytes());
                     channel1.write(wrap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
