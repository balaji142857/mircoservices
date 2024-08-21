package com.bk.ms.kafka.producer.service;

import com.bk.ms.kafka.models.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "app.mock.produceDummyEvents", havingValue = "yes")
@RequiredArgsConstructor
@Slf4j
public class DummyEventProducer {
    private static final EventType[] EVENTS = EventType.values();


    private final ProcessorService service;

    private final Random random  = new Random();

    @Value("${app.mock.userNames}")
    private List<String> mockUserNames;

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void fireDummyEvents() {
        if (CollectionUtils.isEmpty(mockUserNames)) {
            log.warn("need few user names to fire mock events. Configure app.mock.userNames and restart application");
            return;
        }
        var event = createEvent();
        service.process(event);
        log.info("Event produced: {}", event);
    }

    public Event createEvent() {
        int eventIndex = random.nextInt(EVENTS.length);
        String  userName = mockUserNames.get(random.nextInt(mockUserNames.size()));
        return new Event(EVENTS[eventIndex], userName);
    }
}
