package com.netty.demo_day1.nio.groupChat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @ClassName GroupChatClient
 * @Description TODO
 * @Author CGH
 * @Date 2021/6/21 13:58
 * @Version 1.0
 */
public class GroupChatClient {

    private  final String HOST = "127.0.0.1";
    private final int PORT = 6667;
    private SocketChannel socketChannel = null;
    private Selector selector = null;
    private String userName;

    public GroupChatClient(){
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST,PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            userName = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(userName + " is ok ....");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 向服务器发送消息
    public void sendInfo(String info){
        info = userName + "说" + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 读取从服务器端回复的消息
    public void readInfo(){
        try {
            int select = selector.select();
            if (select > 0){ // 有可用通道
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isReadable()){
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer allocate = ByteBuffer.allocate(1024);
                        channel.read(allocate);
                        String msg = new String(allocate.array());
                        System.out.println(msg.trim());
                    }
                    iterator.remove();
                }
            }else {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroupChatClient groupChatClient = new GroupChatClient();
        new Thread(){
            @Override
            public void run() {
                while (true){
                    groupChatClient.readInfo();
                    try {
                        Thread.currentThread().sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            System.out.println(s);
            groupChatClient.sendInfo(s);
        }
    }
}
