package learn;

import java.util.concurrent.TimeUnit;
import learn.component.KafkaPublisher;
import learn.component.KafkaSubscriber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExternalKafkaTest {

    @Autowired
    KafkaPublisher publisher;

    @Autowired
    KafkaSubscriber subscriber;

    @Test
    void publishAndSubscribe() throws InterruptedException {
        publisher.publish("topic-1", "message-1");

        while (true) {
            if (subscriber.isReceived()) {
                break;
            }

            TimeUnit.SECONDS.sleep(1);
        }
    }
}
