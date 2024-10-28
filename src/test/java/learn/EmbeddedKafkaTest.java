package learn;

import static learn.EmbeddedKafkaTest.TOPIC_NAME;
import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.kafka.test.assertj.KafkaConditions.key;
import static org.springframework.kafka.test.assertj.KafkaConditions.keyValue;
import static org.springframework.kafka.test.assertj.KafkaConditions.partition;
import static org.springframework.kafka.test.assertj.KafkaConditions.value;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.assertj.KafkaConditions;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {TOPIC_NAME})
class EmbeddedKafkaTest {

    static final String TOPIC_NAME = "topic-1";

    @Autowired
    EmbeddedKafkaBroker kafka;

    @Value("${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}")
    String brokerAddressesAsString;

    @Test
    void consumeAll() {
        System.out.println(kafka.getBrokersAsString()); // 127.0.0.1:61108
        assertThat(kafka.getBrokersAsString()).isEqualTo(brokerAddressesAsString);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", kafka);
        //consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 2.5 버전부터 Default

        ConsumerFactory<Integer, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);

        Consumer<Integer, String> consumer = consumerFactory.createConsumer();

        kafka.consumeFromAllEmbeddedTopics(consumer);
    }

    @Test
    void produceAndConsume() throws InterruptedException {
        // ~ Consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", kafka);
        //consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 2.5 버전부터 Default

        ConsumerFactory<Integer, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);

        // ~ Container
        ContainerProperties containerProperties = new ContainerProperties(TOPIC_NAME);

        KafkaMessageListenerContainer<Integer, String> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        BlockingQueue<ConsumerRecord<Integer, String>> recordQ = new LinkedBlockingQueue<>();

        container.setupMessageListener(new MessageListener<Integer, String>() {

            @Override
            public void onMessage(ConsumerRecord<Integer, String> record) {
                System.out.println("Publish > onMessage > " + record);
                recordQ.add(record);
            }
        });

        container.setBeanName("containerBean");

        container.start();

        ContainerTestUtils.waitForAssignment(container, 1);

        // ~ Producer
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(kafka);

        ProducerFactory<Integer, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);

        KafkaTemplate<Integer, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        kafkaTemplate.setDefaultTopic(TOPIC_NAME);

        kafkaTemplate.sendDefault("foo");
        assertThat(recordQ.poll(10, TimeUnit.SECONDS)).has(KafkaConditions.value("foo"));

        kafkaTemplate.sendDefault(0, 2, "bar");
        ConsumerRecord<Integer, String> received = recordQ.poll(10, TimeUnit.SECONDS);
        assertThat(received).has(key(2));
        assertThat(received).has(partition(0));
        assertThat(received).has(value("bar"));

        kafkaTemplate.send(TOPIC_NAME, 0, 2, "baz"); // 데이터 갱신
        received = recordQ.poll(10, TimeUnit.SECONDS);
        //assertThat(received).has(key(2));
        //assertThat(received).has(partition(0));
        //assertThat(received).has(value("baz"));
        assertThat(received).has(allOf(keyValue(2, "baz"), partition(0)));
    }
}
