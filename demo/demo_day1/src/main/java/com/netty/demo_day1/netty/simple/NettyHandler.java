package com.netty.demo_day1.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 说明
 *  自定义handler需要继承Netty官方定义的HandlerAdapter
 */
public class NettyHandler extends ChannelInboundHandlerAdapter {
    // 读取用户发送的实际数据

    /**
     * @param ctx 上下文对象 包含 管道pipeline，通道channel，地址
     * @param msg 客户端发送的信息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("服务器读取线程" + Thread.currentThread().getName());
//        System.out.println("server ctx = " + ctx);
//        // 将客户端数据转成一个 byteBuf， byteBuf是由Netty提供的
//        ByteBuf buf = (ByteBuf) msg;
//        Channel channel = ctx.channel();
//        ChannelPipeline pipeline = ctx.pipeline();
//        System.out.println("客户端发送的信息是"+buf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户地址： " + channel.remoteAddress() );

        // 用户自定义普通任务
        // 由于每个业务耗时较长因此需要异步执行，再提交到NIOEventLoop的taskQueue
        // ctx.channel().eventLoop().execute() 可写多个但是因为Thread是同一个线程因此堵塞时间是相加的
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("123Hellow NettyClient",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("异常");
                }
            }
        });
        // 用户自定义定时任务 提交到NIOEventLoop的scheduleTaskQueue中
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("1235Hellow NettyClient",CharsetUtil.UTF_8));
                    ctx.writeAndFlush(Unpooled.copiedBuffer("1235Hellow NettyClient",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("异常");
                }
            }
        },5, TimeUnit.SECONDS);
    }
    // 数据读取完毕 返回客户端信息
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 写入数据并进行刷新 一般来说要先进行编码再发送
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hellow NettyClient",CharsetUtil.UTF_8));
    }

    // 处理异常 关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

