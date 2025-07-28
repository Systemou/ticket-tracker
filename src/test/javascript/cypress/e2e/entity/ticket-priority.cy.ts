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

describe('TicketPriority e2e test', () => {
  const ticketPriorityPageUrl = '/ticket-priority';
  const ticketPriorityPageUrlPattern = new RegExp('/ticket-priority(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const ticketPrioritySample = { name: 'tightly' };

  let ticketPriority;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/ticket-priorities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ticket-priorities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ticket-priorities/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (ticketPriority) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ticket-priorities/${ticketPriority.id}`,
      }).then(() => {
        ticketPriority = undefined;
      });
    }
  });

  it('TicketPriorities menu should load TicketPriorities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ticket-priority');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TicketPriority').should('exist');
    cy.url().should('match', ticketPriorityPageUrlPattern);
  });

  describe('TicketPriority page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ticketPriorityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TicketPriority page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ticket-priority/new$'));
        cy.getEntityCreateUpdateHeading('TicketPriority');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPriorityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ticket-priorities',
          body: ticketPrioritySample,
        }).then(({ body }) => {
          ticketPriority = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ticket-priorities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [ticketPriority],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(ticketPriorityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TicketPriority page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ticketPriority');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPriorityPageUrlPattern);
      });

      it('edit button click should load edit TicketPriority page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TicketPriority');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPriorityPageUrlPattern);
      });

      it('edit button click should load edit TicketPriority page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TicketPriority');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPriorityPageUrlPattern);
      });

      it('last delete button click should delete instance of TicketPriority', () => {
        cy.intercept('GET', '/api/ticket-priorities/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('ticketPriority').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPriorityPageUrlPattern);

        ticketPriority = undefined;
      });
    });
  });

  describe('new TicketPriority page', () => {
    beforeEach(() => {
      cy.visit(`${ticketPriorityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TicketPriority');
    });

    it('should create an instance of TicketPriority', () => {
      cy.get(`[data-cy="name"]`).type('coordination slap absentmindedly');
      cy.get(`[data-cy="name"]`).should('have.value', 'coordination slap absentmindedly');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        ticketPriority = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', ticketPriorityPageUrlPattern);
    });
  });
});
