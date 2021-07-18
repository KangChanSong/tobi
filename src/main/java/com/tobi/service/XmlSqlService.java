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
import com.tobi.sql.SqlRetirevalFailureException;
import com.tobi.sql.SqlService;

public class XmlSqlService implements SqlService{
	
	private Map<String, String> sqlMap = new HashMap<>();
	
	private String sqlMapFile;
	
	public void setSqlMapFile(String sqlMapFile) {
		this.sqlMapFile = sqlMapFile;
	}
	
	@PostConstruct
	public void loadSql() {
		// TODO Auto-generated constructor stub
		String contextPath = SqlMap.class.getPackage().getName();
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			InputStream is = UserDao.class.getResourceAsStream(sqlMapFile);
			SqlMap sqlmap = (SqlMap) unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlmap.getSql()) {
				sqlMap.put(sql.getKey(), sql.getValue());
			}
			
		} catch(JAXBException e){
			
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getSql(String key) throws SqlRetirevalFailureException {
		// TODO Auto-generated method stub
		String sql = sqlMap.get(key);
		if(sql == null) {
			throw new SqlRetirevalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		} else return sql;
	}

}
