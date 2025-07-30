package com.mycompany.myapp.web.rest.vm;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.myapp.service.dto.AdminUserDTO;

/**
 * Unit tests for {@link ManagedUserVM}.
 */
class ManagedUserVMTest {

    private ManagedUserVM managedUserVM;

    @BeforeEach
    void setUp() {
        managedUserVM = new ManagedUserVM();
    }

    @Test
    void shouldCreateManagedUserVMWithDefaultConstructor() {
        assertThat(managedUserVM).isNotNull();
        assertThat(managedUserVM.getPassword()).isNull();
    }

    @Test
    void shouldSetAndGetPassword() {
        String password = "testPassword123";
        managedUserVM.setPassword(password);

        assertThat(managedUserVM.getPassword()).isEqualTo(password);
    }

    @Test
    void shouldInheritFromAdminUserDTO() {
        assertThat(managedUserVM).isInstanceOf(AdminUserDTO.class);
    }

    @Test
    void shouldSetAndGetAllProperties() {
        // Set basic properties
        managedUserVM.setId(1L);
        managedUserVM.setLogin("testuser");
        managedUserVM.setFirstName("Test");
        managedUserVM.setLastName("User");
        managedUserVM.setEmail("test@example.com");
        managedUserVM.setImageUrl("http://example.com/image.jpg");
        managedUserVM.setActivated(true);
        managedUserVM.setLangKey("en");
        managedUserVM.setCreatedBy("admin");
        managedUserVM.setCreatedDate(Instant.now());
        managedUserVM.setLastModifiedBy("admin");
        managedUserVM.setLastModifiedDate(Instant.now());
        managedUserVM.setPassword("password123");

        // Set authorities
        Set<String> authorities = new HashSet<>();
        authorities.add("ROLE_USER");
        authorities.add("ROLE_ADMIN");
        managedUserVM.setAuthorities(authorities);

        // Verify all properties
        assertThat(managedUserVM.getId()).isEqualTo(1L);
        assertThat(managedUserVM.getLogin()).isEqualTo("testuser");
        assertThat(managedUserVM.getFirstName()).isEqualTo("Test");
        assertThat(managedUserVM.getLastName()).isEqualTo("User");
        assertThat(managedUserVM.getEmail()).isEqualTo("test@example.com");
        assertThat(managedUserVM.getImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(managedUserVM.isActivated()).isTrue();
        assertThat(managedUserVM.getLangKey()).isEqualTo("en");
        assertThat(managedUserVM.getCreatedBy()).isEqualTo("admin");
        assertThat(managedUserVM.getCreatedDate()).isNotNull();
        assertThat(managedUserVM.getLastModifiedBy()).isEqualTo("admin");
        assertThat(managedUserVM.getLastModifiedDate()).isNotNull();
        assertThat(managedUserVM.getPassword()).isEqualTo("password123");
        assertThat(managedUserVM.getAuthorities()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void shouldHaveCorrectPasswordLengthConstants() {
        assertThat(ManagedUserVM.PASSWORD_MIN_LENGTH).isEqualTo(4);
        assertThat(ManagedUserVM.PASSWORD_MAX_LENGTH).isEqualTo(100);
    }

    @Test
    void shouldReturnCorrectToString() {
        managedUserVM.setLogin("testuser");
        managedUserVM.setFirstName("Test");
        managedUserVM.setLastName("User");
        managedUserVM.setPassword("password123");

        String result = managedUserVM.toString();

        assertThat(result).contains("ManagedUserVM{");
        assertThat(result).contains("login='testuser'");
        assertThat(result).contains("firstName='Test'");
        assertThat(result).contains("lastName='User'");
        assertThat(result).contains("password123");
    }

    @Test
    void shouldHandleNullValues() {
        managedUserVM.setId(null);
        managedUserVM.setLogin(null);
        managedUserVM.setFirstName(null);
        managedUserVM.setLastName(null);
        managedUserVM.setEmail(null);
        managedUserVM.setImageUrl(null);
        managedUserVM.setLangKey(null);
        managedUserVM.setCreatedBy(null);
        managedUserVM.setCreatedDate(null);
        managedUserVM.setLastModifiedBy(null);
        managedUserVM.setLastModifiedDate(null);
        managedUserVM.setAuthorities(null);
        managedUserVM.setPassword(null);

        assertThat(managedUserVM.getId()).isNull();
        assertThat(managedUserVM.getLogin()).isNull();
        assertThat(managedUserVM.getFirstName()).isNull();
        assertThat(managedUserVM.getLastName()).isNull();
        assertThat(managedUserVM.getEmail()).isNull();
        assertThat(managedUserVM.getImageUrl()).isNull();
        assertThat(managedUserVM.getLangKey()).isNull();
        assertThat(managedUserVM.getCreatedBy()).isNull();
        assertThat(managedUserVM.getCreatedDate()).isNull();
        assertThat(managedUserVM.getLastModifiedBy()).isNull();
        assertThat(managedUserVM.getLastModifiedDate()).isNull();
        assertThat(managedUserVM.getAuthorities()).isNull();
        assertThat(managedUserVM.getPassword()).isNull();
    }
}
