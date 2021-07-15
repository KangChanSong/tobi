package com.tobi.proxy;

public class HelloUppercase implements Hello{
	
	Hello hello;
	
	public HelloUppercase(Hello hello) {
		this.hello = hello;
	}

	@Override
	public String sayHello(String name) {
		// TODO Auto-generated method stub
		return this.hello.sayHello(name).toUpperCase();
	}

	@Override
	public String sayHi(String name) {
		// TODO Auto-generated method stub
		return this.hello.sayHi(name).toUpperCase();
	}

	@Override
	public String sayThankYou(String name) {
		// TODO Auto-generated method stub
		return this.hello.sayThankYou(name).toUpperCase();
	}

}
