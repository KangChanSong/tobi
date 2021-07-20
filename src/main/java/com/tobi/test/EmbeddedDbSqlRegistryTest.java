package com.tobi.test;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.tobi.sql.EmbeddedDbSqlRegistry;
import com.tobi.sql.SqlUpdateFailureException;
import com.tobi.sql.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{

	EmbeddedDatabase db;
	
	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {

		db = new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("classpath:com/tobi/test/sqlRegistrySchema.sql")
				.build();
		
		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);
		
		return embeddedDbSqlRegistry;
	}
	
	@Test
	public void transactionalUpdate() {
		checkFind("SQL1", "SQL2", "SQL3");
		
		Map<String, String> sqlMap = new HashMap<>();
		sqlMap.put("KEY1", "Modified1");
		sqlMap.put("KEY0000@@!#", "Modified!@#!$");
		
		try {
			sqlRegistry.updateSql(sqlMap);
			fail();
		}catch(SqlUpdateFailureException e) {}
		
		checkFind("SQL1", "SQL2", "SQL3");
	}
	
	@After
	public void tearDown() {
		db.shutdown();
	}

	
}
