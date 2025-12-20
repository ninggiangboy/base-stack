package dev.ngb.base_stack.infrastructure.kafka.cdc.producer;

import dev.ngb.base_stack.application.event.Event;
import dev.ngb.base_stack.application.event.annonation.Topic;
import dev.ngb.base_stack.application.shared.port.EventPublisher;
import dev.ngb.base_stack.application.shared.port.TenantContext;
import dev.ngb.base_stack.infrastructure.kafka.cdc.payload.CdcEventPayload;
import dev.ngb.base_stack.infrastructure.persistence.jdbc.entity.event.EventPublicationEntity;
import dev.ngb.base_stack.infrastructure.persistence.jdbc.repository.event.EventPublicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class CdcKafkaEventPublisher implements EventPublisher {

    private final EventPublicationRepository eventPublicationRepository;
    private final ObjectMapper objectMapper;
    private final TenantContext tenantContext;
    private final KafkaTemplate<@NonNull String, @NonNull Object> kafkaTemplate;

    @Override
    public void publish(Event event) {
        String payload = objectMapper.writeValueAsString(event);
        log.info("Saving outbox event: {}", payload);
        EventPublicationEntity eventPublication = EventPublicationEntity.builder()
                .typeClazz(event.getClass().getName())
                .tenantId(tenantContext.getTenantId())
                .payload(payload)
                .occurredAt(event.occurredAt())
                .build();
        eventPublicationRepository.save(eventPublication);
    }

    @KafkaListener(topics = "cdc.public.event_publications")
    public void handleOutboxEvent(CdcEventPayload<EventPublicationEntity> cdcPayload) throws ClassNotFoundException {
        log.info("Received CDC event: {}", cdcPayload);
        if (cdcPayload.isCreate() && cdcPayload.payload().after() != null) {
            EventPublicationEntity event = cdcPayload.payload().after();
            Class<?> eventClass = Class.forName(event.typeClazz());
            Topic annotation = eventClass.getAnnotation(Topic.class);
            if (annotation == null) {
                throw new IllegalArgumentException("No Topic information found on " + event.typeClazz());
            }
            String topic = annotation.value();
            log.info("Publishing event topic: {}, payload: {}", topic, event.payload());
            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, event.id().toString(), event);
            record.headers().add(new RecordHeader("__TypeId__", eventClass.getName().getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record);
        }
    }
}
