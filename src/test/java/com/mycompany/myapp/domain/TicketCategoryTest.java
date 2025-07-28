package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TicketCategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketCategory.class);
        TicketCategory ticketCategory1 = getTicketCategorySample1();
        TicketCategory ticketCategory2 = new TicketCategory();
        assertThat(ticketCategory1).isNotEqualTo(ticketCategory2);

        ticketCategory2.setId(ticketCategory1.getId());
        assertThat(ticketCategory1).isEqualTo(ticketCategory2);

        ticketCategory2 = getTicketCategorySample2();
        assertThat(ticketCategory1).isNotEqualTo(ticketCategory2);
    }
}
