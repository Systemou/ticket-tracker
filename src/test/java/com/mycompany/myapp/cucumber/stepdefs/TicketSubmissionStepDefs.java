package com.mycompany.myapp.cucumber.stepdefs;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.RandomStringUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.mycompany.myapp.domain.Ticket;
import com.mycompany.myapp.domain.TicketCategory;
import com.mycompany.myapp.domain.TicketPriority;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.TicketStatus;
import com.mycompany.myapp.repository.TicketCategoryRepository;
import com.mycompany.myapp.repository.TicketPriorityRepository;
import com.mycompany.myapp.repository.TicketRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.TicketService;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TicketSubmissionStepDefs extends StepDefs {

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

    private User testUser;
    private TicketCategory selectedCategory;
    private TicketPriority selectedPriority;
    private Ticket submittedTicket;
    private List<Ticket> submittedTickets;
    private Exception lastException;
    private List<Ticket> userTickets;

    @Before
    public void setup() {
        // Setup authentication for test user
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        UserDetails principal = new org.springframework.security.core.userdetails.User(
            "testuser", "", true, true, true, true, grantedAuthorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

        @After
    public void cleanup() {
        // Clean up test tickets first (before deleting user)
        if (submittedTickets != null) {
            submittedTickets.forEach(ticket -> {
                if (ticket.getId() != null) {
                    ticketRepository.deleteById(ticket.getId());
                }
            });
        }

        // Clean up individual submitted ticket
        if (submittedTicket != null && submittedTicket.getId() != null) {
            ticketRepository.deleteById(submittedTicket.getId());
        }

        // Clean up test user (only if we created it)
        if (testUser != null && testUser.getId() != null) {
            // Delete any tickets associated with this user first
            ticketRepository.findByUserIsCurrentUser().stream()
                .filter(ticket -> ticket.getUser().getId().equals(testUser.getId()))
                .forEach(ticket -> ticketRepository.deleteById(ticket.getId()));

            // Now delete the user
            userRepository.findOneByLogin(testUser.getLogin())
                .ifPresent(userRepository::delete);
        }

        // Clean up test categories and priorities created during tests
        if (selectedCategory != null && selectedCategory.getId() != null) {
            ticketCategoryRepository.deleteById(selectedCategory.getId());
        }
        if (selectedPriority != null && selectedPriority.getId() != null) {
            ticketPriorityRepository.deleteById(selectedPriority.getId());
        }
    }

        @Given("I am logged in as a user")
    public void i_am_logged_in_as_a_user() {
        // Check if test user already exists, if not create it
        testUser = userRepository.findOneByLogin("testuser")
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setLogin("testuser");
                newUser.setPassword(RandomStringUtils.insecure().nextAlphanumeric(60));
                newUser.setActivated(true);
                newUser.setEmail("testuser@localhost");
                newUser.setFirstName("Test");
                newUser.setLastName("User");
                newUser.setLangKey("en");
                return userRepository.save(newUser);
            });
    }

    @Given("there are available ticket categories and priorities")
    public void there_are_available_ticket_categories_and_priorities() {
        // Create test categories if they don't exist
        createTestCategoryIfNotExists("TECHNICAL");
        createTestCategoryIfNotExists("BUG");
        createTestCategoryIfNotExists("FEATURE");
        createTestCategoryIfNotExists("CRITICAL");
        createTestCategoryIfNotExists("MINOR");
        createTestCategoryIfNotExists("TEST");

        // Create test priorities if they don't exist
        createTestPriorityIfNotExists("HIGH");
        createTestPriorityIfNotExists("MEDIUM");
        createTestPriorityIfNotExists("LOW");
    }

    @When("I submit a ticket with title {string} and description {string}")
    public void i_submit_a_ticket_with_title_and_description(String title, String description) {
        try {
            Ticket ticket = new Ticket();
            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setUser(testUser);

            // Set category and priority if they were selected
            if (selectedCategory != null) {
                ticket.setCategory(selectedCategory);
            }
            if (selectedPriority != null) {
                ticket.setPriority(selectedPriority);
            }

            // Call actual service - this should fail until business logic is implemented
            submittedTicket = ticketService.save(ticket);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("I select category {string} and priority {string}")
    public void i_select_category_and_priority(String categoryName, String priorityName) {
        // Find or create category
        selectedCategory = ticketCategoryRepository.findAll().stream()
            .filter(cat -> cat.getName().equalsIgnoreCase(categoryName))
            .findFirst()
            .orElseGet(() -> createTestCategory(categoryName));

        // Find or create priority
        selectedPriority = ticketPriorityRepository.findAll().stream()
            .filter(pri -> pri.getName().equalsIgnoreCase(priorityName))
            .findFirst()
            .orElseGet(() -> createTestPriority(priorityName));
    }

    @When("I do not select a category")
    public void i_do_not_select_a_category() {
        selectedCategory = null;
    }

    @When("I do not select a priority")
    public void i_do_not_select_a_priority() {
        selectedPriority = null;
    }

    @When("I submit multiple tickets concurrently")
    public void i_submit_multiple_tickets_concurrently() {
        submittedTickets = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<CompletableFuture<Ticket>> futures = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            final int index = i;
            CompletableFuture<Ticket> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Ticket ticket = new Ticket();
                    ticket.setTitle("Concurrent Ticket " + index);
                    ticket.setDescription("This is a detailed description for concurrent ticket " + index);
                    ticket.setUser(testUser);
                    ticket.setCategory(selectedCategory);
                    ticket.setPriority(selectedPriority);
                    return ticketService.save(ticket);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        futures.forEach(future -> {
            try {
                submittedTickets.add(future.get());
            } catch (Exception e) {
                // Handle exception
            }
        });

        executor.shutdown();
    }

    @Then("the ticket should be created successfully")
    public void the_ticket_should_be_created_successfully() {
        assertThat(submittedTicket).isNotNull();
        assertThat(submittedTicket.getId()).isNotNull();
    }

    @Then("the ticket should have status {string}")
    public void the_ticket_should_have_status(String expectedStatus) {
        assertThat(submittedTicket.getStatus()).isEqualTo(TicketStatus.valueOf(expectedStatus));
    }

    @Then("the ticket should have a creation date")
    public void the_ticket_should_have_a_creation_date() {
        assertThat(submittedTicket.getCreationDate()).isNotNull();
    }

    @Then("the ticket should be visible in my list of tickets")
    public void the_ticket_should_be_visible_in_my_list_of_tickets() {
        // Call actual repository to get user's tickets
        userTickets = ticketRepository.findByUserIsCurrentUser();
        assertThat(userTickets).anyMatch(ticket ->
            ticket.getId().equals(submittedTicket.getId()));
    }

    @Then("I should see an error message about title length requirement")
    public void i_should_see_an_error_message_about_title_length_requirement() {
        assertThat(lastException).isNotNull();
        // The exception should indicate validation failure
    }

    @Then("I should see an error message about description length requirement")
    public void i_should_see_an_error_message_about_description_length_requirement() {
        assertThat(lastException).isNotNull();
        // The exception should indicate validation failure
    }

    @Then("I should see an error message about required title")
    public void i_should_see_an_error_message_about_required_title() {
        assertThat(lastException).isNotNull();
        // The exception should indicate validation failure
    }

    @Then("I should see an error message about required description")
    public void i_should_see_an_error_message_about_required_description() {
        assertThat(lastException).isNotNull();
        // The exception should indicate validation failure
    }

    @Then("I should see an error message about required category")
    public void i_should_see_an_error_message_about_required_category() {
        assertThat(lastException).isNotNull();
        // The exception should indicate validation failure
    }

    @Then("I should see an error message about required priority")
    public void i_should_see_an_error_message_about_required_priority() {
        assertThat(lastException).isNotNull();
        // The exception should indicate validation failure
    }

    @Then("the ticket should be created with minimum length requirements enforced")
    public void the_ticket_should_be_created_with_minimum_length_requirements_enforced() {
        assertThat(submittedTicket.getTitle()).hasSizeGreaterThanOrEqualTo(5);
        assertThat(submittedTicket.getDescription()).hasSizeGreaterThanOrEqualTo(20);
    }

    @Then("the ticket should have automatic creation date")
    public void the_ticket_should_have_automatic_creation_date() {
        assertThat(submittedTicket.getCreationDate()).isNotNull();
        assertThat(submittedTicket.getCreationDate()).isBeforeOrEqualTo(Instant.now());
    }

    @Then("the ticket should have default status {string}")
    public void the_ticket_should_have_default_status(String expectedStatus) {
        assertThat(submittedTicket.getStatus()).isEqualTo(TicketStatus.valueOf(expectedStatus));
    }

    @Then("all tickets should be created successfully")
    public void all_tickets_should_be_created_successfully() {
        assertThat(submittedTickets).hasSize(5);
        submittedTickets.forEach(ticket -> {
            assertThat(ticket).isNotNull();
            assertThat(ticket.getId()).isNotNull();
        });
    }

    @Then("each ticket should have unique IDs")
    public void each_ticket_should_have_unique_ids() {
        List<Long> ids = submittedTickets.stream()
            .map(Ticket::getId)
            .toList();
        assertThat(ids).doesNotHaveDuplicates();
    }

    @Then("each ticket should have correct creation dates")
    public void each_ticket_should_have_correct_creation_dates() {
        submittedTickets.forEach(ticket -> {
            assertThat(ticket.getCreationDate()).isNotNull();
            assertThat(ticket.getCreationDate()).isBeforeOrEqualTo(Instant.now());
        });
    }

    private void createTestCategoryIfNotExists(String name) {
        ticketCategoryRepository.findAll().stream()
            .filter(cat -> cat.getName().equalsIgnoreCase(name))
            .findFirst()
            .ifPresentOrElse(
                cat -> selectedCategory = cat,
                () -> selectedCategory = createTestCategory(name)
            );
    }

    private void createTestPriorityIfNotExists(String name) {
        ticketPriorityRepository.findAll().stream()
            .filter(pri -> pri.getName().equalsIgnoreCase(name))
            .findFirst()
            .ifPresentOrElse(
                pri -> selectedPriority = pri,
                () -> selectedPriority = createTestPriority(name)
            );
    }

    private TicketCategory createTestCategory(String name) {
        TicketCategory category = new TicketCategory();
        category.setName(name);
        return ticketCategoryRepository.save(category);
    }

    private TicketPriority createTestPriority(String name) {
        TicketPriority priority = new TicketPriority();
        priority.setName(name);
        return ticketPriorityRepository.save(priority);
    }
}
