package com.data.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyHttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipe = ch.pipeline();
		
//		pipe.addLast("decode", new HttpRequestDecoder());
//		pipe.addLast("encode", new HttpRequestEncoder());
		pipe.addLast("codec", new HttpServerCodec());
        pipe.addLast("aggregator", new HttpObjectAggregator(512*1024));
        pipe.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
		pipe.addLast("request", new NettyHttpServerHandler());
		
	}
	

}
