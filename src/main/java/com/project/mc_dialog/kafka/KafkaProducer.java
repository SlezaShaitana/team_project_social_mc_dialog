package com.project.mc_dialog.kafka;

import com.project.mc_dialog.kafka.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    @Value("${app.kafka.kafkaMessageNotificationTopic}")
    private String kafkaNotificationTopic;

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void sendNotificationMessage(NotificationEvent notificationEvent){
        kafkaTemplate.send(kafkaNotificationTopic, notificationEvent);
        log.info("Send message to notification");
    }
}
