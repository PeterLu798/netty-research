package io.netty.example.study.server.codec.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.study.common.Operation;
import io.netty.example.study.common.RequestMessage;
import io.netty.example.study.common.auth.AuthOperation;
import io.netty.example.study.common.auth.AuthOperationResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<RequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage requestMessage) throws Exception {
        try {
            Operation operation = requestMessage.getMessageBody();
            if (operation instanceof AuthOperation) {
                AuthOperation authOperation = AuthOperation.class.cast(operation);
                AuthOperationResult result = authOperation.execute();
                if (result.isPassAuth()) {
                    log.info("auth pass");

                } else {
                    log.error("fail to auth");
                    ctx.close();
                }
            } else {
                log.error("expect first message is auth");
                ctx.close();
            }
        } catch (Exception e) {
            log.error("auth exception {}", e);
            ctx.close();
        } finally {
            ctx.pipeline().remove(this);
        }
    }
}
