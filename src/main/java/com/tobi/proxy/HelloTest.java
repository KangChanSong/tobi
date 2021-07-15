package com.tobi.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;

public class HelloTest {
	
	public static final String NAME = "Toby";
	public static final String HELLO = "Hello " + NAME;
	public static final String HI = "Hi " + NAME;
	public static final String THANKYOU = "Thank You " + NAME;
	
	Hello proxiedHello = (Hello)Proxy.newProxyInstance(
			//동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
			getClass().getClassLoader(), 
			// 구현할 인터페이스
			new Class[] {Hello.class},
			// 부가기능과 위임코드를 담은 InvocationHandler
			new UppercaseHandler(new HelloTarget()));

	@Test
	public void hello() {
		
		Hello hello = new HelloTarget();
		
		assertThat(hello.sayHello(NAME), is(HELLO));
		assertThat(hello.sayHi(NAME), is(HI));
		assertThat(hello.sayThankYou(NAME), is(THANKYOU));
	}
	
	@Test
	public void helloUppercaseTest() {
		
		Hello hello = proxiedHello;
		
		assertThat(hello.sayHello(NAME), is(HELLO.toUpperCase()));
		assertThat(hello.sayHi(NAME), is(HI.toUpperCase()));
		assertThat(hello.sayThankYou(NAME), is(THANKYOU.toUpperCase()));
	}
}
