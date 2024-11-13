package learn.config;

import learn.config.KafkaConfigForBZone.KafkaPropertiesForBZone;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

//@Configuration
@EnableConfigurationProperties(KafkaPropertiesForBZone.class)
@RequiredArgsConstructor
public class KafkaConfigForBZone {

    private final KafkaPropertiesForBZone props;

    @Bean
    public ProducerFactory<String, Object> kafkaProducerFactoryForBZone() {
        return new DefaultKafkaProducerFactory<>(props.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplateForBZone(ProducerFactory<String, Object> kafkaProducerFactoryForBZone) {

        return new KafkaTemplate<>(kafkaProducerFactoryForBZone);
    }

    @Bean
    public ConsumerFactory<String, Object> kafkaConsumerFactoryForBZone() {
        return new DefaultKafkaConsumerFactory<>(props.buildConsumerProperties());
    }

    @Bean
    public KafkaListenerContainerFactory containerFactoryForBZone(ConsumerFactory<String, Object> kafkaConsumerFactoryForBZone) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactoryForBZone);
        return factory;
    }

    @Bean
    public KafkaPropertiesForBZone kafkaPropertiesForBZone() {
        return new KafkaPropertiesForBZone();
    }

    @ConfigurationProperties(prefix = "spring.kafka.b-zone")
    @Getter
    @Setter
    static class KafkaPropertiesForBZone extends KafkaProperties {

    }
}
