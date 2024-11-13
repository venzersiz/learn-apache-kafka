package learn.config;

import learn.config.KafkaConfigForAZone.KafkaPropertiesForAZone;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableConfigurationProperties(KafkaPropertiesForAZone.class)
@RequiredArgsConstructor
public class KafkaConfigForAZone {

    private final KafkaPropertiesForAZone props;

    @Bean
    public ProducerFactory<String, Object> kafkaProducerFactoryForAZone() {
        return new DefaultKafkaProducerFactory<>(props.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplateForAZone(ProducerFactory<String, Object> kafkaProducerFactoryForAZone) {

        return new KafkaTemplate<>(kafkaProducerFactoryForAZone);
    }

    @Bean
    public ConsumerFactory<String, Object> kafkaConsumerFactoryForAZone() {
        return new DefaultKafkaConsumerFactory<>(props.buildConsumerProperties());
    }

    @Bean
    public KafkaListenerContainerFactory containerFactoryForAZone(ConsumerFactory<String, Object> kafkaConsumerFactoryForAZone) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactoryForAZone);
        return factory;
    }

    @Bean
    public KafkaPropertiesForAZone kafkaPropertiesForAZone() {
        return new KafkaPropertiesForAZone();
    }

    @ConfigurationProperties(prefix = "spring.kafka.a-zone")
    @Getter
    @Setter
    static class KafkaPropertiesForAZone extends KafkaProperties {

    }
}
