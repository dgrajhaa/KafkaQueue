package com.data.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyHttpClientChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipe = ch.pipeline();
		pipe.addLast("codec", new HttpClientCodec());
        pipe.addLast("aggregator", new HttpObjectAggregator(512*1024));
        pipe.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
		pipe.addLast("response", new NettyHttpClientHandler());
	}

}
