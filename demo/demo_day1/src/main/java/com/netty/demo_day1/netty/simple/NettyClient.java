package com.netty.demo_day1.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        // 事件循环组
        EventLoopGroup eventExecutors = new NioEventLoopGroup();
        // 创建客户端启动
        Bootstrap bootstrap = new Bootstrap();
        try{
            bootstrap.group(eventExecutors) // 设置线程组
                    .channel(NioSocketChannel.class) // 设置客户端通道的实现类
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyClientHandler());//进入处理器
                        }
                    });
            System.out.println("客户端 is ready");

            //启动客户端并连接服务器 关于channelFuture涉及到netty的异步模型
            ChannelFuture sync = bootstrap.connect("127.0.0.1", 6668).sync();
            // 给关闭通道进行监听
            sync.channel().closeFuture().sync();
        }finally {
            eventExecutors.shutdownGracefully();
        }
    }
}
