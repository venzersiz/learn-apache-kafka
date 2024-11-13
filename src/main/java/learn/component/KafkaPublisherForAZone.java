package learn.component;

import javax.annotation.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPublisherForAZone {

    @Resource(name = "kafkaTemplateForAZone")
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, Object message) {

        kafkaTemplate.send(topic, message);
    }
}
