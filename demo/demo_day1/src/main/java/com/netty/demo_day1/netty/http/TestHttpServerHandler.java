package com.netty.demo_day1.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * 说明
 *  SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter的子类
 *  HttpObject 客户端和服务器相互通讯的数据封装成 HttpObject
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 判断 msg 是不是 HttpRequest请求
        if(msg instanceof HttpRequest){
            // 每个访问的浏览器的TestHttpServerHandler和pipeline都是不同
            System.out.println("pipeline: " + ctx.pipeline().hashCode() + " TestHttpServerHandler:" + this.hashCode());
            System.out.println("msg 类型 = " + msg.getClass());
            System.out.println("客户端地址 = " + ctx.channel().remoteAddress());
            HttpRequest request = (HttpRequest) msg;
            // 获取URI
            URI uri = new URI(request.getUri());
            // 过滤指定资源
            if("/favico.icon".equals(uri.getPath())){
                System.out.println("非法请求，不做响应");
                return;
            }
            // 回复信息到浏览器 [需满足HTTP协议]
            ByteBuf buf = Unpooled.copiedBuffer("hello 我是服务器", CharsetUtil.UTF_8);
            //　构建http的相应即httpResponse
            HttpResponse defaultHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
            // 设置头信息
            // 传输类型
            defaultHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"test/plain");
            // 传输数据的大小
            defaultHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
            //　发送数据
            ctx.writeAndFlush(defaultHttpResponse);
        }

    }
}
