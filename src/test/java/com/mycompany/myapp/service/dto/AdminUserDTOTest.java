package com.mycompany.myapp.service.dto;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.security.AuthoritiesConstants;

/**
 * Unit tests for {@link AdminUserDTO}.
 */
class AdminUserDTOTest {

    private AdminUserDTO adminUserDTO;
    private User user;

    @BeforeEach
    void setUp() {
        adminUserDTO = new AdminUserDTO();
        user = createTestUser();
    }

    private User createTestUser() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setPassword(RandomStringUtils.insecure().nextAlphanumeric(60));
        testUser.setActivated(true);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setImageUrl("http://example.com/image.jpg");
        testUser.setLangKey("en");
        testUser.setCreatedBy("admin");
        testUser.setCreatedDate(Instant.now());
        testUser.setLastModifiedBy("admin");
        testUser.setLastModifiedDate(Instant.now());

        Set<Authority> authorities = new HashSet<>();
        Authority userAuthority = new Authority();
        userAuthority.setName(AuthoritiesConstants.USER);
        authorities.add(userAuthority);
        Authority adminAuthority = new Authority();
        adminAuthority.setName(AuthoritiesConstants.ADMIN);
        authorities.add(adminAuthority);
        testUser.setAuthorities(authorities);

        return testUser;
    }

    @Test
    void shouldCreateAdminUserDTOWithDefaultConstructor() {
        assertThat(adminUserDTO).isNotNull();
        assertThat(adminUserDTO.getId()).isNull();
        assertThat(adminUserDTO.getLogin()).isNull();
        assertThat(adminUserDTO.getFirstName()).isNull();
        assertThat(adminUserDTO.getLastName()).isNull();
        assertThat(adminUserDTO.getEmail()).isNull();
        assertThat(adminUserDTO.getImageUrl()).isNull();
        assertThat(adminUserDTO.isActivated()).isFalse();
        assertThat(adminUserDTO.getLangKey()).isNull();
        assertThat(adminUserDTO.getCreatedBy()).isNull();
        assertThat(adminUserDTO.getCreatedDate()).isNull();
        assertThat(adminUserDTO.getLastModifiedBy()).isNull();
        assertThat(adminUserDTO.getLastModifiedDate()).isNull();
        assertThat(adminUserDTO.getAuthorities()).isNull();
    }

    @Test
    void shouldCreateAdminUserDTOFromUser() {
        AdminUserDTO dtoFromUser = new AdminUserDTO(user);

        assertThat(dtoFromUser.getId()).isEqualTo(user.getId());
        assertThat(dtoFromUser.getLogin()).isEqualTo(user.getLogin());
        assertThat(dtoFromUser.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(dtoFromUser.getLastName()).isEqualTo(user.getLastName());
        assertThat(dtoFromUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(dtoFromUser.getImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(dtoFromUser.isActivated()).isEqualTo(user.isActivated());
        assertThat(dtoFromUser.getLangKey()).isEqualTo(user.getLangKey());
        assertThat(dtoFromUser.getCreatedBy()).isEqualTo(user.getCreatedBy());
        assertThat(dtoFromUser.getCreatedDate()).isEqualTo(user.getCreatedDate());
        assertThat(dtoFromUser.getLastModifiedBy()).isEqualTo(user.getLastModifiedBy());
        assertThat(dtoFromUser.getLastModifiedDate()).isEqualTo(user.getLastModifiedDate());
        assertThat(dtoFromUser.getAuthorities()).containsExactlyInAnyOrder(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN);
    }

    @Test
    void shouldSetAndGetAllProperties() {
        // Set properties
        adminUserDTO.setId(1L);
        adminUserDTO.setLogin("testuser");
        adminUserDTO.setFirstName("Test");
        adminUserDTO.setLastName("User");
        adminUserDTO.setEmail("test@example.com");
        adminUserDTO.setImageUrl("http://example.com/image.jpg");
        adminUserDTO.setActivated(true);
        adminUserDTO.setLangKey("en");
        adminUserDTO.setCreatedBy("admin");
        adminUserDTO.setCreatedDate(Instant.now());
        adminUserDTO.setLastModifiedBy("admin");
        adminUserDTO.setLastModifiedDate(Instant.now());

        Set<String> authorities = new HashSet<>();
        authorities.add("ROLE_USER");
        authorities.add("ROLE_ADMIN");
        adminUserDTO.setAuthorities(authorities);

        // Verify properties
        assertThat(adminUserDTO.getId()).isEqualTo(1L);
        assertThat(adminUserDTO.getLogin()).isEqualTo("testuser");
        assertThat(adminUserDTO.getFirstName()).isEqualTo("Test");
        assertThat(adminUserDTO.getLastName()).isEqualTo("User");
        assertThat(adminUserDTO.getEmail()).isEqualTo("test@example.com");
        assertThat(adminUserDTO.getImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(adminUserDTO.isActivated()).isTrue();
        assertThat(adminUserDTO.getLangKey()).isEqualTo("en");
        assertThat(adminUserDTO.getCreatedBy()).isEqualTo("admin");
        assertThat(adminUserDTO.getCreatedDate()).isNotNull();
        assertThat(adminUserDTO.getLastModifiedBy()).isEqualTo("admin");
        assertThat(adminUserDTO.getLastModifiedDate()).isNotNull();
        assertThat(adminUserDTO.getAuthorities()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void shouldHandleNullValues() {
        adminUserDTO.setId(null);
        adminUserDTO.setLogin(null);
        adminUserDTO.setFirstName(null);
        adminUserDTO.setLastName(null);
        adminUserDTO.setEmail(null);
        adminUserDTO.setImageUrl(null);
        adminUserDTO.setLangKey(null);
        adminUserDTO.setCreatedBy(null);
        adminUserDTO.setCreatedDate(null);
        adminUserDTO.setLastModifiedBy(null);
        adminUserDTO.setLastModifiedDate(null);
        adminUserDTO.setAuthorities(null);

        assertThat(adminUserDTO.getId()).isNull();
        assertThat(adminUserDTO.getLogin()).isNull();
        assertThat(adminUserDTO.getFirstName()).isNull();
        assertThat(adminUserDTO.getLastName()).isNull();
        assertThat(adminUserDTO.getEmail()).isNull();
        assertThat(adminUserDTO.getImageUrl()).isNull();
        assertThat(adminUserDTO.getLangKey()).isNull();
        assertThat(adminUserDTO.getCreatedBy()).isNull();
        assertThat(adminUserDTO.getCreatedDate()).isNull();
        assertThat(adminUserDTO.getLastModifiedBy()).isNull();
        assertThat(adminUserDTO.getLastModifiedDate()).isNull();
        assertThat(adminUserDTO.getAuthorities()).isNull();
        assertThat(adminUserDTO.isActivated()).isFalse(); // Default value
    }

    @Test
    void shouldHandleEmptyAuthorities() {
        Set<String> emptyAuthorities = new HashSet<>();
        adminUserDTO.setAuthorities(emptyAuthorities);

        assertThat(adminUserDTO.getAuthorities()).isEmpty();
    }

    @Test
    void shouldReturnCorrectToString() {
        adminUserDTO.setLogin("testuser");
        adminUserDTO.setFirstName("Test");
        adminUserDTO.setLastName("User");
        adminUserDTO.setEmail("test@example.com");
        adminUserDTO.setImageUrl("http://example.com/image.jpg");
        adminUserDTO.setActivated(true);
        adminUserDTO.setLangKey("en");
        adminUserDTO.setCreatedBy("admin");
        adminUserDTO.setCreatedDate(Instant.now());
        adminUserDTO.setLastModifiedBy("admin");
        adminUserDTO.setLastModifiedDate(Instant.now());

        Set<String> authorities = new HashSet<>();
        authorities.add("ROLE_USER");
        adminUserDTO.setAuthorities(authorities);

        String result = adminUserDTO.toString();

        assertThat(result).contains("AdminUserDTO{");
        assertThat(result).contains("login='testuser'");
        assertThat(result).contains("firstName='Test'");
        assertThat(result).contains("lastName='User'");
        assertThat(result).contains("email='test@example.com'");
        assertThat(result).contains("imageUrl='http://example.com/image.jpg'");
        assertThat(result).contains("activated=true");
        assertThat(result).contains("langKey='en'");
        assertThat(result).contains("createdBy=admin");
        assertThat(result).contains("lastModifiedBy='admin'");
        assertThat(result).contains("authorities=");
    }

    @Test
    void shouldHandleUserWithNullAuthorities() {
        user.setAuthorities(null);
        AdminUserDTO dtoFromUser = new AdminUserDTO(user);

        assertThat(dtoFromUser.getAuthorities()).isNotNull();
        assertThat(dtoFromUser.getAuthorities()).isEmpty();
    }

    @Test
    void shouldHandleUserWithEmptyAuthorities() {
        user.setAuthorities(new HashSet<>());
        AdminUserDTO dtoFromUser = new AdminUserDTO(user);

        assertThat(dtoFromUser.getAuthorities()).isNotNull();
        assertThat(dtoFromUser.getAuthorities()).isEmpty();
    }
}
