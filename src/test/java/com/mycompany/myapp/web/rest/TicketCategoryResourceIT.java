package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TicketCategoryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TicketCategory;
import com.mycompany.myapp.repository.TicketCategoryRepository;
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
 * Integration tests for the {@link TicketCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ticket-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketCategoryRepository ticketCategoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketCategoryMockMvc;

    private TicketCategory ticketCategory;

    private TicketCategory insertedTicketCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketCategory createEntity() {
        return new TicketCategory().name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketCategory createUpdatedEntity() {
        return new TicketCategory().name(UPDATED_NAME);
    }

    @BeforeEach
    void initTest() {
        ticketCategory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTicketCategory != null) {
            ticketCategoryRepository.delete(insertedTicketCategory);
            insertedTicketCategory = null;
        }
    }

    @Test
    @Transactional
    void createTicketCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TicketCategory
        var returnedTicketCategory = om.readValue(
            restTicketCategoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCategory)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TicketCategory.class
        );

        // Validate the TicketCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTicketCategoryUpdatableFieldsEquals(returnedTicketCategory, getPersistedTicketCategory(returnedTicketCategory));

        insertedTicketCategory = returnedTicketCategory;
    }

    @Test
    @Transactional
    void createTicketCategoryWithExistingId() throws Exception {
        // Create the TicketCategory with an existing ID
        ticketCategory.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCategory)))
            .andExpect(status().isBadRequest());

        // Validate the TicketCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketCategory.setName(null);

        // Create the TicketCategory, which fails.

        restTicketCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCategory)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTicketCategories() throws Exception {
        // Initialize the database
        insertedTicketCategory = ticketCategoryRepository.saveAndFlush(ticketCategory);

        // Get all the ticketCategoryList
        restTicketCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getTicketCategory() throws Exception {
        // Initialize the database
        insertedTicketCategory = ticketCategoryRepository.saveAndFlush(ticketCategory);

        // Get the ticketCategory
        restTicketCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingTicketCategory() throws Exception {
        // Get the ticketCategory
        restTicketCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketCategory() throws Exception {
        // Initialize the database
        insertedTicketCategory = ticketCategoryRepository.saveAndFlush(ticketCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketCategory
        TicketCategory updatedTicketCategory = ticketCategoryRepository.findById(ticketCategory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTicketCategory are not directly saved in db
        em.detach(updatedTicketCategory);
        updatedTicketCategory.name(UPDATED_NAME);

        restTicketCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTicketCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTicketCategory))
            )
            .andExpect(status().isOk());

        // Validate the TicketCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketCategoryToMatchAllProperties(updatedTicketCategory);
    }

    @Test
    @Transactional
    void putNonExistingTicketCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCategory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCategory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCategory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCategory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTicketCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedTicketCategory = ticketCategoryRepository.saveAndFlush(ticketCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketCategory using partial update
        TicketCategory partialUpdatedTicketCategory = new TicketCategory();
        partialUpdatedTicketCategory.setId(ticketCategory.getId());

        partialUpdatedTicketCategory.name(UPDATED_NAME);

        restTicketCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketCategory))
            )
            .andExpect(status().isOk());

        // Validate the TicketCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTicketCategory, ticketCategory),
            getPersistedTicketCategory(ticketCategory)
        );
    }

    @Test
    @Transactional
    void fullUpdateTicketCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedTicketCategory = ticketCategoryRepository.saveAndFlush(ticketCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketCategory using partial update
        TicketCategory partialUpdatedTicketCategory = new TicketCategory();
        partialUpdatedTicketCategory.setId(ticketCategory.getId());

        partialUpdatedTicketCategory.name(UPDATED_NAME);

        restTicketCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketCategory))
            )
            .andExpect(status().isOk());

        // Validate the TicketCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketCategoryUpdatableFieldsEquals(partialUpdatedTicketCategory, getPersistedTicketCategory(partialUpdatedTicketCategory));
    }

    @Test
    @Transactional
    void patchNonExistingTicketCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCategory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCategory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCategory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCategoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ticketCategory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTicketCategory() throws Exception {
        // Initialize the database
        insertedTicketCategory = ticketCategoryRepository.saveAndFlush(ticketCategory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ticketCategory
        restTicketCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ticketCategoryRepository.count();
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

    protected TicketCategory getPersistedTicketCategory(TicketCategory ticketCategory) {
        return ticketCategoryRepository.findById(ticketCategory.getId()).orElseThrow();
    }

    protected void assertPersistedTicketCategoryToMatchAllProperties(TicketCategory expectedTicketCategory) {
        assertTicketCategoryAllPropertiesEquals(expectedTicketCategory, getPersistedTicketCategory(expectedTicketCategory));
    }

    protected void assertPersistedTicketCategoryToMatchUpdatableProperties(TicketCategory expectedTicketCategory) {
        assertTicketCategoryAllUpdatablePropertiesEquals(expectedTicketCategory, getPersistedTicketCategory(expectedTicketCategory));
    }
}
