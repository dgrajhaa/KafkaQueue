package com.data.netty.server;

import java.net.URI;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyHttpServer {

	public static void main(String args[]) throws Exception {
		EventLoopGroup master = new NioEventLoopGroup(1);
		EventLoopGroup slave = new NioEventLoopGroup();
		URI uri = new URI("http://localhost:8090/");
		 Properties props = new Properties();
		 props.put("bootstrap.servers", "localhost:8090");
		 props.put("acks", "all");
		 props.put("retries", 0);
		 props.put("batch.size", 16384);
		 props.put("linger.ms", 1);
		 props.put("buffer.memory", 33554432);
		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
		 Producer<String, String> producer = new KafkaProducer<>(props);
//		 Producer<String, byte[]> producer = new KafkaProducer<>(props, new StringSerializer(), new ByteArraySerializer());
//		 ProducerConfig kafkaConfig = new ProducerConfig(props);
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(master, slave).channel(NioServerSocketChannel.class).childHandler(new NettyHttpServerChannelInitializer());
			Channel ch = b.bind(uri.getHost(), uri.getPort()).sync().channel();
			System.out.println("listening");
			ch.closeFuture().sync();
			System.out.println("closed");
		}
		finally {
			master.shutdownGracefully();
			slave.shutdownGracefully();
		}
	}
	
}
