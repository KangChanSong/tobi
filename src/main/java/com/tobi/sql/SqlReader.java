package com.tobi.sql;

public interface SqlReader {
	//SQL 을 외부에서 가져와 SqlRegistry에 등록. 
	void read(SqlRegistry sqlRegistry);
}
