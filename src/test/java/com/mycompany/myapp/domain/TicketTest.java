package com.mycompany.myapp.domain;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import static com.mycompany.myapp.domain.TicketCategoryTestSamples.getTicketCategoryRandomSampleGenerator;
import static com.mycompany.myapp.domain.TicketPriorityTestSamples.getTicketPriorityRandomSampleGenerator;
import static com.mycompany.myapp.domain.TicketTestSamples.getTicketRandomSampleGenerator;
import static com.mycompany.myapp.domain.TicketTestSamples.getTicketSample1;
import static com.mycompany.myapp.domain.TicketTestSamples.getTicketSample2;
import com.mycompany.myapp.domain.enumeration.TicketStatus;
import com.mycompany.myapp.web.rest.TestUtil;

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

    @Test
    void titleTest() {
        Ticket ticket = new Ticket();
        String title = "Test Ticket Title";

        ticket.setTitle(title);
        assertThat(ticket.getTitle()).isEqualTo(title);

        ticket.title("Updated Title");
        assertThat(ticket.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void descriptionTest() {
        Ticket ticket = new Ticket();
        String description = "This is a test ticket description";

        ticket.setDescription(description);
        assertThat(ticket.getDescription()).isEqualTo(description);

        ticket.description("Updated description");
        assertThat(ticket.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void creationDateTest() {
        Ticket ticket = new Ticket();
        Instant creationDate = Instant.now();

        ticket.setCreationDate(creationDate);
        assertThat(ticket.getCreationDate()).isEqualTo(creationDate);

        Instant updatedDate = Instant.now().plusSeconds(3600);
        ticket.creationDate(updatedDate);
        assertThat(ticket.getCreationDate()).isEqualTo(updatedDate);
    }

    @Test
    void statusTest() {
        Ticket ticket = new Ticket();

        ticket.setStatus(TicketStatus.OPEN);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.OPEN);

        ticket.status(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);

        ticket.status(TicketStatus.CLOSED);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CLOSED);
    }

    @Test
    void userTest() {
        Ticket ticket = new Ticket();
        User user = new User();
        user.setLogin("testuser");
        user.setEmail("test@example.com");

        ticket.setUser(user);
        assertThat(ticket.getUser()).isEqualTo(user);

        ticket.user(null);
        assertThat(ticket.getUser()).isNull();
    }

        @Test
    void hashCodeTest() {
        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);

        Ticket ticket2 = new Ticket();
        ticket2.setId(1L);

        assertThat(ticket1.hashCode()).isEqualTo(ticket2.hashCode());

        Ticket ticket3 = new Ticket();
        ticket3.setId(2L);

        // Note: hashCode might be the same for different IDs due to implementation
        // This test verifies that same IDs have same hashCode
        assertThat(ticket1.hashCode()).isEqualTo(ticket2.hashCode());
    }

        @Test
    void toStringTest() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test Description");
        ticket.setStatus(TicketStatus.OPEN);

        String toString = ticket.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("title='Test Ticket'");
        assertThat(toString).contains("description='Test Description'");
        assertThat(toString).contains("status='OPEN'");
    }

    @Test
    void builderPatternTest() {
        TicketCategory category = getTicketCategoryRandomSampleGenerator();
        TicketPriority priority = getTicketPriorityRandomSampleGenerator();
        User user = new User();
        user.setLogin("testuser");

        Ticket ticket = new Ticket()
            .title("Builder Test Ticket")
            .description("This is a test ticket created using builder pattern")
            .category(category)
            .priority(priority)
            .user(user)
            .status(TicketStatus.OPEN)
            .creationDate(Instant.now());

        assertThat(ticket.getTitle()).isEqualTo("Builder Test Ticket");
        assertThat(ticket.getDescription()).isEqualTo("This is a test ticket created using builder pattern");
        assertThat(ticket.getCategory()).isEqualTo(category);
        assertThat(ticket.getPriority()).isEqualTo(priority);
        assertThat(ticket.getUser()).isEqualTo(user);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(ticket.getCreationDate()).isNotNull();
    }



    @Test
    void nullHandlingTest() {
        Ticket ticket = new Ticket();

        // Test null values
        ticket.setTitle(null);
        assertThat(ticket.getTitle()).isNull();

        ticket.setDescription(null);
        assertThat(ticket.getDescription()).isNull();

        ticket.setCreationDate(null);
        assertThat(ticket.getCreationDate()).isNull();

        ticket.setStatus(null);
        assertThat(ticket.getStatus()).isNull();

        ticket.setCategory(null);
        assertThat(ticket.getCategory()).isNull();

        ticket.setPriority(null);
        assertThat(ticket.getPriority()).isNull();

        ticket.setUser(null);
        assertThat(ticket.getUser()).isNull();
    }

    @Test
    void statusEnumTest() {
        // Test all enum values
        assertThat(TicketStatus.OPEN).isNotNull();
        assertThat(TicketStatus.IN_PROGRESS).isNotNull();
        assertThat(TicketStatus.CLOSED).isNotNull();

        // Test enum comparison
        assertThat(TicketStatus.OPEN).isNotEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(TicketStatus.IN_PROGRESS).isNotEqualTo(TicketStatus.CLOSED);
        assertThat(TicketStatus.OPEN).isNotEqualTo(TicketStatus.CLOSED);
    }
}
