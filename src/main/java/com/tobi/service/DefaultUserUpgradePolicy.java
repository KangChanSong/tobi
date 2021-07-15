package com.tobi.service;

import com.tobi.domain.Level;
import com.tobi.domain.User;
import com.tobi.domain.UserDao;

public class DefaultUserUpgradePolicy implements UserLevelUpgradePolicy{

	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;
	
	UserDao userDao;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Override
	public boolean canUpgradeLevel(User user) {
		// TODO Auto-generated method stub
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
			case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
			case GOLD: return false;
			default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}

	@Override
	public void upgradeLevel(User user) {
		// TODO Auto-generated method stub
		user.upgradeLevel();
		userDao.update(user);
	}
}

