package learn.component;

import learn.model.User;
import learn.model.User2;
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

    @KafkaListener(topics = "topic-2", groupId = "test-group")
    public void subscribeTopic2(User user) {

        System.out.println("받은 메시지: " + user);
        received = true;
    }

    @KafkaListener(topics = "topic-3", groupId = "test-group", errorHandler = "customKafkaListenerErrorHandler")
    public void subscribeTopic3(User2 user) {

        System.out.println("받은 메시지: " + user);
        received = true;
    }
}
