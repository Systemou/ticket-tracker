package com.mycompany.myapp.web.rest.vm;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link KeyAndPasswordVM}.
 */
class KeyAndPasswordVMTest {

    private KeyAndPasswordVM keyAndPasswordVM;

    @BeforeEach
    void setUp() {
        keyAndPasswordVM = new KeyAndPasswordVM();
    }

    @Test
    void shouldCreateKeyAndPasswordVMWithDefaultConstructor() {
        assertThat(keyAndPasswordVM).isNotNull();
        assertThat(keyAndPasswordVM.getKey()).isNull();
        assertThat(keyAndPasswordVM.getNewPassword()).isNull();
    }

    @Test
    void shouldSetAndGetKey() {
        String key = "test-activation-key-123";
        keyAndPasswordVM.setKey(key);

        assertThat(keyAndPasswordVM.getKey()).isEqualTo(key);
    }

    @Test
    void shouldSetAndGetNewPassword() {
        String newPassword = "newPassword123";
        keyAndPasswordVM.setNewPassword(newPassword);

        assertThat(keyAndPasswordVM.getNewPassword()).isEqualTo(newPassword);
    }

    @Test
    void shouldSetAndGetAllProperties() {
        String key = "test-activation-key-123";
        String newPassword = "newPassword123";

        keyAndPasswordVM.setKey(key);
        keyAndPasswordVM.setNewPassword(newPassword);

        assertThat(keyAndPasswordVM.getKey()).isEqualTo(key);
        assertThat(keyAndPasswordVM.getNewPassword()).isEqualTo(newPassword);
    }

    @Test
    void shouldHandleNullValues() {
        keyAndPasswordVM.setKey(null);
        keyAndPasswordVM.setNewPassword(null);

        assertThat(keyAndPasswordVM.getKey()).isNull();
        assertThat(keyAndPasswordVM.getNewPassword()).isNull();
    }

    @Test
    void shouldHandleEmptyStrings() {
        keyAndPasswordVM.setKey("");
        keyAndPasswordVM.setNewPassword("");

        assertThat(keyAndPasswordVM.getKey()).isEmpty();
        assertThat(keyAndPasswordVM.getNewPassword()).isEmpty();
    }

    @Test
    void shouldHandleSpecialCharacters() {
        String keyWithSpecialChars = "test-key@123!";
        String passwordWithSpecialChars = "p@ssw0rd!";

        keyAndPasswordVM.setKey(keyWithSpecialChars);
        keyAndPasswordVM.setNewPassword(passwordWithSpecialChars);

        assertThat(keyAndPasswordVM.getKey()).isEqualTo(keyWithSpecialChars);
        assertThat(keyAndPasswordVM.getNewPassword()).isEqualTo(passwordWithSpecialChars);
    }

    @Test
    void shouldHandleLongValues() {
        String longKey = "a".repeat(1000);
        String longPassword = "b".repeat(1000);

        keyAndPasswordVM.setKey(longKey);
        keyAndPasswordVM.setNewPassword(longPassword);

        assertThat(keyAndPasswordVM.getKey()).isEqualTo(longKey);
        assertThat(keyAndPasswordVM.getNewPassword()).isEqualTo(longPassword);
    }

    @Test
    void shouldHandleWhitespace() {
        String keyWithWhitespace = "  test key  ";
        String passwordWithWhitespace = "  test password  ";

        keyAndPasswordVM.setKey(keyWithWhitespace);
        keyAndPasswordVM.setNewPassword(passwordWithWhitespace);

        assertThat(keyAndPasswordVM.getKey()).isEqualTo(keyWithWhitespace);
        assertThat(keyAndPasswordVM.getNewPassword()).isEqualTo(passwordWithWhitespace);
    }

    @Test
    void shouldHandleUnicodeCharacters() {
        String keyWithUnicode = "test-key-测试-123";
        String passwordWithUnicode = "password测试123";

        keyAndPasswordVM.setKey(keyWithUnicode);
        keyAndPasswordVM.setNewPassword(passwordWithUnicode);

        assertThat(keyAndPasswordVM.getKey()).isEqualTo(keyWithUnicode);
        assertThat(keyAndPasswordVM.getNewPassword()).isEqualTo(passwordWithUnicode);
    }

    @Test
    void shouldHandleMultipleUpdates() {
        // First set
        keyAndPasswordVM.setKey("key1");
        keyAndPasswordVM.setNewPassword("password1");
        assertThat(keyAndPasswordVM.getKey()).isEqualTo("key1");
        assertThat(keyAndPasswordVM.getNewPassword()).isEqualTo("password1");

        // Update
        keyAndPasswordVM.setKey("key2");
        keyAndPasswordVM.setNewPassword("password2");
        assertThat(keyAndPasswordVM.getKey()).isEqualTo("key2");
        assertThat(keyAndPasswordVM.getNewPassword()).isEqualTo("password2");

        // Update again
        keyAndPasswordVM.setKey("key3");
        keyAndPasswordVM.setNewPassword("password3");
        assertThat(keyAndPasswordVM.getKey()).isEqualTo("key3");
        assertThat(keyAndPasswordVM.getNewPassword()).isEqualTo("password3");
    }
}
