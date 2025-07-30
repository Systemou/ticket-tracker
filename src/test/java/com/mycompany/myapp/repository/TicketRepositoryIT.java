package com.mycompany.myapp.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

/**
 * Integration tests for the {@link TicketRepository}.
 */
@IntegrationTest
@Transactional
class TicketRepositoryIT {

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
        ticket.setCreationDate(Instant.now());
    }

    @Test
    void shouldSaveAndFindTicket() {
        // When
        Ticket savedTicket = ticketRepository.save(ticket);
        Optional<Ticket> foundTicket = ticketRepository.findById(savedTicket.getId());

        // Then
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getTitle()).isEqualTo("Test Ticket Title");
        assertThat(foundTicket.get().getCategory()).isNotNull();
        assertThat(foundTicket.get().getPriority()).isNotNull();
        assertThat(foundTicket.get().getUser()).isNotNull();
    }

    @Test
    void shouldFindAllTickets() {
        // Given
        ticketRepository.save(ticket);

        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Second Test Ticket");
        ticket2.setDescription("This is another test ticket description that meets the minimum length requirement");
        ticket2.setCategory(category);
        ticket2.setPriority(priority);
        ticket2.setUser(user);
        ticket2.setStatus(TicketStatus.IN_PROGRESS);
        ticket2.setCreationDate(Instant.now());
        ticketRepository.save(ticket2);

        // When
        List<Ticket> tickets = ticketRepository.findAll();

        // Then
        assertThat(tickets).hasSizeGreaterThanOrEqualTo(2);
        assertThat(tickets).anyMatch(t -> t.getTitle().equals("Test Ticket Title"));
        assertThat(tickets).anyMatch(t -> t.getTitle().equals("Second Test Ticket"));
    }

    @Test
    void shouldFindAllTicketsWithPagination() {
        // Given
        ticketRepository.save(ticket);

        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Second Test Ticket");
        ticket2.setDescription("This is another test ticket description that meets the minimum length requirement");
        ticket2.setCategory(category);
        ticket2.setPriority(priority);
        ticket2.setUser(user);
        ticket2.setStatus(TicketStatus.IN_PROGRESS);
        ticket2.setCreationDate(Instant.now());
        ticketRepository.save(ticket2);

        // When
        Page<Ticket> tickets = ticketRepository.findAll(PageRequest.of(0, 10));

        // Then
        assertThat(tickets.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(tickets.getTotalElements()).isGreaterThanOrEqualTo(2L);
    }

    @Test
    void shouldFindOneWithEagerRelationships() {
        // Given
        Ticket savedTicket = ticketRepository.save(ticket);

        // When
        Optional<Ticket> foundTicket = ticketRepository.findOneWithEagerRelationships(savedTicket.getId());

        // Then
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getCategory()).isNotNull();
        assertThat(foundTicket.get().getPriority()).isNotNull();
        assertThat(foundTicket.get().getUser()).isNotNull();
        assertThat(foundTicket.get().getCategory().getName()).isEqualTo("Test Category");
        assertThat(foundTicket.get().getPriority().getName()).isEqualTo("High");
    }

    @Test
    void shouldReturnEmptyWhenFindingNonExistentTicketWithEagerRelationships() {
        // When
        Optional<Ticket> foundTicket = ticketRepository.findOneWithEagerRelationships(999L);

        // Then
        assertThat(foundTicket).isEmpty();
    }

    @Test
    void shouldFindAllWithEagerRelationships() {
        // Given
        ticketRepository.save(ticket);

        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Second Test Ticket");
        ticket2.setDescription("This is another test ticket description that meets the minimum length requirement");
        ticket2.setCategory(category);
        ticket2.setPriority(priority);
        ticket2.setUser(user);
        ticket2.setStatus(TicketStatus.IN_PROGRESS);
        ticket2.setCreationDate(Instant.now());
        ticketRepository.save(ticket2);

        // When
        List<Ticket> tickets = ticketRepository.findAllWithEagerRelationships();

        // Then
        assertThat(tickets).hasSizeGreaterThanOrEqualTo(2);
        assertThat(tickets).allMatch(t -> t.getCategory() != null);
        assertThat(tickets).allMatch(t -> t.getPriority() != null);
        assertThat(tickets).allMatch(t -> t.getUser() != null);
    }

    @Test
    void shouldFindAllWithEagerRelationshipsAndPagination() {
        // Given
        ticketRepository.save(ticket);

        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Second Test Ticket");
        ticket2.setDescription("This is another test ticket description that meets the minimum length requirement");
        ticket2.setCategory(category);
        ticket2.setPriority(priority);
        ticket2.setUser(user);
        ticket2.setStatus(TicketStatus.IN_PROGRESS);
        ticket2.setCreationDate(Instant.now());
        ticketRepository.save(ticket2);

        // When
        Page<Ticket> tickets = ticketRepository.findAllWithEagerRelationships(PageRequest.of(0, 10));

        // Then
        assertThat(tickets.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(tickets.getContent()).allMatch(t -> t.getCategory() != null);
        assertThat(tickets.getContent()).allMatch(t -> t.getPriority() != null);
        assertThat(tickets.getContent()).allMatch(t -> t.getUser() != null);
    }

    @Test
    void shouldUpdateTicket() {
        // Given
        Ticket savedTicket = ticketRepository.save(ticket);
        savedTicket.setTitle("Updated Title");
        savedTicket.setStatus(TicketStatus.CLOSED);

        // When
        Ticket updatedTicket = ticketRepository.save(savedTicket);

        // Then
        assertThat(updatedTicket.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.CLOSED);
    }

    @Test
    void shouldDeleteTicket() {
        // Given
        Ticket savedTicket = ticketRepository.save(ticket);
        long countBefore = ticketRepository.count();

        // When
        ticketRepository.deleteById(savedTicket.getId());

        // Then
        assertThat(ticketRepository.count()).isEqualTo(countBefore - 1);
        assertThat(ticketRepository.findById(savedTicket.getId())).isEmpty();
    }

    @Test
    void shouldFindOneWithToOneRelationships() {
        // Given
        Ticket savedTicket = ticketRepository.save(ticket);

        // When
        Optional<Ticket> foundTicket = ticketRepository.findOneWithToOneRelationships(savedTicket.getId());

        // Then
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getCategory()).isNotNull();
        assertThat(foundTicket.get().getPriority()).isNotNull();
        assertThat(foundTicket.get().getUser()).isNotNull();
    }

    @Test
    void shouldFindAllWithToOneRelationships() {
        // Given
        ticketRepository.save(ticket);

        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Second Test Ticket");
        ticket2.setDescription("This is another test ticket description that meets the minimum length requirement");
        ticket2.setCategory(category);
        ticket2.setPriority(priority);
        ticket2.setUser(user);
        ticket2.setStatus(TicketStatus.IN_PROGRESS);
        ticket2.setCreationDate(Instant.now());
        ticketRepository.save(ticket2);

        // When
        List<Ticket> tickets = ticketRepository.findAllWithToOneRelationships();

        // Then
        assertThat(tickets).hasSizeGreaterThanOrEqualTo(2);
        assertThat(tickets).allMatch(t -> t.getCategory() != null);
        assertThat(tickets).allMatch(t -> t.getPriority() != null);
        assertThat(tickets).allMatch(t -> t.getUser() != null);
    }

    @Test
    void shouldFindAllWithToOneRelationshipsAndPagination() {
        // Given
        ticketRepository.save(ticket);

        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Second Test Ticket");
        ticket2.setDescription("This is another test ticket description that meets the minimum length requirement");
        ticket2.setCategory(category);
        ticket2.setPriority(priority);
        ticket2.setUser(user);
        ticket2.setStatus(TicketStatus.IN_PROGRESS);
        ticket2.setCreationDate(Instant.now());
        ticketRepository.save(ticket2);

        // When
        Page<Ticket> tickets = ticketRepository.findAllWithToOneRelationships(PageRequest.of(0, 10));

        // Then
        assertThat(tickets.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(tickets.getContent()).allMatch(t -> t.getCategory() != null);
        assertThat(tickets.getContent()).allMatch(t -> t.getPriority() != null);
        assertThat(tickets.getContent()).allMatch(t -> t.getUser() != null);
    }

    @Test
    void shouldReturnEmptyWhenFindingNonExistentTicketWithToOneRelationships() {
        // When
        Optional<Ticket> foundTicket = ticketRepository.findOneWithToOneRelationships(999L);

        // Then
        assertThat(foundTicket).isEmpty();
    }
}
