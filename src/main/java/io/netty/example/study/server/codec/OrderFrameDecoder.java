package io.netty.example.study.server.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * TCP层解码协议：定义数据包大小等等
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {
    public OrderFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }
}