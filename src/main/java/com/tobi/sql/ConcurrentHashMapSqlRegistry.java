package com.tobi.sql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {

private Map<String, String> sqlMap = new ConcurrentHashMap<>();
	
	@Override
	public void registerSql(String key, String sql) {
		// TODO Auto-generated method stub
		sqlMap.put(key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		// TODO Auto-generated method stub
		String sql = this.sqlMap.get(key);
		if(sql == null) {
			throw new SqlNotFoundException(key + " 를 이용해서 SQL을 찾을 수 없습니다.");
		} else {
			return sql;
		}
	}

	@Override
	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		
		if(sqlMap.get(key) == null) {
			throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다");
		}
		
		sqlMap.put(key, sql);
		
	}

	@Override
	public void updateSql(Map<String, String> sqlMap) throws SqlUpdateFailureException {
		// TODO Auto-generated method stub
		for(Map.Entry<String, String> entry : sqlMap.entrySet()) {
			updateSql(entry.getKey(), entry.getValue());
		}
		
	}


}
