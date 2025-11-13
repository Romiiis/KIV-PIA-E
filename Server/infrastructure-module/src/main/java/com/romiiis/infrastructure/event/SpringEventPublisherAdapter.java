package com.romiiis.infrastructure.event;

import com.romiiis.port.IDomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SpringEventPublisherAdapter implements IDomainEventPublisher {

    private final ApplicationEventPublisher springEventPublisher;

    @Override
    public void publish(Object event) {
        springEventPublisher.publishEvent(event);
    }
}
