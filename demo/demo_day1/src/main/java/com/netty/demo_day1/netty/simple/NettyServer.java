package com.netty.demo_day1.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        // 创建 BossGroup 和 WorkerGroup
        /**
         * 说明
         *  创建的BossGroup与WorkerGroup都为线程组且都是无限循环
         *  BossGroup与WorkerGroup所含有的子线程(NioEventLoop)的个数为 默认实际的 cpu核数*2
         *  BossGroup 只处理连接请求
         *  WorkerGroup 只处理读写请求(业务处理)
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            // 创建服务器端启动对象 并配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)// 设置线程组
                    .channel(NioServerSocketChannel.class) // 使用NioServerSocketChannel实现通道通讯
                    .option(ChannelOption.SO_BACKLOG,128) // 设置线程组最大连接数
                    .childOption(ChannelOption.SO_KEEPALIVE,true)// 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 创建通道的匿名对象
                        // 设置pipeline处理器
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.hashCode(); // 可使用集合管理socketChannel，再推送消息时，将业务加入到各个channel对应的NIOEventLoop的taskQueue或scheduleTaskQueue
                            channel.pipeline().addLast(new NettyHandler());
                        }
                    });// 为workerGroup的EventGroup设置处理器
            System.out.println("。。。。。 服务器 is ready 。。。。");
            // 绑定端口且同步，生成一个ChannelFuture对象 此处启动服务器
            ChannelFuture sync = serverBootstrap.bind(6668).sync();
            sync.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (sync.isSuccess()){
                        System.out.println("监听成功");
                    }else {
                        System.out.println("失败");
                    }
                }
            });
            // 监听关闭的通道
            sync.channel().closeFuture().sync();
        }finally {
            // 关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
