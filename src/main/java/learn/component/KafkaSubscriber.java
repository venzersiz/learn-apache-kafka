package learn.component;

import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KafkaSubscriber {

    private boolean received;

    @KafkaListener(topics = "topic-1", groupId = "test-group")
    public void subscribeTopic1(String message) {

        System.out.println("받은 메시지: " + message);
        received = true;
    }
}
