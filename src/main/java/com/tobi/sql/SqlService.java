package com.tobi.sql;

public interface SqlService {

	String getSql(String key) throws SqlRetirevalFailureException;
}
