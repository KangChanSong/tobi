package com.tobi.test;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tobi.config.AppContext;
import com.tobi.domain.Level;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppContext.class)
@ActiveProfiles("test")
public class UserDaoTest {
	
	@Autowired
	private UserDao userDao;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		
		user1= new User("first", "첫째", "1234", Level.BASIC, 1 , 0, "first@nacer.com");
		user2= new User("second", "둘째", "12344", Level.SILVER, 55, 10, "second@naver.com");
		user3=  new User("third", "셋째", "123456", Level.GOLD, 100, 40, "third@naver.com");
		
	}
	
	@Test
	public void bean() {
		
		assertThat(userDao.getClass().getName(), is("com.tobi.domain.User"
				+ "DaoJdbc"));
	}

	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {
		
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));
		
		userDao.add(user1);
		userDao.add(user2);
		
		User userGet1 = userDao.get(user1.getId());
		checkSameUser(user1, userGet1);
		
		User userGet2 = userDao.get(user2.getId());
		checkSameUser(user2, userGet2);
	}
	
	@Test
	public void count() throws SQLException {
		
		
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));
		
		userDao.add(user1);
		assertThat(userDao.getCount(), is(1));
		
		userDao.add(user2);
		assertThat(userDao.getCount(), is(2));
		
		userDao.add(user3);
		assertThat(userDao.getCount(), is(3));
		
		
	}
	
	@Test(expected =EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException{
		
		
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));
		
		userDao.get("unknown_id");
	}
	
	@Test
	public void getAll() throws SQLException{
		
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));
		
		List<User> emptyLisy = userDao.getAll();
		assertThat(emptyLisy.size(), is(0));
		
		userDao.add(user1);
		List<User> userList1 = userDao.getAll();
		assertThat(userDao.getCount(), is(1));
		checkSameUser(user1, userList1.get(0));
		
		userDao.add(user2);
		List<User> userList2 = userDao.getAll();
		assertThat(userDao.getCount(), is(2));
		checkSameUser(user1, userList2.get(0));
		checkSameUser(user2, userList2.get(1));
		
		userDao.add(user3);
		List<User> userList3 = userDao.getAll();
		assertThat(userDao.getCount(), is(3));
		checkSameUser(user1, userList3.get(0));
		checkSameUser(user2, userList3.get(1));
		checkSameUser(user3, userList3.get(2));
		
	}
	
	public void checkSameUser(User user1, User user2) {
		
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
	}
	
	@Test(expected = DataAccessException.class)
	public void duplicateKeyExceptionTest() {
		
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		userDao.add(user1);
		userDao.add(user1);

	}
	
	@Test
	public void update() {
		
		userDao.deleteAll();
		
		userDao.add(user1);
		userDao.add(user2);
		
		user1.setName("new");
		user1.setPassword("1234");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		
		assertThat(userDao.update(user1), is(1));
		
		User user1Update = userDao.get(user1.getId());
		checkSameUser(user1, user1Update);
		
		User user2Same = userDao.get(user2.getId());
		checkSameUser(user2, user2Same);
	}
}
