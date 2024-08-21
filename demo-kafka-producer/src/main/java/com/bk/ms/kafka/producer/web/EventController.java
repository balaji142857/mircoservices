package com.bk.ms.kafka.producer.web;

import com.bk.ms.kafka.models.Event;
import com.bk.ms.kafka.producer.service.ProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/events")
public class EventController {

    private final ProcessorService service;

    @PostMapping
    public String handleEvent(@RequestBody Event event) {
        return service.process(event);
    }

}
