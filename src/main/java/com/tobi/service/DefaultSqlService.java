package com.tobi.service;

import com.tobi.sql.HashMapSqlRegistry;
import com.tobi.sql.JaxbXmlSqlReader;



public class DefaultSqlService extends BaseSqlService{

	public DefaultSqlService() {
		setSqlRegistry(new HashMapSqlRegistry());
		setSqlReader(new JaxbXmlSqlReader());
	}
	


}
