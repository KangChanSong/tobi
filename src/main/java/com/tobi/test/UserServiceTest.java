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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.tobi.config.TestApplicationContext;
import com.tobi.domain.Level;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;
import com.tobi.service.UserService;
import com.tobi.service.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestApplicationContext.class)
public class UserServiceTest {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserService testUserService;
	
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
	
	public static class TestUserServiceImpl extends UserServiceImpl{
		private String id = "dd";
		
		@Override
		protected void upgradeLevel(User user) {
			if(user.getId().equals(id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
		
		@Override
		public List<User> getAll() {
			// TODO Auto-generated method stub
			for(User user : super.getAll()) {
				
				super.update(user);
			}
			
			return null;
		}
	}
	
	@Test
	public void upgradeAllOrNothing() throws Exception {
		
		userDao.deleteAll();
		
		for(User user: users) userDao.add(user);
			
		try {
			testUserService.upgradeLevels();
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
	
	@Test(expected = TransientDataAccessResourceException.class)
	public void readOnlyTransactionAttribute() {
		testUserService.getAll();
	}
	
	@Test(expected = TransientDataAccessException.class)
	@Transactional(readOnly = true)
	public void transactionSync() {

		userService.deleteAll();

		
	}
	
	@Test
	public void rollbackTEst() {
		
		//트랜잭션 시작
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			userService.deleteAll();
			userService.add(users.get(1));
			userService.add(users.get(2));
			
		} finally {
			transactionManager.rollback(txStatus);
		}
	}
	
	
}




