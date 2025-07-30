package com.mycompany.myapp;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Unit tests for the {@link ApplicationWebXml}.
 */
class ApplicationWebXmlTest {

    @Test
    void shouldExtendSpringBootServletInitializer() {
        // Then
        assertThat(ApplicationWebXml.class.getSuperclass()).isEqualTo(SpringBootServletInitializer.class);
    }

    @Test
    void shouldBePublicClass() {
        // Then
        assertThat(ApplicationWebXml.class.getModifiers() & java.lang.reflect.Modifier.PUBLIC).isNotZero();
    }
}
