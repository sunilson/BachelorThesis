package com.sunilson.bachelorthesis.domain.calendar.interactors;

import com.sunilson.bachelorthesis.data.model.EventEntity;
import com.sunilson.bachelorthesis.domain.shared.AbstractUseCase;
import com.sunilson.bachelorthesis.domain.calendar.mappers.EventEntityToDomainEventMapper;
import com.sunilson.bachelorthesis.domain.calendar.model.DomainEvent;
import com.sunilson.bachelorthesis.data.repository.BodyModels.EventForPostBody;
import com.sunilson.bachelorthesis.domain.repository.EventRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author Linus Weiss
 *
 * Use case for adding a new event to the web service
 */
public class AddEventUseCase extends AbstractUseCase<DomainEvent, AddEventUseCase.Params> {

    private EventRepository eventRepository;
    private EventEntityToDomainEventMapper entityToDomainEventMapper;

    @Inject
    public AddEventUseCase(EventRepository eventRepository, EventEntityToDomainEventMapper entityToDomainEventMapper) {
        this.eventRepository = eventRepository;
        this.entityToDomainEventMapper = entityToDomainEventMapper;
    }

    @Override
    protected Observable<DomainEvent> buildUseCaseObservable(Params params) {

        DomainEvent domainEvent = params.domainEvent;

        EventForPostBody eventForPostBody = new EventForPostBody(
                domainEvent.getEventType(),
                domainEvent.getDescription(),
                domainEvent.getLocation(),
                domainEvent.getSummary(),
                null,
                domainEvent.getFrom().getMillis(),
                domainEvent.getTo().getMillis()
        );

        return this.eventRepository.addEvent(eventForPostBody)
                .map(eventEntity -> entityToDomainEventMapper.mapToDomainEvent(eventEntity));
    }

    public static final class Params {

        DomainEvent domainEvent;

        private Params(DomainEvent domainEvent) {
            this.domainEvent = domainEvent;
        }

        public static Params forDaySpan(DomainEvent domainEvent) {
            return new Params(domainEvent);
        }
    }
}
