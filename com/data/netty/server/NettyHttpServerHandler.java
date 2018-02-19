package com.data.netty.server;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.json.JSONObject;

import com.tutorial.protobuf.EmployeeProto;
import com.tutorial.protobuf.EmployeeProto.Employee;
import com.tutorial.protobuf.EmployeeProto.Employee.Builder;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

public class NettyHttpServerHandler extends SimpleChannelInboundHandler<Object>{
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof FullHttpRequest) {
			System.out.println("fullhttprequest");
			final FullHttpRequest req = (FullHttpRequest) msg;
			@SuppressWarnings("deprecation")
			String uri = req.getUri();
			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
			Map<String, List<String>> params = queryStringDecoder.parameters();
			JSONObject job = new JSONObject();
            if (!params.isEmpty()) {
                for (Entry<String, List<String>> p: params.entrySet()) {
                    String key = p.getKey();
                    List<String> vals = p.getValue();
                    for (String val : vals) {
                    	job.put(key, val);
                    }
                }
            }
            System.out.println(job);
            String responseMessage = "Request Added to kafka";
//            convert job/URL Params to proto object
            Builder empbuild = EmployeeProto.Employee.newBuilder();
            empbuild.setFirstname("Rajarathinam");
            empbuild.setLastname("Gopinath");
            empbuild.setId(10);
            empbuild.setSalary(100000);
            
            Employee raja = empbuild.build();
//            raja.writeTo(output);
//            put it in kafka queue
            ByteArraySerializer bytebuf = new ByteArraySerializer();
            byte[] serializedProto = bytebuf.serialize("protoTopic", raja.toByteArray());
//            put this serialized proto into kafka producer
            
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(responseMessage.getBytes()));
            if (HttpHeaders.isKeepAlive(req))
            {
              response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE );
            }
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, responseMessage.length());
            ctx.write(response);   
//            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
//		else if (msg instanceof HttpRequest) {
//			HttpRequest req = (HttpRequest) msg;
//			System.out.println("HttpRequest");
//			String responseMessage = "Request Added to kafka";
//            
//            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(responseMessage.getBytes()));
//            if (HttpHeaders.isKeepAlive(req))
//            {
//              response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE );
//            }
//            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
//            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, responseMessage.length());
////            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//            ctx.write(response);
//            channelReadComplete(ctx);
//		}
//		else if(msg instanceof HttpContent) {
//			System.out.println("HttpContent");
//		}
		else {
			System.out.println("else");
//			super.channelRead(ctx, msg);
//			channelReadComplete(ctx);
		}
		channelReadComplete(ctx);
		
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			ctx.writeAndFlush(new DefaultFullHttpResponse( HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(cause.getMessage().getBytes())));
	}

}
