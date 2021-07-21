package com.tobi.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hsqldb.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.cj.jdbc.Driver;
import com.tobi.domain.UserDao;
import com.tobi.domain.UserDaoJdbc;
import com.tobi.service.OxmSqlService;
import com.tobi.service.UserService;
import com.tobi.service.UserServiceImpl;
import com.tobi.sql.EmbeddedDbSqlRegistry;
import com.tobi.sql.SqlRegistry;
import com.tobi.sql.SqlService;
import com.tobi.test.UserServiceTest.TestUserServiceImpl;
import com.tobi.utils.DummyMailSender;

@EnableTransactionManagement
@Configuration
public class TestApplicationContext {
	
	@Bean
	public DataSource dataSource() {
		
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost/testdb");
		dataSource.setUsername("root");
		dataSource.setPassword("1234");
		
		return dataSource;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource());
		
		return transactionManager;
	}
	
	@Bean
	public UserDao userDao() {
		UserDaoJdbc userDao = new UserDaoJdbc();
		userDao.setDataSource(dataSource());
		userDao.setSqlService(sqlService());
		return userDao;
	}
	
	@Bean
	public UserService userService() {
		UserServiceImpl userService = new UserServiceImpl();
		userService.setUserDao(userDao());
		userService.setMailSender(mailSender());
		return userService;
	}
	
	@Bean
	public UserService testUserService() {
		TestUserServiceImpl testUserService = new TestUserServiceImpl();
		testUserService.setUserDao(userDao());
		testUserService.setMailSender(mailSender());
		return testUserService;
	}
	
	@Bean
	public MailSender mailSender() {
		return new DummyMailSender();
	}
	
	@Bean
	public SqlService sqlService() {
		OxmSqlService sqlService = new OxmSqlService();
		sqlService.setUnmarshaller(unmarshaller());
		sqlService.setSqlRegistry(sqlRegistry());
		return sqlService;
	}

	@Bean
	public SqlRegistry sqlRegistry() {
		EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
		sqlRegistry.setDataSource(embeddedDatabase());
		return sqlRegistry;
	}

	
	@Bean
	public Unmarshaller unmarshaller() {
		Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
		unmarshaller.setContextPath("com.tobi.domain.sql.jaxb");
		return unmarshaller;
	}
	
	@Bean
	public DataSource embeddedDatabase() {
		
		return new EmbeddedDatabaseBuilder()
				.setName("embeddedDatabase")
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("classpath:com/tobi/test/sqlRegistrySchema.sql")
				.build();
	}
}
