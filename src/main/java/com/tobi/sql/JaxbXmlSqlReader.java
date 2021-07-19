package com.tobi.sql;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.tobi.domain.UserDao;
import com.tobi.domain.sql.jaxb.SqlMap;
import com.tobi.domain.sql.jaxb.SqlType;

public class JaxbXmlSqlReader implements SqlReader{
	
	private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
	
	private String sqlMapFile = DEFAULT_SQLMAP_FILE;
	
	public void setSqlMapFile(String sqlMapFile) {
		this.sqlMapFile = sqlMapFile;
	}
	
	@Override
	public void read(SqlRegistry sqlRegistry) {
		// TODO Auto-generated method stub
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
