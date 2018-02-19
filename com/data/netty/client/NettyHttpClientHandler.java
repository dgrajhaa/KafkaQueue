package com.data.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;

public class NettyHttpClientHandler extends SimpleChannelInboundHandler<HttpObject>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject obj) throws Exception {
		System.out.println("client received");
		if (obj instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) obj;
            System.out.println("STATUS: " + response.getStatus());
            ByteBuf b = response.content();
            System.out.print("RESPONSE : ");
            for (int i = 0; i < b.capacity(); i ++) {
                byte by = b.getByte(i);
                System.out.print((char) by);
            }
		}
		else {
			ctx.close();
		}
		
	}

}
