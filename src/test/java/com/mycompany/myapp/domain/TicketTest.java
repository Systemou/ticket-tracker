package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TicketCategoryTestSamples.*;
import static com.mycompany.myapp.domain.TicketPriorityTestSamples.*;
import static com.mycompany.myapp.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ticket.class);
        Ticket ticket1 = getTicketSample1();
        Ticket ticket2 = new Ticket();
        assertThat(ticket1).isNotEqualTo(ticket2);

        ticket2.setId(ticket1.getId());
        assertThat(ticket1).isEqualTo(ticket2);

        ticket2 = getTicketSample2();
        assertThat(ticket1).isNotEqualTo(ticket2);
    }

    @Test
    void categoryTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        TicketCategory ticketCategoryBack = getTicketCategoryRandomSampleGenerator();

        ticket.setCategory(ticketCategoryBack);
        assertThat(ticket.getCategory()).isEqualTo(ticketCategoryBack);

        ticket.category(null);
        assertThat(ticket.getCategory()).isNull();
    }

    @Test
    void priorityTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        TicketPriority ticketPriorityBack = getTicketPriorityRandomSampleGenerator();

        ticket.setPriority(ticketPriorityBack);
        assertThat(ticket.getPriority()).isEqualTo(ticketPriorityBack);

        ticket.priority(null);
        assertThat(ticket.getPriority()).isNull();
    }
}
