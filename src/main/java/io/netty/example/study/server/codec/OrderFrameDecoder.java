package io.netty.example.study.server.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * TCP层解码协议：定义数据包大小等等
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {
    public OrderFrameDecoder() {
        /**
         * maxFrameLength: 帧的最大长度，这里是Integer.MAX_VALUE
         * lengthFieldOffset: 标识长度字段的开始位置，这里是0表示从头开始
         * lengthFieldLength: 长度字段所占用的长度，这里为2字节
         * lengthAdjustment: 有时候需要这样的场景：源数据的基础上增加一定长度的数据表示特殊业务，但是这个特殊业务的长度也会计算在
         *                   lengthFieldOffset中，这个长度可以用lengthAdjustment表示，也就是说 lengthAdjustment + 源数据长度 = lengthFieldLength
         * initialBytesToStrip: 去除的长度，这里是2表示把标识长度的字段部分去除，只解码源数据部分
         */
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }
}
