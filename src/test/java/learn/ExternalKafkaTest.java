package learn;

import java.util.concurrent.TimeUnit;
import learn.component.KafkaPublisherForAZone;
import learn.component.KafkaSubscriber;
import learn.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExternalKafkaTest {

    @Autowired
    KafkaPublisherForAZone publisherForAZone;

    //@Autowired
    //KafkaPublisherForBZone publisherForBZone;

    @Autowired
    KafkaSubscriber subscriber;

    @Test
    void publishAndSubscribe() throws InterruptedException {

        User user = User.builder()
                        .name("산신령")
                        .age(100)
                        .build();

        publisherForAZone.publish("topic-3", user);
        //publisherForBZone.publish("topic-3", user);

        while (true) {
            if (subscriber.getReceivedCount().get() == 1) {
                break;
            }

            TimeUnit.SECONDS.sleep(1);
        }
    }
}
