package com.tobi.factorybean;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.tobi.proxy.TransactionHandler;

public class TxProxyFactoryBean implements FactoryBean<Object>{
	
	Object target;
	PlatformTransactionManager transactionManager;
	String pattern;
	Class<?> serviceInterface;
	
	public void setTarget(Object target) {
		this.target = target;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	@Override
	public Object getObject() throws Exception {
		// 트랜잭션을 위한 InvocationHandler 생성
		TransactionHandler txHandler = new TransactionHandler();
		// target
		txHandler.setTarget(target);
		// PlatformTransactionManager의 구현체인 DataSourceTransasctionManager 를 DI 받아 주입시킴
		txHandler.setTransactionManager(transactionManager);
		// 메서드 이름
		txHandler.setPattern(pattern);
		
		// 1. 프록시 팩토리에 다이내믹 프록시 요청
		// 2. serviceInterface 에 따른 구현 다이내믹 프록시 생성
		// 3. txHandler 가 target 을 제어
		return Proxy.newProxyInstance(
				getClass().getClassLoader()
				, new Class[] {serviceInterface}
				, txHandler);
	}

	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		// 싱글톤 빈이 아니라는 뜻이 아님.
		// getObject() 가 매번 같은 오브젝트를 리턴하지 않는다는 뜻.
		return false;
	}

}
