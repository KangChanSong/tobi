package com.tobi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JUnitTest {

	
	private static JUnitTest testObject;
	
	@Test
	public void 오브젝트가_매번_새로_생성될까(){
		
		assertThat(this, is(not(sameInstance(testObject))));
		testObject = this;
	}
	
	@Test
	public void 오브젝트가_매번_새로_생성될까1(){
		assertThat(this, is(not(sameInstance(testObject))));
		testObject = this;
		
	}
	
	@Test
	public void 오브젝트가_매번_새로_생성될까2(){
		
		assertThat(this, is(not(sameInstance(testObject))));
		testObject = this;
	}
}
