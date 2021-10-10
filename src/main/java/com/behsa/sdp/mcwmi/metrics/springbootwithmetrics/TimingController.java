package com.behsa.sdp.mcwmi.metrics.springbootwithmetrics;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class TimingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    private final MeterRegistry registry;

    /**
     * We inject the MeterRegistry into this class
     */
    public TimingController(MeterRegistry registry) {
        this.registry = registry;
    }

    /**
     * The @Timed annotation adds timing support, so we can see how long
     * it takes to execute in Prometheus
     * percentiles
     */
    @GetMapping("/Timing")
    @Timed(value = "timing.time", description = "Time taken to return greeting",
            percentiles = {0.5, 0.90})
    public Timing timing(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Timing(counter.incrementAndGet(), String.format(template, name));
    }

}
