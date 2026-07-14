package com.example.kafkaasyncproducer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OrderNotificationMessage {

    private final String orderId;
    private final Long userId;
    private final Integer amount;

    @JsonCreator
    public OrderNotificationMessage(
            @JsonProperty("orderId") String orderId,
            @JsonProperty("userId") Long userId,
            @JsonProperty("amount") Integer amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }

    public static OrderNotificationMessage from(Orders order) {
        return new OrderNotificationMessage(order.getId(), order.getUserId(), order.getAmount());
    }
}
