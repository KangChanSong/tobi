package com.tobi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import com.tobi.domain.Level;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;
import com.tobi.service.UserService;
import com.tobi.service.UserServiceImpl;
import com.tobi.service.UserServiceTx;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-application-context.xml")
public class UserServiceTest {
	
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("aa", "AA", "p1", Level.BASIC, UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER -1, 0, "aa@n.com"),
				new User("bb", "BB", "p2", Level.BASIC, UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER, 0, "bb@n.com"),
				new User("cc", "CC", "p3", Level.SILVER, 60, UserServiceImpl.MIN_RECOMMEND_FOR_GOLD -1 ,"cc@n.com"),
				new User("dd", "DD", "p4", Level.SILVER, 60, UserServiceImpl.MIN_RECOMMEND_FOR_GOLD , "dd@n.com"),
				new User("ee", "EE", "p5", Level.GOLD, 100, 100, "ee@n.com")
				);
	}
	
	@Test
	public void beans() {
		
		assertThat(userDao, is(notNullValue()));
		assertThat(userService, is(notNullValue()));
	}
	
	@Test
	@DirtiesContext
	public void upgradeLevels() throws Exception {
		userDao.deleteAll();
		for(User user: users) {userDao.add(user);}
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		checkLevel(users.get(0), false);
		checkLevel(users.get(1), true);
		checkLevel(users.get(2), false);
		checkLevel(users.get(3), true);
		checkLevel(users.get(4), false);
		
		List<String> request= mockMailSender.getRequest();
		assertThat(request.size(), is(2) );
		System.out.println(request.toString());
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
		
	}
	
	@Test
	public void add() {
		
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
	
	private void checkLevel(User user, boolean upgraded) {
		
		User userUpgraded = userDao.get(user.getId());
		if(upgraded) {
			assertThat(userUpgraded.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpgraded.getLevel(), is(user.getLevel()));
		}
	}
	

	static class TestUserServiceException  extends RuntimeException{}
	
	static class TestUserService extends UserServiceImpl{
		private String id;
		
		private TestUserService(String id) {
			this.id = id;
		}
		
		@Override
		protected void upgradeLevel(User user) {
			if(user.getId().equals(id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	@Test
	public void upgradeAllOrNothing() throws Exception {
		UserServiceImpl testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setMailSender(mailSender);
		
		//직점 오브젝트를 생성하는 것이기 때문에 수동으로 DI 해줘야함
		UserServiceTx txUserService =  new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);
		
		userDao.deleteAll();
		for(User user: users) userDao.add(user);
			
		try {
			txUserService.upgradeLevels();
			fail("TestUserServiceExecption Expected");
		} catch(TestUserServiceException e) {
			
		}
		
		checkLevel(users.get(1), false);
	}
	
	static class MockMailSender implements MailSender{
		
		private List<String> request = new ArrayList<>();
		
		public List<String> getRequest() {
			return request;
		}
		
		@Override
		public void send(SimpleMailMessage message) throws MailException {
			
			request.add(message.getTo()[0]);
		}

		@Override
		public void send(SimpleMailMessage[] message) throws MailException {
		}
		
	}

}




