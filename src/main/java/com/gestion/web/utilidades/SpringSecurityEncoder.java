package com.gestion.web.utilidades;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityEncoder {

	/**
	 * Metodo que encripta el password introducido por el usuario
	 * Se registra como componente de Spring para poder utilizarlo posteriormente con @Bean
	 * @return BCryptPasswordEncoder
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}	
	
}
