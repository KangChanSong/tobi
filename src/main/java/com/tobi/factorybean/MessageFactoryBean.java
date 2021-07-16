package com.tobi.factorybean;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message>{
	
	String text;
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public Message getObject() throws Exception {
		// TODO Auto-generated method stub
		return Message.newMessage(text);
	}

	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		return Message.class;
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

}
