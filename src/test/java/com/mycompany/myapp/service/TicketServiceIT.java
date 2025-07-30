package com.mycompany.myapp.service;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Ticket;
import com.mycompany.myapp.domain.TicketCategory;
import com.mycompany.myapp.domain.TicketPriority;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.TicketStatus;
import com.mycompany.myapp.repository.TicketCategoryRepository;
import com.mycompany.myapp.repository.TicketPriorityRepository;
import com.mycompany.myapp.repository.TicketRepository;
import com.mycompany.myapp.repository.UserRepository;

/**
 * Integration tests for the {@link TicketService}.
 */
@IntegrationTest
@Transactional
class TicketServiceIT {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketCategoryRepository ticketCategoryRepository;

    @Autowired
    private TicketPriorityRepository ticketPriorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Ticket ticket;
    private TicketCategory category;
    private TicketPriority priority;
    private User user;

    @BeforeEach
    void setUp() {
        // Create test data
        category = new TicketCategory();
        category.setName("Test Category");
        category = ticketCategoryRepository.save(category);

        priority = new TicketPriority();
        priority.setName("High");
        priority = ticketPriorityRepository.save(priority);

        user = new User();
        user.setLogin("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setActivated(true);
        user = userRepository.save(user);

        ticket = new Ticket();
        ticket.setTitle("Test Ticket Title");
        ticket.setDescription("This is a test ticket description that meets the minimum length requirement");
        ticket.setCategory(category);
        ticket.setPriority(priority);
        ticket.setUser(user);
        ticket.setStatus(TicketStatus.OPEN);
    }

    @Test
    void shouldSaveTicket() {
        // When
        Ticket savedTicket = ticketService.save(ticket);

        // Then
        assertThat(savedTicket.getId()).isNotNull();
        assertThat(savedTicket.getTitle()).isEqualTo("Test Ticket Title");
        assertThat(savedTicket.getStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(savedTicket.getCreationDate()).isNotNull();
        assertThat(ticketRepository.findById(savedTicket.getId())).isPresent();
    }

    @Test
    void shouldSetDefaultStatusWhenNotProvided() {
        // Given
        ticket.setStatus(null);

        // When
        Ticket savedTicket = ticketService.save(ticket);

        // Then
        assertThat(savedTicket.getStatus()).isEqualTo(TicketStatus.OPEN);
    }

    @Test
    void shouldSetCreationDateWhenNotProvided() {
        // Given
        ticket.setCreationDate(null);

        // When
        Ticket savedTicket = ticketService.save(ticket);

        // Then
        assertThat(savedTicket.getCreationDate()).isNotNull();
        assertThat(savedTicket.getCreationDate()).isAfter(Instant.now().minusSeconds(5));
    }

    @Test
    void shouldUpdateTicket() {
        // Given
        Ticket savedTicket = ticketService.save(ticket);
        savedTicket.setTitle("Updated Title");
        savedTicket.setStatus(TicketStatus.IN_PROGRESS);

        // When
        Ticket updatedTicket = ticketService.update(savedTicket);

        // Then
        assertThat(updatedTicket.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    void shouldPartiallyUpdateTicket() {
        // Given
        Ticket savedTicket = ticketService.save(ticket);
        Ticket partialUpdate = new Ticket();
        partialUpdate.setId(savedTicket.getId());
        partialUpdate.setTitle("Partially Updated Title");

        // When
        Optional<Ticket> result = ticketService.partialUpdate(partialUpdate);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Partially Updated Title");
        assertThat(result.get().getDescription()).isEqualTo(savedTicket.getDescription());
        assertThat(result.get().getStatus()).isEqualTo(savedTicket.getStatus());
    }

    @Test
    void shouldReturnEmptyWhenPartiallyUpdatingNonExistentTicket() {
        // Given
        Ticket partialUpdate = new Ticket();
        partialUpdate.setId(999L);
        partialUpdate.setTitle("Non-existent Ticket");

        // When
        Optional<Ticket> result = ticketService.partialUpdate(partialUpdate);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAllTickets() {
        // Given
        ticketService.save(ticket);

        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Second Test Ticket");
        ticket2.setDescription("This is another test ticket description that meets the minimum length requirement");
        ticket2.setCategory(category);
        ticket2.setPriority(priority);
        ticket2.setUser(user);
        ticketService.save(ticket2);

        // When
        Page<Ticket> tickets = ticketService.findAll(PageRequest.of(0, 10));

        // Then
        assertThat(tickets.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(tickets.getContent()).anyMatch(t -> t.getTitle().equals("Test Ticket Title"));
        assertThat(tickets.getContent()).anyMatch(t -> t.getTitle().equals("Second Test Ticket"));
    }

    @Test
    void shouldFindAllTicketsWithEagerRelationships() {
        // Given
        ticketService.save(ticket);

        // When
        Page<Ticket> tickets = ticketService.findAllWithEagerRelationships(PageRequest.of(0, 10));

        // Then
        assertThat(tickets.getContent()).hasSizeGreaterThanOrEqualTo(1);
        Ticket foundTicket = tickets.getContent().stream()
            .filter(t -> t.getTitle().equals("Test Ticket Title"))
            .findFirst()
            .orElse(null);
        assertThat(foundTicket).isNotNull();
        assertThat(foundTicket.getCategory()).isNotNull();
        assertThat(foundTicket.getPriority()).isNotNull();
        assertThat(foundTicket.getUser()).isNotNull();
    }

    @Test
    void shouldFindTicketById() {
        // Given
        Ticket savedTicket = ticketService.save(ticket);

        // When
        Optional<Ticket> foundTicket = ticketService.findOne(savedTicket.getId());

        // Then
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getTitle()).isEqualTo("Test Ticket Title");
        assertThat(foundTicket.get().getCategory()).isNotNull();
        assertThat(foundTicket.get().getPriority()).isNotNull();
        assertThat(foundTicket.get().getUser()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenFindingNonExistentTicket() {
        // When
        Optional<Ticket> foundTicket = ticketService.findOne(999L);

        // Then
        assertThat(foundTicket).isEmpty();
    }

    @Test
    void shouldDeleteTicket() {
        // Given
        Ticket savedTicket = ticketService.save(ticket);
        long countBefore = ticketRepository.count();

        // When
        ticketService.delete(savedTicket.getId());

        // Then
        assertThat(ticketRepository.count()).isEqualTo(countBefore - 1);
        assertThat(ticketRepository.findById(savedTicket.getId())).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenSavingInvalidTicket() {
        // Given
        ticket.setTitle(""); // Invalid title

        // When & Then
        assertThatThrownBy(() -> ticketService.save(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Title is required");
    }

    @Test
    void shouldThrowExceptionWhenSavingTicketWithShortTitle() {
        // Given
        ticket.setTitle("Hi"); // Too short

        // When & Then
        assertThatThrownBy(() -> ticketService.save(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Title must be at least 5 characters long");
    }

    @Test
    void shouldThrowExceptionWhenSavingTicketWithShortDescription() {
        // Given
        ticket.setDescription("Short"); // Too short

        // When & Then
        assertThatThrownBy(() -> ticketService.save(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Description must be at least 20 characters long");
    }

    @Test
    void shouldThrowExceptionWhenSavingTicketWithoutCategory() {
        // Given
        ticket.setCategory(null);

        // When & Then
        assertThatThrownBy(() -> ticketService.save(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Category is required");
    }

    @Test
    void shouldThrowExceptionWhenSavingTicketWithoutPriority() {
        // Given
        ticket.setPriority(null);

        // When & Then
        assertThatThrownBy(() -> ticketService.save(ticket))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Priority is required");
    }
}
