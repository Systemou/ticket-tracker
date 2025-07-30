package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PasswordChangeDTO}.
 */
class PasswordChangeDTOTest {

    private PasswordChangeDTO passwordChangeDTO;

    @BeforeEach
    void setUp() {
        passwordChangeDTO = new PasswordChangeDTO();
    }

    @Test
    void shouldCreatePasswordChangeDTOWithDefaultConstructor() {
        assertThat(passwordChangeDTO).isNotNull();
        assertThat(passwordChangeDTO.getCurrentPassword()).isNull();
        assertThat(passwordChangeDTO.getNewPassword()).isNull();
    }

    @Test
    void shouldCreatePasswordChangeDTOWithParameters() {
        String currentPassword = "oldPassword123";
        String newPassword = "newPassword123";

        PasswordChangeDTO dtoWithParams = new PasswordChangeDTO(currentPassword, newPassword);

        assertThat(dtoWithParams.getCurrentPassword()).isEqualTo(currentPassword);
        assertThat(dtoWithParams.getNewPassword()).isEqualTo(newPassword);
    }

    @Test
    void shouldSetAndGetCurrentPassword() {
        String currentPassword = "oldPassword123";
        passwordChangeDTO.setCurrentPassword(currentPassword);

        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo(currentPassword);
    }

    @Test
    void shouldSetAndGetNewPassword() {
        String newPassword = "newPassword123";
        passwordChangeDTO.setNewPassword(newPassword);

        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo(newPassword);
    }

    @Test
    void shouldSetAndGetAllProperties() {
        String currentPassword = "oldPassword123";
        String newPassword = "newPassword123";

        passwordChangeDTO.setCurrentPassword(currentPassword);
        passwordChangeDTO.setNewPassword(newPassword);

        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo(currentPassword);
        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo(newPassword);
    }

    @Test
    void shouldHandleNullValues() {
        passwordChangeDTO.setCurrentPassword(null);
        passwordChangeDTO.setNewPassword(null);

        assertThat(passwordChangeDTO.getCurrentPassword()).isNull();
        assertThat(passwordChangeDTO.getNewPassword()).isNull();
    }

    @Test
    void shouldHandleEmptyStrings() {
        passwordChangeDTO.setCurrentPassword("");
        passwordChangeDTO.setNewPassword("");

        assertThat(passwordChangeDTO.getCurrentPassword()).isEmpty();
        assertThat(passwordChangeDTO.getNewPassword()).isEmpty();
    }

    @Test
    void shouldHandleSpecialCharacters() {
        String currentPasswordWithSpecialChars = "old@passw0rd!";
        String newPasswordWithSpecialChars = "new@passw0rd!";

        passwordChangeDTO.setCurrentPassword(currentPasswordWithSpecialChars);
        passwordChangeDTO.setNewPassword(newPasswordWithSpecialChars);

        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo(currentPasswordWithSpecialChars);
        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo(newPasswordWithSpecialChars);
    }

    @Test
    void shouldHandleLongPasswords() {
        String longCurrentPassword = "a".repeat(1000);
        String longNewPassword = "b".repeat(1000);

        passwordChangeDTO.setCurrentPassword(longCurrentPassword);
        passwordChangeDTO.setNewPassword(longNewPassword);

        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo(longCurrentPassword);
        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo(longNewPassword);
    }

    @Test
    void shouldHandleWhitespace() {
        String currentPasswordWithWhitespace = "  old password  ";
        String newPasswordWithWhitespace = "  new password  ";

        passwordChangeDTO.setCurrentPassword(currentPasswordWithWhitespace);
        passwordChangeDTO.setNewPassword(newPasswordWithWhitespace);

        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo(currentPasswordWithWhitespace);
        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo(newPasswordWithWhitespace);
    }

    @Test
    void shouldHandleUnicodeCharacters() {
        String currentPasswordWithUnicode = "old密码123";
        String newPasswordWithUnicode = "new密码123";

        passwordChangeDTO.setCurrentPassword(currentPasswordWithUnicode);
        passwordChangeDTO.setNewPassword(newPasswordWithUnicode);

        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo(currentPasswordWithUnicode);
        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo(newPasswordWithUnicode);
    }

    @Test
    void shouldHandleMultipleUpdates() {
        // First set
        passwordChangeDTO.setCurrentPassword("password1");
        passwordChangeDTO.setNewPassword("newpassword1");
        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo("password1");
        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo("newpassword1");

        // Update
        passwordChangeDTO.setCurrentPassword("password2");
        passwordChangeDTO.setNewPassword("newpassword2");
        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo("password2");
        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo("newpassword2");

        // Update again
        passwordChangeDTO.setCurrentPassword("password3");
        passwordChangeDTO.setNewPassword("newpassword3");
        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo("password3");
        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo("newpassword3");
    }

    @Test
    void shouldHandleSamePasswords() {
        String samePassword = "samePassword123";
        passwordChangeDTO.setCurrentPassword(samePassword);
        passwordChangeDTO.setNewPassword(samePassword);

        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo(samePassword);
        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo(samePassword);
    }

    @Test
    void shouldHandleConstructorWithNullValues() {
        PasswordChangeDTO dtoWithNulls = new PasswordChangeDTO(null, null);

        assertThat(dtoWithNulls.getCurrentPassword()).isNull();
        assertThat(dtoWithNulls.getNewPassword()).isNull();
    }

    @Test
    void shouldHandleConstructorWithEmptyStrings() {
        PasswordChangeDTO dtoWithEmpties = new PasswordChangeDTO("", "");

        assertThat(dtoWithEmpties.getCurrentPassword()).isEmpty();
        assertThat(dtoWithEmpties.getNewPassword()).isEmpty();
    }
}
