package org.rvmiranda.kafkaservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rvmiranda.kafkaservice.dto.ProductDto;
import org.rvmiranda.kafkaservice.domain.model.OrderEvent;
import org.rvmiranda.kafkaservice.domain.model.PaymentEvent;
import org.rvmiranda.kafkaservice.domain.model.ProductEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // ── Topic Definitions ─────────────────────────────────────────────────────

    @Bean
    public NewTopic paymentReceivedEventsTopic() {
        return TopicBuilder.name("payment-events").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic fullReceivedPaymentsEventsTopic() {
        return TopicBuilder.name("full_recieved_payments_events").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic orderStatusChangedEventsTopic() {
        return TopicBuilder.name("order-events").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryUpdateEventsTopic() {
        return TopicBuilder.name("inventory_update_events").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic productEventsTopic() {
        return TopicBuilder.name("product-events").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic productEventsRetryTopic() {
        return TopicBuilder.name("product-events-retry").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic orderEventsRetryTopic() {
        return TopicBuilder.name("order-events-retry").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic paymentEventsRetryTopic() {
        return TopicBuilder.name("payment-events-retry").partitions(1).replicas(1).build();
    }

    // ── Producer ──────────────────────────────────────────────────────────────

    @Bean
    public ProducerFactory<String, ProductDto> producerFactory() {
        ObjectMapper mapper = new ObjectMapper();
        JsonSerializer<ProductDto> serializer = new JsonSerializer<>(mapper);
        serializer.setAddTypeInfo(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, ProductDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ── Consumer ──────────────────────────────────────────────────────────────

    @Bean
    public ConsumerFactory<String, ProductDto> consumerFactory() {
        ObjectMapper mapper = new ObjectMapper();
        JsonDeserializer<ProductDto> jsonDeserializer = new JsonDeserializer<>(ProductDto.class, mapper);
        jsonDeserializer.setUseTypeHeaders(false);
        
        org.springframework.kafka.support.serializer.ErrorHandlingDeserializer<ProductDto> errorHandlingDeserializer = 
            new org.springframework.kafka.support.serializer.ErrorHandlingDeserializer<>(jsonDeserializer);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    // ── Consumer para Retry Jobs (String → String, sin deserialización JSON) ──────

    @Bean
    public ConsumerFactory<String, String> retryConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> retryKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(retryConsumerFactory());
        return factory;
    }
    // ── Consumer para ProductEvent (product-events topic) ────────────────────

    @Bean
    public ConsumerFactory<String, ProductEvent> productEventConsumerFactory() {
        ObjectMapper mapper = new ObjectMapper();
        JsonDeserializer<ProductEvent> jsonDeserializer = new JsonDeserializer<>(ProductEvent.class, mapper);
        jsonDeserializer.setUseTypeHeaders(false);

        org.springframework.kafka.support.serializer.ErrorHandlingDeserializer<ProductEvent> errorHandlingDeserializer =
            new org.springframework.kafka.support.serializer.ErrorHandlingDeserializer<>(jsonDeserializer);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductEvent> productEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productEventConsumerFactory());
        return factory;
    }

    // ── Consumer para OrderEvent (order-events topic) ─────────────────────────

    @Bean
    public ConsumerFactory<String, OrderEvent> orderEventConsumerFactory() {
        ObjectMapper mapper = new ObjectMapper();
        JsonDeserializer<OrderEvent> jsonDeserializer = new JsonDeserializer<>(OrderEvent.class, mapper);
        jsonDeserializer.setUseTypeHeaders(false);

        org.springframework.kafka.support.serializer.ErrorHandlingDeserializer<OrderEvent> errorHandlingDeserializer =
            new org.springframework.kafka.support.serializer.ErrorHandlingDeserializer<>(jsonDeserializer);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> orderEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderEventConsumerFactory());
        return factory;
    }

    // ── Consumer para PaymentEvent (payment-events topic) ─────────────────────

    @Bean
    public ConsumerFactory<String, PaymentEvent> paymentEventConsumerFactory() {
        ObjectMapper mapper = new ObjectMapper();
        JsonDeserializer<PaymentEvent> jsonDeserializer = new JsonDeserializer<>(PaymentEvent.class, mapper);
        jsonDeserializer.setUseTypeHeaders(false);

        org.springframework.kafka.support.serializer.ErrorHandlingDeserializer<PaymentEvent> errorHandlingDeserializer =
            new org.springframework.kafka.support.serializer.ErrorHandlingDeserializer<>(jsonDeserializer);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> paymentEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentEventConsumerFactory());
        return factory;
    }
}
