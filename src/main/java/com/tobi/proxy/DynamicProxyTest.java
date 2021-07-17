package com.tobi.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;


public class DynamicProxyTest {
	
	public static final String NAME = "Toby";
	public static final String HELLO = "Hello " + NAME;
	public static final String HI = "Hi " + NAME;
	public static final String THANKYOU = "Thank You " + NAME;
	
	//jdk 다이내믹 프록시 생성
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
		
		
	}
	
	@Test
	public void helloUppercaseTest() {
		
		Hello hello = proxiedHello;
		
		assertThat(hello.sayHello(NAME), is(HELLO.toUpperCase()));
		assertThat(hello.sayHi(NAME), is(HI.toUpperCase()));
		assertThat(hello.sayThankYou(NAME), is(THANKYOU.toUpperCase()));
	}
	
	@Test
	public void proxyFactoryBean() {
		
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		pfBean.addAdvice(new LowercaseAdvice());
		
		Hello proxiedHello = (Hello)pfBean.getObject();
		
		assertThat(proxiedHello.sayHello(NAME), is(HELLO.toLowerCase()));
		assertThat(proxiedHello.sayHi(NAME), is(HI.toLowerCase()));
		assertThat(proxiedHello.sayThankYou(NAME), is(THANKYOU.toLowerCase()));
		
	}
	
	static class LowercaseAdvice implements MethodInterceptor{

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			// TODO Auto-generated method stub
			//타깃의 메서드를 내부적으로 실행해줌
			String ret = (String)invocation.proceed();
			
			return ret.toLowerCase();
		}

	}
	
	@Test
	public void pointCutAdvisor() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		//타깃 세팅
		pfBean.setTarget(new HelloTarget());
		
		//메서드 알고리즘 
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new LowercaseAdvice()));
		
		Hello proxiedHello = (Hello)pfBean.getObject();
		
		assertThat(proxiedHello.sayHello(NAME), is(HELLO.toLowerCase()));
		assertThat(proxiedHello.sayHi(NAME), is(HI.toLowerCase()));
		assertThat(proxiedHello.sayThankYou(NAME), is(THANKYOU));
		
	}
	
	@Test
	public void classNamePointcutAdvisor() {
		
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			
			@Override
			public ClassFilter getClassFilter() {
				// TODO Auto-generated method stub
				return new ClassFilter() {
					
					@Override
					public boolean matches(Class<?> clazz) {
						// TODO Auto-generated method stub
						return clazz.getSimpleName().startsWith("HelloT");
					}
				};
			}
		};
		
		classMethodPointcut.setMappedName("sayH*");
		
		checkAdviced(new HelloTarget(), classMethodPointcut, true);
		
		class HelloWorld extends HelloTarget{}
		checkAdviced(new HelloWorld(), classMethodPointcut, false);
		
		class HelloToby extends HelloTarget{}
		checkAdviced(new HelloToby(), classMethodPointcut, true);
	}
	
	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
			
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new LowercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		if(adviced) {
			assertThat(proxiedHello.sayHello(NAME), is(HELLO.toLowerCase()));
			assertThat(proxiedHello.sayHi(NAME), is(HI.toLowerCase()));
			assertThat(proxiedHello.sayThankYou(NAME), is(THANKYOU));
		} else {
			assertThat(proxiedHello.sayHello(NAME), is(HELLO));
			assertThat(proxiedHello.sayHi(NAME), is(HI));
			assertThat(proxiedHello.sayThankYou(NAME), is(THANKYOU));
		}
	}
	
	private void checkLowerWithoutTHANKYOU(Hello hello) {
		
	}
	
	private void checkNormal(Hello hello) {
		
	}
}
