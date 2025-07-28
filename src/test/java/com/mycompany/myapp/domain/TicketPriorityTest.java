package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TicketPriorityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketPriorityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketPriority.class);
        TicketPriority ticketPriority1 = getTicketPrioritySample1();
        TicketPriority ticketPriority2 = new TicketPriority();
        assertThat(ticketPriority1).isNotEqualTo(ticketPriority2);

        ticketPriority2.setId(ticketPriority1.getId());
        assertThat(ticketPriority1).isEqualTo(ticketPriority2);

        ticketPriority2 = getTicketPrioritySample2();
        assertThat(ticketPriority1).isNotEqualTo(ticketPriority2);
    }
}
