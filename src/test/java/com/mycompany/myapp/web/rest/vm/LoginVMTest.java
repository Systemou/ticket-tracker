package com.mycompany.myapp.web.rest.vm;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LoginVM}.
 */
class LoginVMTest {

    private LoginVM loginVM;

    @BeforeEach
    void setUp() {
        loginVM = new LoginVM();
    }

    @Test
    void shouldCreateLoginVMWithDefaultConstructor() {
        assertThat(loginVM).isNotNull();
        assertThat(loginVM.getUsername()).isNull();
        assertThat(loginVM.getPassword()).isNull();
        assertThat(loginVM.isRememberMe()).isFalse();
    }

    @Test
    void shouldSetAndGetUsername() {
        String username = "testuser";
        loginVM.setUsername(username);

        assertThat(loginVM.getUsername()).isEqualTo(username);
    }

    @Test
    void shouldSetAndGetPassword() {
        String password = "testPassword123";
        loginVM.setPassword(password);

        assertThat(loginVM.getPassword()).isEqualTo(password);
    }

    @Test
    void shouldSetAndGetRememberMe() {
        loginVM.setRememberMe(true);
        assertThat(loginVM.isRememberMe()).isTrue();

        loginVM.setRememberMe(false);
        assertThat(loginVM.isRememberMe()).isFalse();
    }

    @Test
    void shouldSetAndGetAllProperties() {
        String username = "testuser";
        String password = "testPassword123";
        boolean rememberMe = true;

        loginVM.setUsername(username);
        loginVM.setPassword(password);
        loginVM.setRememberMe(rememberMe);

        assertThat(loginVM.getUsername()).isEqualTo(username);
        assertThat(loginVM.getPassword()).isEqualTo(password);
        assertThat(loginVM.isRememberMe()).isEqualTo(rememberMe);
    }

    @Test
    void shouldReturnCorrectToString() {
        loginVM.setUsername("testuser");
        loginVM.setPassword("password123");
        loginVM.setRememberMe(true);

        String result = loginVM.toString();

        assertThat(result).contains("LoginVM{");
        assertThat(result).contains("username='testuser'");
        assertThat(result).contains("rememberMe=true");
        assertThat(result).doesNotContain("password="); // Password should not be in toString for security
    }

    @Test
    void shouldHandleNullValues() {
        loginVM.setUsername(null);
        loginVM.setPassword(null);

        assertThat(loginVM.getUsername()).isNull();
        assertThat(loginVM.getPassword()).isNull();
        assertThat(loginVM.isRememberMe()).isFalse(); // Default value
    }

    @Test
    void shouldHandleEmptyStrings() {
        loginVM.setUsername("");
        loginVM.setPassword("");

        assertThat(loginVM.getUsername()).isEmpty();
        assertThat(loginVM.getPassword()).isEmpty();
    }

    @Test
    void shouldHandleSpecialCharacters() {
        String usernameWithSpecialChars = "test@user.com";
        String passwordWithSpecialChars = "p@ssw0rd!";

        loginVM.setUsername(usernameWithSpecialChars);
        loginVM.setPassword(passwordWithSpecialChars);

        assertThat(loginVM.getUsername()).isEqualTo(usernameWithSpecialChars);
        assertThat(loginVM.getPassword()).isEqualTo(passwordWithSpecialChars);
    }

    @Test
    void shouldHandleLongValues() {
        String longUsername = "a".repeat(50); // Max length
        String longPassword = "b".repeat(100); // Max length

        loginVM.setUsername(longUsername);
        loginVM.setPassword(longPassword);

        assertThat(loginVM.getUsername()).isEqualTo(longUsername);
        assertThat(loginVM.getPassword()).isEqualTo(longPassword);
    }
}
