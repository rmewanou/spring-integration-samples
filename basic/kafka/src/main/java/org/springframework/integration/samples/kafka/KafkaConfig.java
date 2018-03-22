package org.springframework.integration.samples.kafka;

import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;

@Configuration
public class KafkaConfig {

    @Autowired
    private KafkaAppProperties properties;

//    @Autowired
//    private IntegrationFlowContext flowContext;


    @Bean
    public ProducerFactory<?, ?> kafkaProducerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @ServiceActivator(inputChannel = "toKafka")
    @Bean
    public MessageHandler handler(KafkaTemplate<String, String> kafkaTemplate) {
        KafkaProducerMessageHandler<String, String> handler = new KafkaProducerMessageHandler<>(kafkaTemplate);
        handler.setMessageKeyExpression(new LiteralExpression(this.properties.getMessageKey()));

        return handler;
    }

    @Bean
    public ConsumerFactory<?, ?> kafkaConsumerFactory(KafkaProperties properties) {
        Map<String, Object> consumerProperties = properties.buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    @Bean
    public KafkaMessageListenerContainer<String, String> container(
            ConsumerFactory<String, String> kafkaConsumerFactory) {
        return new KafkaMessageListenerContainer<>(kafkaConsumerFactory,
                new ContainerProperties(new TopicPartitionInitialOffset(this.properties.getTopicIn(), 0)));
    }

    @Bean
    public KafkaMessageDrivenChannelAdapter<String, String> adapter(KafkaMessageListenerContainer<String, String> container) {
        KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter = new KafkaMessageDrivenChannelAdapter<>(container);
        kafkaMessageDrivenChannelAdapter.setOutputChannel(fromKafka());

        return kafkaMessageDrivenChannelAdapter;
    }

    @Bean
    public PollableChannel fromKafka() {
        return new QueueChannel();
    }

    /*
     * Boot's autoconfigured KafkaAdmin will provision the topics.
     */

    @Bean
    public NewTopic topic(KafkaAppProperties kafkaAppProperties) {

        return new NewTopic(kafkaAppProperties.getTopicOut(), 10, (short) 1);
    }

//	@Bean
//    public KafkaAdmin kafkaAdmin(KafkaProperties kafkaProperties) {
//        List<String> bootstrapServers = kafkaProperties.getBootstrapServers();
//        if (kafkaProperties.getProducer() != null) {
//            bootstrapServers = kafkaProperties.getProducer().getBootstrapServers();
//        }
//        Map<String, Object> configs = Collections.singletonMap(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, StringUtils.collectionToCommaDelimitedString(bootstrapServers));
//
//        return new KafkaAdmin(configs);
//    }

}
