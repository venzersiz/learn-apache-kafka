package learn.component;

import javax.annotation.Resource;
import org.springframework.kafka.core.KafkaTemplate;

//@Component
public class KafkaPublisherForBZone {

    @Resource(name = "kafkaTemplateForBZone")
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, Object message) {

        kafkaTemplate.send(topic, message);
    }
}
