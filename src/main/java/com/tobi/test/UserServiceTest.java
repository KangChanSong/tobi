package com.tobi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.catalina.core.ApplicationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import com.tobi.proxy.TransactionHandler;
import com.tobi.service.UserService;
import com.tobi.service.UserServiceImpl;
import com.tobi.service.UserServiceTx;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-application-context.xml")
public class UserServiceTest {
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private UserService userService;
	
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

		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		MockUserDao mockUserDao = new MockUserDao(users);
		userServiceImpl.setUserDao(mockUserDao);
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2) );
		assertThat(updated.get(0), is(users.get(1)));
		assertThat(updated.get(1), is(users.get(3)));
		
		List<String> request= mockMailSender.getRequest();
		assertThat(request.size(), is(2) );
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
		
		TransactionHandler txHandler =  new TransactionHandler();
		txHandler.setTransactionManager(transactionManager);
		txHandler.setTarget(testUserService);
		txHandler.setPattern("upgradeLevels");
		
		UserService txUserService = (UserService)Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] {UserService.class}
				, txHandler);
		
		userDao.deleteAll();
		for(User user: users) userDao.add(user);
			
		try {
			txUserService.upgradeLevels();
			fail("TestUserServiceExecption Expected");
		} catch(TestUserServiceException e) {
			
		}
		
		checkLevel(users.get(1), false);
	}
	
	static class MockUserDao implements UserDao{
		
		private List<User> users;
		private List<User> updated = new ArrayList<>();
		
		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated(){
			return this.updated;
		}
		@Override
		public List<User> getAll() {
			// TODO Auto-generated method stub
			return this.users;
		}
		
		@Override
		public int update(User user) {
			// TODO Auto-generated method stub
			this.updated.add(user);
			return 1;
		}

		@Override
		public void add(User user)  {throw new UnsupportedOperationException();}

		@Override
		public User get(String id)  {throw new UnsupportedOperationException();}

		@Override
		public void deleteAll()  {throw new UnsupportedOperationException();}

		@Override
		public int getCount() {throw new UnsupportedOperationException();}

		
		
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
	
	@Test
	public void mockUpgradeLevels() throws Exception{
		
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		//mock 오브젝트 생성
		UserDao mockUserDao = mock(UserDao.class);
		//getAll() 메서드를 호출하면 users 를 반환
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		//mock 오브젝트 생성
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		//update 메서드가 2번 호출됏나
		verify(mockUserDao, times(2)).update(any(User.class));
		//users.get(1) 을 파라미터로 받는 update 메서드가 호출됏나
		verify(mockUserDao).update(users.get(1));
		//level 확인
		assertThat(users.get(1).getLevel(), is(Level.SILVER) );
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = 
				ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
		
		
	}
	

}




