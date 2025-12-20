package dev.ngb.base_stack.infrastructure.kafka.config;

import org.apache.kafka.common.errors.RecordDeserializationException;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {

        FixedBackOff backOff = new FixedBackOff(0L, 0);

        DefaultErrorHandler handler =
                new DefaultErrorHandler(backOff);

        // BẮT BUỘC add SerializationException
        handler.addNotRetryableExceptions(
                SerializationException.class,
                RecordDeserializationException.class
        );

        return handler;
    }


}
