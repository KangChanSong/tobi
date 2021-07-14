package com.tobi.domain;

import java.sql.Connection;

import com.mysql.cj.jdbc.Driver;
import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

public class UserDao {
	
	private DataSource dataSource;
	
	private JdbcContext jdbcContext;
	
	public UserDao() {
		// TODO Auto-generated constructor stub
	}
	
	public void setDataSource(DataSource dataSource) {
		
		this.jdbcContext = new JdbcContext();
		this.jdbcContext.setDataSource(dataSource);
		
		this.dataSource = dataSource;
	}

	public void add( final User user) throws SQLException {
		
		this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
			
			@Override
			public PreparedStatement makeConnection(Connection c) throws SQLException {
				// TODO Auto-generated method stub
				PreparedStatement ps;
				ps = c.prepareStatement("insert into users(id, name,password) values(?,?,?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());

				return ps;
			}
		});
	}
	
	public User get(String id) throws SQLException{
		
		Connection c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement(
				"select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		User user = null;
		if(rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}
		
		if (user == null) throw new EmptyResultDataAccessException(1);
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
	}
	
	public void deleteAll() throws SQLException{
		this.jdbcContext.executeSql("delete from users");
	}
	

	
	public int getCount() throws SQLException{
	
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement("select count(*) from users");
			
			rs = ps.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			
			return count;
		} catch (SQLException e) {
			throw e;
		} finally {
		
			if(rs != null) {
				try {
					rs.close();
				} catch(SQLException e) {
				}
			}
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
}
