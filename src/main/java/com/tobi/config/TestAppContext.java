package com.tobi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;

import com.tobi.service.UserService;
import com.tobi.test.UserServiceTest.TestUserServiceImpl;
import com.tobi.utils.DummyMailSender;

@Configuration
@Profile("test")
public class TestAppContext {
	
	@Bean
	public UserService testUserService() {
		return new TestUserServiceImpl();
	}
	
	@Bean
	public MailSender mailSender() {
		return new DummyMailSender();
	}
	
	
	
}
