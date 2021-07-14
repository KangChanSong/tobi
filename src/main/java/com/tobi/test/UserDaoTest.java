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
		
		user1= new User("first", "첫째", "1234");
		user2= new User("second", "둘째", "12344");
		user3=  new User("third", "셋째", "123456");
		
	}
	
	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		dao.add(user2);
		
		User userGet1 = dao.get(user1.getId());
		assertThat(userGet1.getName(), is(user1.getName()));
		assertThat(userGet1.getPassword(), is(user1.getPassword()));
		
		User userGet2 = dao.get(user2.getId());
		assertThat(userGet2.getName(), is(user2.getName()));
		assertThat(userGet2.getPassword(), is(user2.getPassword()));
		
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
	}
	
	@Test(expected = DataAccessException.class)
	public void duplicateKeyExceptionTest() {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		try {
			dao.add(user1);
			dao.add(user1);
		} catch(DuplicateKeyException e) {
			SQLException sqlEx = (SQLException)e.getRootCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			
			assertThat(set.translate(null, null, sqlEx), is(DuplicateKeyException.class));
		}
	}
}
