package com.romiiis.port;

/**
 * Port for publishing domain events.
 *
 */
public interface IDomainEventPublisher {

    /**
     * Publishes the given domain event to all interested subscribers.
     */
    void publish(Object event);
}