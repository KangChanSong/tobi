package com.tobi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.tobi.sql.SqlNotFoundException;
import com.tobi.sql.SqlUpdateFailureException;
import com.tobi.sql.UpdatableSqlRegistry;

public abstract class AbstractUpdatableSqlRegistryTest {

	UpdatableSqlRegistry sqlRegistry;
	
	@Before
	public void setUp() {
		sqlRegistry = createUpdatableSqlRegistry();
		sqlRegistry.registerSql("KEY1", "SQL1");
		sqlRegistry.registerSql("KEY2", "SQL2");
		sqlRegistry.registerSql("KEY3", "SQL3");
	}

	abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry();
	
	@Test
	public void find() {
		checkFind("SQL1", "SQL2", "SQL3");
	}
	
	@Test(expected = SqlNotFoundException.class)
	public void unkownKey() {
		sqlRegistry.findSql("@@@@DSKAL!!");
	}
	
	@Test
	public void updateSingle() {
		sqlRegistry.updateSql("KEY2", "Modified2");
		checkFind("SQL1", "Modified2", "SQL3");
	}
	
	@Test
	public void updateMulti() {
		Map<String, String> sqlmap = new HashMap<>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY3", "Modified3");
		
		sqlRegistry.updateSql(sqlmap);
		checkFind("Modified1", "SQL2", "Modified3");
	}
	
	@Test(expected = SqlUpdateFailureException.class)
	public void updateWithNotExistingKey() {
		sqlRegistry.updateSql("SQL@##EFF", "Modified2");
	}
	
	protected void checkFind(String exp1 , String exp2, String exp3) {
		
		assertThat(sqlRegistry.findSql("KEY1"), is(exp1));
		assertThat(sqlRegistry.findSql("KEY2"), is(exp2));
		assertThat(sqlRegistry.findSql("KEY3"), is(exp3));
	}
	
	
	
}
