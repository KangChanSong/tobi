package com.tobi.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementStrategy {
	
	public PreparedStatement makeConnection(Connection c) throws SQLException;

}
