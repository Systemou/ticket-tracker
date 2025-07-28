package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TicketPriorityAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TicketPriority;
import com.mycompany.myapp.repository.TicketPriorityRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TicketPriorityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketPriorityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ticket-priorities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketPriorityRepository ticketPriorityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketPriorityMockMvc;

    private TicketPriority ticketPriority;

    private TicketPriority insertedTicketPriority;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketPriority createEntity() {
        return new TicketPriority().name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketPriority createUpdatedEntity() {
        return new TicketPriority().name(UPDATED_NAME);
    }

    @BeforeEach
    void initTest() {
        ticketPriority = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTicketPriority != null) {
            ticketPriorityRepository.delete(insertedTicketPriority);
            insertedTicketPriority = null;
        }
    }

    @Test
    @Transactional
    void createTicketPriority() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TicketPriority
        var returnedTicketPriority = om.readValue(
            restTicketPriorityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketPriority)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TicketPriority.class
        );

        // Validate the TicketPriority in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTicketPriorityUpdatableFieldsEquals(returnedTicketPriority, getPersistedTicketPriority(returnedTicketPriority));

        insertedTicketPriority = returnedTicketPriority;
    }

    @Test
    @Transactional
    void createTicketPriorityWithExistingId() throws Exception {
        // Create the TicketPriority with an existing ID
        ticketPriority.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketPriorityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketPriority)))
            .andExpect(status().isBadRequest());

        // Validate the TicketPriority in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketPriority.setName(null);

        // Create the TicketPriority, which fails.

        restTicketPriorityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketPriority)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTicketPriorities() throws Exception {
        // Initialize the database
        insertedTicketPriority = ticketPriorityRepository.saveAndFlush(ticketPriority);

        // Get all the ticketPriorityList
        restTicketPriorityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketPriority.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getTicketPriority() throws Exception {
        // Initialize the database
        insertedTicketPriority = ticketPriorityRepository.saveAndFlush(ticketPriority);

        // Get the ticketPriority
        restTicketPriorityMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketPriority.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketPriority.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingTicketPriority() throws Exception {
        // Get the ticketPriority
        restTicketPriorityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketPriority() throws Exception {
        // Initialize the database
        insertedTicketPriority = ticketPriorityRepository.saveAndFlush(ticketPriority);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketPriority
        TicketPriority updatedTicketPriority = ticketPriorityRepository.findById(ticketPriority.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTicketPriority are not directly saved in db
        em.detach(updatedTicketPriority);
        updatedTicketPriority.name(UPDATED_NAME);

        restTicketPriorityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTicketPriority.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTicketPriority))
            )
            .andExpect(status().isOk());

        // Validate the TicketPriority in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketPriorityToMatchAllProperties(updatedTicketPriority);
    }

    @Test
    @Transactional
    void putNonExistingTicketPriority() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketPriority.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketPriorityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketPriority.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketPriority))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketPriority in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketPriority() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketPriority.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketPriorityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketPriority))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketPriority in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketPriority() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketPriority.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketPriorityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketPriority)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketPriority in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTicketPriorityWithPatch() throws Exception {
        // Initialize the database
        insertedTicketPriority = ticketPriorityRepository.saveAndFlush(ticketPriority);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketPriority using partial update
        TicketPriority partialUpdatedTicketPriority = new TicketPriority();
        partialUpdatedTicketPriority.setId(ticketPriority.getId());

        partialUpdatedTicketPriority.name(UPDATED_NAME);

        restTicketPriorityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketPriority.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketPriority))
            )
            .andExpect(status().isOk());

        // Validate the TicketPriority in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketPriorityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTicketPriority, ticketPriority),
            getPersistedTicketPriority(ticketPriority)
        );
    }

    @Test
    @Transactional
    void fullUpdateTicketPriorityWithPatch() throws Exception {
        // Initialize the database
        insertedTicketPriority = ticketPriorityRepository.saveAndFlush(ticketPriority);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketPriority using partial update
        TicketPriority partialUpdatedTicketPriority = new TicketPriority();
        partialUpdatedTicketPriority.setId(ticketPriority.getId());

        partialUpdatedTicketPriority.name(UPDATED_NAME);

        restTicketPriorityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketPriority.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketPriority))
            )
            .andExpect(status().isOk());

        // Validate the TicketPriority in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketPriorityUpdatableFieldsEquals(partialUpdatedTicketPriority, getPersistedTicketPriority(partialUpdatedTicketPriority));
    }

    @Test
    @Transactional
    void patchNonExistingTicketPriority() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketPriority.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketPriorityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketPriority.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketPriority))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketPriority in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketPriority() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketPriority.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketPriorityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketPriority))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketPriority in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketPriority() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketPriority.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketPriorityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ticketPriority)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketPriority in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTicketPriority() throws Exception {
        // Initialize the database
        insertedTicketPriority = ticketPriorityRepository.saveAndFlush(ticketPriority);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ticketPriority
        restTicketPriorityMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketPriority.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ticketPriorityRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TicketPriority getPersistedTicketPriority(TicketPriority ticketPriority) {
        return ticketPriorityRepository.findById(ticketPriority.getId()).orElseThrow();
    }

    protected void assertPersistedTicketPriorityToMatchAllProperties(TicketPriority expectedTicketPriority) {
        assertTicketPriorityAllPropertiesEquals(expectedTicketPriority, getPersistedTicketPriority(expectedTicketPriority));
    }

    protected void assertPersistedTicketPriorityToMatchUpdatableProperties(TicketPriority expectedTicketPriority) {
        assertTicketPriorityAllUpdatablePropertiesEquals(expectedTicketPriority, getPersistedTicketPriority(expectedTicketPriority));
    }
}
