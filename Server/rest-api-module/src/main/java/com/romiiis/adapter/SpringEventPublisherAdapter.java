package com.romiiis.adapter;

import com.romiiis.service.interfaces.IDomainEventPublisher;
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
