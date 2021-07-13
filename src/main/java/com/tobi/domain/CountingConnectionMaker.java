package com.tobi.domain;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker {
	
	private ConnectionMaker realConnectionMaker;
	int counter = 0;
	
	public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
		this.realConnectionMaker = realConnectionMaker;
	}
	
	public int getCounter() {
		return counter;
	}

	@Override
	public Connection makeConnection() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		counter++;
		return realConnectionMaker.makeConnection();
	}

}
