package com.example.kafkaasyncproducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotificationProducer {

    private static final String TOPIC_NAME = "order.notification";

    private final OrdersRepository ordersRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // db에 주문 저장 후, 사장님에게 알림 발송은 Kafka에 이벤트만 발행한 뒤 즉시 응답
    public Orders createOrderAsync(OrdersRequestDto request) {
        Orders order = Orders.builder()
                .id(UUID.randomUUID().toString())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .createdAt(LocalDateTime.now())
                .status("CREATED")
                .notificationSent(false)
                .build();

        ordersRepository.save(order);

        // 알림 발송은 이벤트만 던지고 끝 (실제 발송은 컨슈머가 비동기로 처리)
        OrderNotificationMessage message = OrderNotificationMessage.from(order);
        kafkaTemplate.send(TOPIC_NAME, order.getId(), toJson(message));

        return order;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }
}
