package com.tobi.test;


import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tobi.domain.Level;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-application-context.xml")
public class UserDaoTest {
	
	@Autowired
	private UserDao dao;
	
	@Autowired
	private DataSource dataSource;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		
		user1= new User("first", "첫째", "1234", Level.BASIC, 1 , 0);
		user2= new User("second", "둘째", "12344", Level.SILVER, 55, 10);
		user3=  new User("third", "셋째", "123456", Level.GOLD, 100, 40);
		
	}
	
	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		dao.add(user2);
		
		User userGet1 = dao.get(user1.getId());
		checkSameUser(user1, userGet1);
		
		User userGet2 = dao.get(user2.getId());
		checkSameUser(user2, userGet2);
	}
	
	@Test
	public void count() throws SQLException {
		
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
		
		
	}
	
	@Test(expected =EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException{
		
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");
	}
	
	@Test
	public void getAll() throws SQLException{
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		List<User> emptyLisy = dao.getAll();
		assertThat(emptyLisy.size(), is(0));
		
		dao.add(user1);
		List<User> userList1 = dao.getAll();
		assertThat(dao.getCount(), is(1));
		checkSameUser(user1, userList1.get(0));
		
		dao.add(user2);
		List<User> userList2 = dao.getAll();
		assertThat(dao.getCount(), is(2));
		checkSameUser(user1, userList2.get(0));
		checkSameUser(user2, userList2.get(1));
		
		dao.add(user3);
		List<User> userList3 = dao.getAll();
		assertThat(dao.getCount(), is(3));
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
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		dao.add(user1);
		dao.add(user1);

	}
	
	@Test
	public void update() {
		
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user2);
		
		user1.setName("new");
		user1.setPassword("1234");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		
		assertThat(dao.update(user1), is(1));
		
		User user1Update = dao.get(user1.getId());
		checkSameUser(user1, user1Update);
		
		User user2Same = dao.get(user2.getId());
		checkSameUser(user2, user2Same);
	}
}
