package com.kiwitech.challenge;

import com.kiwitech.challenge.cors.CorsConfiguration;
import com.kiwitech.challenge.cors.CorsConfigurationSource;
import com.kiwitech.challenge.cors.CorsFilter;
import com.kiwitech.challenge.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component("CorsFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter extends CorsFilter {

	@Autowired
	public SimpleCorsFilter(Environment env) {
		super(corsConfigurationSource(env.getProperty("challenge.allowed-origin")));
	}

	private static CorsConfigurationSource corsConfigurationSource(String allowedOrigin) {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin(allowedOrigin);
		config.addAllowedHeader("*");
		config.setAllowedMethods(Arrays.asList("GET", "POST","PUT","DELETE", "HEAD", "OPTIONS"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}

}
