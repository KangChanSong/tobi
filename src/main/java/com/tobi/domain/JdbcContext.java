package com.tobi.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class JdbcContext {
	
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException{
		
		Connection c = null;
		PreparedStatement ps = null;
		
		try {
			c = dataSource.getConnection();
			ps = stmt.makeConnection(c);
		
			ps.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}	
			}
			if(c != null) {
				try {
					c.close();
				} catch (SQLException e) {
				}
			}
			
		}
	}

	public void executeSql(final String query) throws SQLException{
		this.workWithStatementStrategy(new StatementStrategy() {
			
			@Override
			public PreparedStatement makeConnection(Connection c) throws SQLException {
				// TODO Auto-generated method stub
				PreparedStatement ps;
				ps = c.prepareStatement(query);
				return ps;
			}
		});
	}
}
