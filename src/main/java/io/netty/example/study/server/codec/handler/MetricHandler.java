package io.netty.example.study.server.codec.handler;

import com.codahale.metrics.*;
import com.codahale.metrics.jmx.JmxReporter;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
public class MetricHandler extends ChannelDuplexHandler {
    private Map<String, Metric> statisticsMap = new ConcurrentHashMap<>(16);
    private AtomicLong totalConnectionNumber = new AtomicLong();
    private AtomicLong totalReadNumber = new AtomicLong();
    private AtomicLong totalWriteNumber = new AtomicLong();
    private AtomicLong totalExceptionNumber = new AtomicLong();
    private AtomicLong totalIdleStateEventNumber = new AtomicLong();

    {
        statisticsMap.put("totalConnectionNumber", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return totalConnectionNumber.longValue();
            }
        });
        statisticsMap.put("totalReadNumber", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return totalReadNumber.longValue();
            }
        });
        statisticsMap.put("totalWriteNumber", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return totalWriteNumber.longValue();
            }
        });
        statisticsMap.put("totalExceptionNumber", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return totalExceptionNumber.longValue();
            }
        });
        statisticsMap.put("totalIdleStateEventNumber", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return totalIdleStateEventNumber.incrementAndGet();
            }
        });
        MetricRegistry metricRegistry = new MetricRegistry();
        metricRegistry.registerAll("statistics", new MetricSet() {
            @Override
            public Map<String, Metric> getMetrics() {
                return statisticsMap;
            }
        });
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.start(5, TimeUnit.SECONDS);

        JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
        jmxReporter.start();
    }

    /**
     * 统计连接数 - 建立连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        totalConnectionNumber.incrementAndGet();
        super.channelActive(ctx);
    }

    /**
     * 统计连接数 - 断开连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        totalConnectionNumber.decrementAndGet();
        super.channelInactive(ctx);
    }

    /**
     * 收数据统计 - 接受数据次数
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        totalReadNumber.incrementAndGet();
        super.channelRead(ctx, msg);
    }

    /**
     * 写数据总次数
     *
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        totalWriteNumber.incrementAndGet();
        super.write(ctx, msg, promise);
    }

    /**
     * 发生异常数
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        totalExceptionNumber.incrementAndGet();
        super.exceptionCaught(ctx, cause);
    }

    /**
     * IdleStateEvent 触发次数
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            totalIdleStateEventNumber.incrementAndGet();
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }
}
