import React from 'react';
import { Plus } from 'lucide-react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="plus" to="/ticket/new">
        Submit New Ticket
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket">
        My Tickets
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket-category">
        Ticket Categories
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket-priority">
        Ticket Priorities
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
