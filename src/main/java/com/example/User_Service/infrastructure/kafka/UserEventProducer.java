package com.example.User_Service.infrastructure.kafka;


import com.example.User_Service.api.dto.event.UserEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    public void send(String topic, UserEventDto message) {
        kafkaTemplate.send(topic, message);
    }
}
