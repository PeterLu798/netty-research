package io.netty.example.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.study.server.codec.OrderFrameDecoder;
import io.netty.example.study.server.codec.OrderFrameEncoder;
import io.netty.example.study.server.codec.OrderProtocolDecoder;
import io.netty.example.study.server.codec.OrderProtocolEncoder;
import io.netty.example.study.server.codec.handler.*;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutionException;

public class Server {

    public static void main(String[] args) throws InterruptedException, ExecutionException, CertificateException, SSLException {
        /**
         * 专门用来处理业务handler的线程池
         */
        UnorderedThreadPoolEventExecutor executors = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
        NioEventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));
        serverBootstrap.group(boss, worker);

        MetricHandler metricHandler = new MetricHandler();
        //流量控制
        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(new NioEventLoopGroup(), 100 * 1024 * 1024, 100 * 1024 * 1024);
        //ip过滤黑名单
        IpFilterHandler ipFilterHandler = new IpFilterHandler();
        //auth验证
        AuthHandler authHandler = new AuthHandler();
        //SSL加密通信
        //自签证书
        SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
        System.out.println(selfSignedCertificate.certificate());
        //SSL上下文
        SslContext sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey()).build();

        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

//                pipeline.addLast("ipFilterHandler", ipFilterHandler);

                pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

                pipeline.addLast("idleCheck", new ServerIdleCheckHandler());

                pipeline.addLast("TSHandler", globalTrafficShapingHandler);

                pipeline.addLast("sslHandler", sslContext.newHandler(ch.alloc()));

                pipeline.addLast("orderFrameDecoder", new OrderFrameDecoder());
                pipeline.addLast("orderFrameEncoder", new OrderFrameEncoder());
                pipeline.addLast("orderProtocolEncoder", new OrderProtocolEncoder());
                pipeline.addLast("orderProtocolDecoder", new OrderProtocolDecoder());

                pipeline.addLast("authHandler", authHandler);

                pipeline.addLast("metricHandler", metricHandler);

                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                //开启异步延迟功能，提高吞吐量。
                // consolidateWhenNoReadInProgress 设为true表示开启异步延迟，
                // explicitFlushAfterFlushes设为5表示5个请求flush一次
                pipeline.addLast("flushConsolidationHandler", new FlushConsolidationHandler(5, true));

                pipeline.addLast(executors, new OrderServerProcessHandler());
            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();

        channelFuture.channel().closeFuture().get();

    }
}
