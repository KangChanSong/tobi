package com.tobi.service;

import com.tobi.domain.User;

public interface UserLevelUpgradePolicy {
	
	public boolean canUpgradeLevel(User user);
	public void upgradeLevel(User user);
}
