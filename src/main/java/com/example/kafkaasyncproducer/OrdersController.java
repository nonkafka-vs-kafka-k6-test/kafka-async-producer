package com.example.kafkaasyncproducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OrdersController {

    private final OrderNotificationProducer orderNotificationProducer;

    // 주문 저장만 하고 즉시 응답, 사장님에게 알림은 Kafka로 비동기 처리
    @PostMapping("/orders/async")
    public String createOrderAsync(@RequestBody OrdersRequestDto request) {
        Orders order = orderNotificationProducer.createOrderAsync(request);
        return "주문 생성 완료 - orderId: " + order.getId();
    }
}
