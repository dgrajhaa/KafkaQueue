package com.data.netty.client;

import java.net.URI;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public class NettyHttpClient {

	public static void main(String args[]) throws Exception {
		URI uri = new URI("http://localhost:8090/");
		
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group).channel(NioSocketChannel.class).handler(new NettyHttpClientChannelInitializer());
			Channel ch = bs.connect(uri.getHost(), uri.getPort()).sync().channel();
			FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
	        request.headers().set(HttpHeaders.Names.HOST, uri.getHost());
	        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
	        request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
			ch.writeAndFlush(request);
			System.out.println("request sent");
            // Wait for the server to close the connection.
            ch.closeFuture().sync();
		}
		finally {
			System.out.println("finally");
			group.shutdownGracefully();
		}
	}
}
