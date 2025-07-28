**Title**: Submit a New Support Ticket

**As a** User

**I want** to be able to submit a new support ticket with relevant details

**So that** I can report an issue and receive assistance from the support team.

**Business Logic**:
- Ticket title must be at least 5 characters long.
- Ticket description must be at least 20 characters long.
- User must select a category and priority level for the ticket.
- Creation date is automatically generated upon submission.

**Acceptance Criteria**:
1.  A user can successfully submit a new support ticket with a title, description, category, and priority.
2.  The newly submitted ticket is visible in the user's list of tickets.
3.  The ticket includes the correct creation date and an initial status of "Open".
4.  Error messages are displayed if required fields are missing or invalid.

**Functional Requirements**:
-  Provide a form for users to enter ticket details (title, description, category, priority).
-  Implement validation to ensure required fields are filled and meet minimum length requirements.
-  Automatically assign a creation date to the ticket upon submission.
-  Set the initial status of the ticket to "Open".
-  Store the ticket data in the database.
-  Display a success message upon successful submission.

**Non-Functional Requirements**:
-  The ticket submission process should be responsive and user-friendly.
-  The application should handle a large number of concurrent ticket submissions without performance degradation.
-  The ticket data should be securely stored in the database.
-  The submission process should be accessible on various devices and browsers.

**UI Design**:
-  The ticket submission form should be clear and easy to understand.
-  Use dropdown menus for category and priority selection.
-  Provide clear error messages for invalid or missing fields.
-  Include a "Submit" button to submit the ticket.
-  Consider using a rich text editor for the description field to allow for formatting.
