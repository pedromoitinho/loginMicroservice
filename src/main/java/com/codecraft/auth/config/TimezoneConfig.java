package com.codecraft.auth.config;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class TimezoneConfig {

	@PostConstruct
	public void init() {
		// Set default timezone for the entire application to Recife timezone (BRT -
		// UTC-3)
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/Recife")));
	}
}
