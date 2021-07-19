package com.tobi.sql;

public class SqlNotFoundException extends RuntimeException{
	
	public SqlNotFoundException() {
		// TODO Auto-generated constructor stub
	}
	
	public SqlNotFoundException(String msg) {
		super(msg);
	}
	
	public SqlNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
