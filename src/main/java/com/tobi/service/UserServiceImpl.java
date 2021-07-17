package com.tobi.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.tobi.domain.Level;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;

public class UserServiceImpl implements UserService{
	
	UserDao userDao;
	MailSender mailSender;
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	public void setMailSender(MailSender mailSender2) {
		this.mailSender = mailSender2;
	}

	@Override
	public void add(User user) {
		// TODO Auto-generated method stub
		if(user.getLevel() == null) {user.setLevel(Level.BASIC);}
		
		userDao.add(user);
	}
	
	@Override
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for(User user: users) {
			if(canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
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
	@Override
	public User get(String id) {
		// TODO Auto-generated method stub
		return userDao.get(id);
	}
	@Override
	public List<User> getAll() {
		// TODO Auto-generated method stub
		return userDao.getAll();
	}
	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		userDao.deleteAll();
	}
	@Override
	public void update(User user) {
		// TODO Auto-generated method stub
		userDao.update(user);
	}
	

}
