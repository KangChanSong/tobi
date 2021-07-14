package com.tobi.test;


import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.sql.SQLException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.tobi.domain.DaoFactory;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;

public class UserDaoTest {

	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {
		
	 	ApplicationContext context = new GenericXmlApplicationContext("application-context.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);

		dao.deleteAll();
		assertThat(dao.getCount(), is(0) );
		userTest(dao);
		
		assertThat(dao.getCount(), is(1));
		
		
	}
	
	@Test
	public void count() throws SQLException {
		
		ApplicationContext context = new GenericXmlApplicationContext("application-context.xml");
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		User user1 = new User("a", "에이", "1234");
		User user2 = new User("b", "비", "1234");
		User user3 = new User("c" , "씨", "1234");
		
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
		
		
	}
	
	private void userTest(UserDao dao) throws ClassNotFoundException, SQLException {
		User user = new User();
		user.setId("whiteshp");
		user.setName("백기선");
		user.setPassword("married");
		
		dao.add(user);
		
		User user2= dao.get(user.getId());
		
		assertThat(user2.getName(), is(user.getName()));
		assertThat(user2.getPassword(), is(user.getPassword()));
		
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
