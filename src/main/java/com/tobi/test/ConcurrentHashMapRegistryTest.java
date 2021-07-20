package com.tobi.test;

import com.tobi.sql.ConcurrentHashMapSqlRegistry;
import com.tobi.sql.UpdatableSqlRegistry;

public class ConcurrentHashMapRegistryTest extends AbstractUpdatableSqlRegistryTest{

	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		// TODO Auto-generated method stub
		return new ConcurrentHashMapSqlRegistry();
	}
	
}
