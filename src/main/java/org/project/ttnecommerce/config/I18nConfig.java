package org.project.ttnecommerce.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

@Configuration
public class I18nConfig {
    private static final List<Locale> SUPPORTED_LOCALES = List.of(
            Locale.ENGLISH,
            Locale.FRENCH,
            Locale.forLanguageTag("es"),
            Locale.forLanguageTag("de"),
            Locale.forLanguageTag("it")
    );

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                String language = request.getParameter("lang");
                if (language != null && !language.isBlank()) {
                    Locale locale = resolveSupportedLocale(Locale.forLanguageTag(language));
                    if (locale != null) {
                        return locale;
                    }
                }

                Enumeration<Locale> requestLocales = request.getLocales();
                while (requestLocales.hasMoreElements()) {
                    Locale locale = resolveSupportedLocale(requestLocales.nextElement());
                    if (locale != null) {
                        return locale;
                    }
                }

                return Locale.ENGLISH;
            }
        };
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        localeResolver.setSupportedLocales(SUPPORTED_LOCALES);
        return localeResolver;
    }

    private static Locale resolveSupportedLocale(Locale candidate) {
        if (candidate == null) {
            return null;
        }

        return SUPPORTED_LOCALES.stream()
                .filter(locale -> locale.getLanguage().equalsIgnoreCase(candidate.getLanguage()))
                .findFirst()
                .orElse(null);
    }

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setValidationMessageSource(messageSource);
        return validatorFactoryBean;
    }
}