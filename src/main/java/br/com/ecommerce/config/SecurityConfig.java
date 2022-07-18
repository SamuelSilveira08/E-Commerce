package br.com.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.ecommerce.filters.ExceptionHandlerFilter;
import br.com.ecommerce.filters.JwtRequestFilter;
import br.com.ecommerce.service.AuthenticationService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationService customUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private ExceptionHandlerFilter exceptionHandlerFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/products/**").permitAll()
				.antMatchers(HttpMethod.POST, "/users/**").permitAll()
				.anyRequest().authenticated();

		/*
		 * API uses token-based authentication, so CSRF is not needed, because there's no way
		 * a website can get the auth token and use it to forge a request
		 * Unless the client-side stores the token in a not http-only cookie, which would make it
		 * available to be accessible through JavaScript, so I wouldn't recommend that xD
		 */
		
		http.csrf().disable();

		http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"));

		http.rememberMe().key("w9t9ZerAvDOLEuGBehGmFEvH57rHVMEu");
		
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(exceptionHandlerFilter, JwtRequestFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
