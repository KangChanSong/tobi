package com.tobi.service;

import javax.annotation.PostConstruct;

import com.tobi.sql.SqlNotFoundException;
import com.tobi.sql.SqlReader;
import com.tobi.sql.SqlRegistry;
import com.tobi.sql.SqlRetirevalFailureException;
import com.tobi.sql.SqlService;

public class BaseSqlService implements SqlService{
	protected SqlReader sqlReader;
	protected SqlRegistry sqlRegistry;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	@PostConstruct
	public void loadSql() {
		this.sqlReader.read(this.sqlRegistry);
	}

	@Override
	public String getSql(String key) throws SqlRetirevalFailureException {
		// TODO Auto-generated method stub
		 try { return this.sqlRegistry.findSql(key);}
		 catch(SqlNotFoundException e) { throw new SqlRetirevalFailureException(e);}
	}

}
