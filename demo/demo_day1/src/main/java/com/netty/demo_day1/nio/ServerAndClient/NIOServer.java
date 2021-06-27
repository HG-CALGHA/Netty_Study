package com.netty.demo_day1.nio.ServerAndClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) {
        //创建 ServerSocketChannel -> ServerSocket
        try {
            ServerSocketChannel ServerSocketOpen = ServerSocketChannel.open();
            // 得到Selector对象  实际为WindowsSelectorImpl
            Selector SelectorOpen = Selector.open();
            // 绑定监听端口
            ServerSocketOpen.socket().bind(new InetSocketAddress(6666));
            // 设置为非堵塞
            ServerSocketOpen.configureBlocking(false);
            // 将serverSocket注册到 selector 关心事件为SelectionKey.OP_ACCEPT
            ServerSocketOpen.register(SelectorOpen, SelectionKey.OP_ACCEPT);

            System.out.println("注册前的SelectionKey： " + SelectorOpen.keys().size());

            while (true){
                // 等待连接 如无连接则跳过该次监听 超时时间为1s
                if (SelectorOpen.select(1000) == 0){
                    System.out.println("服务器等待超时，无连接");
                    continue;
                }
                // 如果有连接,获取selectionKey集合
                // 如果有连接则表示获取到关注事件
                // selectedKeys()获取返回关注事件的集合
                Set<SelectionKey> selectionKeys = SelectorOpen.selectedKeys();
                // 获取selectionKeys对象的迭代器
                Iterator<SelectionKey> KeyIterator = selectionKeys.iterator();
                while (KeyIterator.hasNext()){
                    // 循环获取SelectionKey对象
                    SelectionKey key = KeyIterator.next();
                    //　查看key关联的事件并做对应的处理
                    if(key.isAcceptable()){ // 如为OP_ACCEPT 则代表有人连接
                        // 为对应的客户端生成SocketChannel,且accept()不会阻塞线程
                        SocketChannel SocketChannelAccept = ServerSocketOpen.accept();
                        // 设置为非阻塞
                        SocketChannelAccept.configureBlocking(false);
                        System.out.println("客户端连接成功 生成了一个socketChannel " + SocketChannelAccept.hashCode());
                        // 将当前的SocketChannelAccept注册到selector 关注为OP_READ 同时绑定Channel
                        SocketChannelAccept.register(SelectorOpen,SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                        System.out.println("注册后的SelectionKey： " + SelectorOpen.keys().size());
                    }
                    if (key.isReadable()){ // 如为OP_READ 则代表有人进行读取操作
                        // 通过key 反向获取对应的channel 需要进行强转 key.channel()返回的是SelectableChannel类型
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 获取该channel绑定的buffer 需要进行强转 key.attachment()返回的是Object类型
                        ByteBuffer attachment = (ByteBuffer) key.attachment();
                        // 读取数据到buffer
                        channel.read(attachment);
                        System.out.println("Form 客户端" + new String(attachment.array()));
                    }
                    // 手动从当前集合中移除当前处理过的selectionKey，防止重复操作
                    KeyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
