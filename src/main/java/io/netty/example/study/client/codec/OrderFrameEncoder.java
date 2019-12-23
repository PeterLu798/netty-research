package io.netty.example.study.client.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * 客户端解决TCP沾包半包问题
 */
public class OrderFrameEncoder extends LengthFieldPrepender {
    public OrderFrameEncoder() {
        //前置固定长度（单位字节）存放“长度”字段，这里是2个字节。并且这个长度只表示原始数据的长度。
        /**
         * 这个值只允许1，2，3，4，8 也就是对应的1字节，2字节(short)，3字节(medium？？？)，4字节(int)以及8字节(long)：
         * switch (lengthFieldLength) {
         *         case 1:
         *             if (length >= 256) {
         *                 throw new IllegalArgumentException(
         *                         "length does not fit into a byte: " + length);
         *             }
         *             out.add(ctx.alloc().buffer(1).order(byteOrder).writeByte((byte) length));
         *             break;
         *         case 2:
         *             if (length >= 65536) {
         *                 throw new IllegalArgumentException(
         *                         "length does not fit into a short integer: " + length);
         *             }
         *             out.add(ctx.alloc().buffer(2).order(byteOrder).writeShort((short) length));
         *             break;
         *         case 3:
         *             if (length >= 16777216) {
         *                 throw new IllegalArgumentException(
         *                         "length does not fit into a medium integer: " + length);
         *             }
         *             out.add(ctx.alloc().buffer(3).order(byteOrder).writeMedium(length));
         *             break;
         *         case 4:
         *             out.add(ctx.alloc().buffer(4).order(byteOrder).writeInt(length));
         *             break;
         *         case 8:
         *             out.add(ctx.alloc().buffer(8).order(byteOrder).writeLong(length));
         *             break;
         *         default:
         *             throw new Error("should not reach here");
         *         }
         */
        super(2);
    }
}
