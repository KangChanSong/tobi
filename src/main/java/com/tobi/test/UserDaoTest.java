package com.tobi.test;

import java.sql.SQLException;

import com.tobi.domain.SimpleConnectionMaker;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;

public class UserDaoTest {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		UserDao dao = new UserDao(new SimpleConnectionMaker());
		
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
}
