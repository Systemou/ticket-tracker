package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TicketCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TicketCategory getTicketCategorySample1() {
        return new TicketCategory().id(1L).name("name1");
    }

    public static TicketCategory getTicketCategorySample2() {
        return new TicketCategory().id(2L).name("name2");
    }

    public static TicketCategory getTicketCategoryRandomSampleGenerator() {
        return new TicketCategory().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
