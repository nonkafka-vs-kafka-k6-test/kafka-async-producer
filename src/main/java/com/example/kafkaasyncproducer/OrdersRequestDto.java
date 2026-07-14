package com.example.kafkaasyncproducer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersRequestDto {
    private Long userId;
    private Integer amount;
}
