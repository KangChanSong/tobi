package com.tobi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import com.tobi.domain.Level;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;
import com.tobi.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-application-context.xml")
public class UserServiceTest {
	
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	List<User> users;
	
	@Before
	public void setUp() {
		
		users = Arrays.asList(
				new User("aa", "AA", "p1", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER -1, 0),
				new User("bb", "BB", "p2", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("cc", "CC", "p3", Level.SILVER, 60, UserService.MIN_RECOMMEND_FOR_GOLD -1 ),
				new User("dd", "DD", "p4", Level.SILVER, 60, UserService.MIN_RECOMMEND_FOR_GOLD),
				new User("ee", "EE", "p5", Level.GOLD, 100, 100)
				);
	}
	
	@Test
	public void beans() {
		
		assertThat(userDao, is(notNullValue()));
		assertThat(userService, is(notNullValue()));
	}
	
	@Test
	public void upgradeLevels() throws Exception {
		userDao.deleteAll();
		for(User user: users) {userDao.add(user);}
		
		userService.upgradeLevels();
		
		checkLevel(users.get(0), false);
		checkLevel(users.get(1), true);
		checkLevel(users.get(2), false);
		checkLevel(users.get(3), true);
		checkLevel(users.get(4), false);
		
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
	
	static class TestUserService extends UserService{
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
		UserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setTransactionManager(transactionManager);
		
		userDao.deleteAll();
		for(User user: users) userDao.add(user);
			
		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceExecption Expected");
		} catch(TestUserServiceException e) {
			
		}
		
		checkLevel(users.get(1), false);
	}
}




