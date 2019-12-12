package io.netty.example.study.client.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 客户端接受服务端返回结果：解决TCP层沾包半包问题
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {
    public OrderFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }
}
