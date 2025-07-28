package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TicketPriorityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TicketPriority getTicketPrioritySample1() {
        return new TicketPriority().id(1L).name("name1");
    }

    public static TicketPriority getTicketPrioritySample2() {
        return new TicketPriority().id(2L).name("name2");
    }

    public static TicketPriority getTicketPriorityRandomSampleGenerator() {
        return new TicketPriority().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
