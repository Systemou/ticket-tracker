import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('TicketCategory e2e test', () => {
  const ticketCategoryPageUrl = '/ticket-category';
  const ticketCategoryPageUrlPattern = new RegExp('/ticket-category(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const ticketCategorySample = { name: 'supposing' };

  let ticketCategory;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/ticket-categories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ticket-categories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ticket-categories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (ticketCategory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ticket-categories/${ticketCategory.id}`,
      }).then(() => {
        ticketCategory = undefined;
      });
    }
  });

  it('TicketCategories menu should load TicketCategories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ticket-category');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TicketCategory').should('exist');
    cy.url().should('match', ticketCategoryPageUrlPattern);
  });

  describe('TicketCategory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ticketCategoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TicketCategory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ticket-category/new$'));
        cy.getEntityCreateUpdateHeading('TicketCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketCategoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ticket-categories',
          body: ticketCategorySample,
        }).then(({ body }) => {
          ticketCategory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ticket-categories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [ticketCategory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(ticketCategoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TicketCategory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ticketCategory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketCategoryPageUrlPattern);
      });

      it('edit button click should load edit TicketCategory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TicketCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketCategoryPageUrlPattern);
      });

      it('edit button click should load edit TicketCategory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TicketCategory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketCategoryPageUrlPattern);
      });

      it('last delete button click should delete instance of TicketCategory', () => {
        cy.intercept('GET', '/api/ticket-categories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('ticketCategory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketCategoryPageUrlPattern);

        ticketCategory = undefined;
      });
    });
  });

  describe('new TicketCategory page', () => {
    beforeEach(() => {
      cy.visit(`${ticketCategoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TicketCategory');
    });

    it('should create an instance of TicketCategory', () => {
      cy.get(`[data-cy="name"]`).type('foolish');
      cy.get(`[data-cy="name"]`).should('have.value', 'foolish');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        ticketCategory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', ticketCategoryPageUrlPattern);
    });
  });
});
