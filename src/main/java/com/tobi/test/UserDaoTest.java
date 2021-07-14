package com.tobi.test;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.tobi.domain.DaoFactory;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;

public class UserDaoTest {
	
	private UserDao dao;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		
		dao = new UserDao();
		dao.setDataSource(new SingleConnectionDataSource(
				"jdbc:mysql://localhost/testdb", "root", "1234", true	
				));
		
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
	
	private void userTest(UserDao dao) throws ClassNotFoundException, SQLException {
		
		dao.add(user1);
		
		User user2= dao.get(user1.getId());
		
		assertThat(user2.getName(), is(user1.getName()));
		assertThat(user2.getPassword(), is(user1.getPassword()));
		
	}
	
	private static void singleToneTest(ApplicationContext context) {
		UserDao conDaoA = context.getBean("userDao", UserDao.class);
		UserDao conDaoB = context.getBean("userDao", UserDao.class);
		
		DaoFactory factory = new DaoFactory();
		UserDao daoA = factory.userDao();
		UserDao daoB = factory.userDao();
		
		System.out.println(daoA);
		System.out.println(daoB);
		System.out.println(daoA == daoB);
		System.out.println(daoA.equals(daoB));
		
		System.out.println(conDaoA);
		System.out.println(conDaoB);
		System.out.println(conDaoA == conDaoB);
		System.out.println(conDaoA.equals(conDaoB));
	}
}
