package com.tobi.config;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.tobi.annotation.EnableSqlService;
import com.tobi.domain.UserDao;
import com.tobi.service.UserService;
import com.tobi.test.UserServiceTest.TestUserServiceImpl;
import com.tobi.utils.DummyMailSender;

@ComponentScan( basePackages = {"com.tobi.domain", "com.tobi.service"})
@EnableTransactionManagement
@Configuration
@EnableSqlService
@PropertySource("/database.properties")
public class AppContext implements SqlMapConfig{
	

	@Autowired
	UserDao userDao;
	
	
	@Value("${db.driverClass}") Class<? extends Driver> driverClass;
	@Value("${db.url}") String url;
	@Value("${db.username}") String username;
	@Value("${db.password}") String password;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	
	@Bean
	public DataSource dataSource() {

		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		
		return dataSource;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource());
		
		return transactionManager;
	}
	
	
	
	@Configuration
	@Profile("production")
	public static class ProductionAppContext {

		@Bean
		public MailSender mailSender() {
			
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("mail.mycompany.com");
			return mailSender;
		}
	}
	
	@Configuration
	@Profile("test")
	public static class TestAppContext {
		
		@Bean
		public UserService testUserService() {
			return new TestUserServiceImpl();
		}
		
		@Bean
		public MailSender mailSender() {
			return new DummyMailSender();
		}
	}
	
	@Override
	public Resource getSqlMapResource() {
		// TODO Auto-generated method stub
		return new ClassPathResource("sqlmap.xml", UserDao.class);
	}

	
}