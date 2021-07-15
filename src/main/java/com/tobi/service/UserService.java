package com.tobi.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.tobi.domain.Level;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;

public class UserService {
	
	UserDao userDao;
	
	PlatformTransactionManager transactionManager;
	
	MailSender mailSender;
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	public void setMailSender(MailSender mailSender2) {
		this.mailSender = mailSender2;
	}

	public void add(User user) {
		// TODO Auto-generated method stub
		if(user.getLevel() == null) {user.setLevel(Level.BASIC);}
		
		userDao.add(user);
	}
	

	public void upgradeLevels() throws Exception{
		
		//												 얘는 트랜잭션의 속성을 갖고잇다.
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			List<User> users = userDao.getAll();
			for(User user: users) {
				if(canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			transactionManager.commit(status);
		} catch(Exception e) {
			transactionManager.rollback(status);
			throw e;
		} 
	}
	
	private boolean canUpgradeLevel(User user) {
		
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
			case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
			case GOLD: return false;
			default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
	
	protected void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
		sendUpgradeEmail(user);
	}
	
	private void sendUpgradeEmail(User user) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "으로 변경되었습니다");
		
		this.mailSender.send(mailMessage);
	}
	

}
