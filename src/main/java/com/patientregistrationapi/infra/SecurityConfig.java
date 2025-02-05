package com.patientregistrationapi.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private SecurityFilter securityFilter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		 http
         .csrf(csrf -> csrf.disable()) // Desativa CSRF usando lambda DSL
         .sessionManagement(session -> 
             session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configura política de sessão
         )
         .authorizeHttpRequests(auth -> auth
                 .requestMatchers(HttpMethod.POST, "/login/**").permitAll() // Permite POSTs para endpoints específicos
                 .requestMatchers("/swagger-ui.html/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()  // Permite acesso a Swagger UI
                 .anyRequest().authenticated() // Exige autenticação para qualquer outra requisição
             );
         
         // Adiciona o filtro personalizado antes do UsernamePasswordAuthenticationFilter
         http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
     
		 return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
