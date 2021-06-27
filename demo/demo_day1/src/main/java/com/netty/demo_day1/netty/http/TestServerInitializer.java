package com.netty.demo_day1.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 向管道加入处理器
        // 获取管道
        Channel channel = ch.pipeline().channel();
        ChannelPipeline pipeline = ch.pipeline();
        //　加入netty 提供的httpServerCodec(编解码器)
        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());
        // 编写自定义的Handler
        pipeline.addLast("MyHttpServerHandler",new TestHttpServerHandler());
    }
}
