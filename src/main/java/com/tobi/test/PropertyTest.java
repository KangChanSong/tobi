package com.tobi.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;

@PropertySource("/database.properties")
public class PropertyTest {
	
	@Autowired
	private ApplicationContext context;
	
	@Test
	public void getProperties() {
		
		Class driverClass=  context.getBean("driverClass", Class.class);
		
		System.out.println(driverClass.getClass().getName());
	}
}
