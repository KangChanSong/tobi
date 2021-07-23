package com.tobi.service;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import com.tobi.domain.UserDao;
import com.tobi.domain.sql.jaxb.SqlMap;
import com.tobi.domain.sql.jaxb.SqlType;
import com.tobi.sql.HashMapSqlRegistry;
import com.tobi.sql.SqlReader;
import com.tobi.sql.SqlRegistry;
import com.tobi.sql.SqlRetirevalFailureException;
import com.tobi.sql.SqlService;

public class OxmSqlService implements SqlService{
	
	private final BaseSqlService baseSqlService = new BaseSqlService();

	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}
	
	public void setSqlMapFile(Resource sqlMapFile) {
		this.oxmSqlReader.setSqlMapFile(sqlMapFile);
	}
	
	@Override
	public String getSql(String key) throws SqlRetirevalFailureException {
		// TODO Auto-generated method stub
		return this.baseSqlService.getSql(key);
	}
	
	@PostConstruct
	public void loadSql() {
		this.baseSqlService.setSqlReader(oxmSqlReader);
		this.baseSqlService.setSqlRegistry(sqlRegistry);
		
		this.baseSqlService.loadSql();
	}
	
	private class OxmSqlReader implements SqlReader{
		
		private Unmarshaller unmarshaller;
		private Resource sqlMapFile = new ClassPathResource("/sqlmap.xml");
		
		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}
		
		public void setSqlMapFile(Resource sqlMapFile) {
			this.sqlMapFile = sqlMapFile;
		}

		@Override
		public void read(SqlRegistry sqlRegistry) {
			
			try {
				Source source = new StreamSource(sqlMapFile.getInputStream());
				
				SqlMap sqlMap = (SqlMap) this.unmarshaller.unmarshal(source);
				
				for(SqlType sql : sqlMap.getSql()) {
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
				
			}catch(IOException e) {
				throw new IllegalArgumentException(this.sqlMapFile.getFilename()
						+ "을 가져올 수 없습니다");
			}
		}
		
	}

}
