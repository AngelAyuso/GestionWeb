package com.gestion.web.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class ConfiguracionMensajes implements WebMvcConfigurer {
	  @Bean
	  public LocaleResolver localeResolver() {
	    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
	    localeResolver.setDefaultLocale(Locale.getDefault());
	    return localeResolver;
	  }

	  @Bean
	  public LocaleChangeInterceptor localeChangeInterceptor() {
	    LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
	    localeInterceptor.setIgnoreInvalidLocale(true);
	    localeInterceptor.setParamName("lang");
	    return localeInterceptor;
	  }
	  
	  @Bean
	  public MessageSource messageSource() {
	    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	    messageSource.setBasename("classpath:i18n/messages");
	    messageSource.setDefaultEncoding("UTF-8");
	    return messageSource;
	  }
	  
	  @Override
	  public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(localeChangeInterceptor());
	  }
}