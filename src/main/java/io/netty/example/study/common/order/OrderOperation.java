package io.netty.example.study.common.order;


import com.google.common.util.concurrent.Uninterruptibles;
import io.netty.example.study.common.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class OrderOperation extends Operation {

    private int tableId;
    private String dish;

    public OrderOperation(int tableId, String dish) {
        this.tableId = tableId;
        this.dish = dish;
    }

    @Override
    public OrderOperationResult execute() {
        log.info("order's executing startup with orderRequest: " + toString());
        //模拟业务执行3秒
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        log.info("order's executing complete");
        OrderOperationResult orderResponse = new OrderOperationResult(tableId, dish, true);
        return orderResponse;
    }
}
