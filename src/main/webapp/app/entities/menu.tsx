import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/ticket">
        Ticket
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket-category">
        Ticket Category
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket-priority">
        Ticket Priority
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
