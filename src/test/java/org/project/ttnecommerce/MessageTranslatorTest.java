package org.project.ttnecommerce;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.project.ttnecommerce.i18n.MessageTranslator;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTranslatorTest {

    private final MessageTranslator translator = new MessageTranslator(messageSource());

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void translatesStaticHindiMessage() {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("hi"));

        assertEquals("लॉगआउट सफल रहा", translator.get("response.logout.success"));
    }

    @Test
    void translatesDynamicFrenchMessage() {
        LocaleContextHolder.setLocale(Locale.FRENCH);

        assertEquals("Champ de tri invalide : name", translator.translate("Invalid sort field: name"));
    }

    private ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}
