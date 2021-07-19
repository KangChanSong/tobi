package com.tobi.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.tobi.domain.UserDao;
import com.tobi.domain.sql.jaxb.SqlMap;
import com.tobi.domain.sql.jaxb.SqlType;
import com.tobi.sql.SqlNotFoundException;
import com.tobi.sql.SqlReader;
import com.tobi.sql.SqlRegistry;
import com.tobi.sql.SqlRetirevalFailureException;
import com.tobi.sql.SqlService;

public class XmlSqlService implements SqlService, SqlRegistry, SqlReader{
	
	private Map<String, String> sqlMap = new HashMap<>();
	
	private String sqlMapFile;
	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;
	
	public void setSqlMapFile(String sqlMapFile) {
		this.sqlMapFile = sqlMapFile;
	}
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	//SqlService
	@PostConstruct
	public void loadSql() {
		// TODO Auto-generated constructor stub
		this.sqlReader.read(this.sqlRegistry);
	}
	@Override
	public String getSql(String key) throws SqlRetirevalFailureException {
		// TODO Auto-generated method stub
		try {
			return this.sqlRegistry.findSql(key);
			
		}catch(SqlNotFoundException e) {
			throw new SqlRetirevalFailureException(e);
		}
	}
	
	//SqlRegistry
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
		
	}
	@Override
	public String findSql(String key) throws SqlNotFoundException {
		// TODO Auto-generated method stub
		String sql = sqlMap.get(key);
		if( sql == null) throw new SqlNotFoundException(key + "에 대한 SQL을 찾을 수 없습니다.");
		else return sql;
	}
	//SqlReader
	
	@Override
	public void read(SqlRegistry sqlRegistry) {
		String contextPath = SqlMap.class.getPackage().getName();
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			InputStream is = UserDao.class.getResourceAsStream(sqlMapFile);
			SqlMap sqlmap = (SqlMap) unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlmap.getSql()) {
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
			
		} catch(JAXBException e){
			
			throw new RuntimeException(e);
		}
		
	}


	

	
	
}
