package io.netty.example.study.server.codec.handler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;

@ChannelHandler.Sharable
public class IpFilterHandler extends RuleBasedIpFilter {
    private static IpFilterRule[] list = {
            new IpSubnetFilterRule("127.0.0.1", 8, IpFilterRuleType.REJECT)
    };

    public IpFilterHandler() {
        super(list);
    }
}
