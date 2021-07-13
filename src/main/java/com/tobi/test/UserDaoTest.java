package com.tobi.test;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.tobi.domain.CountingConnectionMaker;
import com.tobi.domain.DaoFactory;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;

public class UserDaoTest {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
	 	ApplicationContext context = new GenericXmlApplicationContext("application-context.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		CountingConnectionMaker connectionMaker = context.getBean("connectionMaker", CountingConnectionMaker.class);

		userTest(dao);
		
		System.out.println("Connction Count -> " + connectionMaker.getCounter());
		
	}
	
	private static void userTest(UserDao dao) throws ClassNotFoundException, SQLException {
		User user = new User();
		user.setId("whiteshp");
		user.setName("백기선");
		user.setPassword("married");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		
		User user2= dao.get(user.getId());
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		
		System.out.println(user2.getId() + " 조회 성공");
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
