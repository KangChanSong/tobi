package com.tobi.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.mysql.cj.exceptions.MysqlErrorNumbers;
import com.tobi.exceptions.DuplicatedUserIdException;

public class UserDaoJdbc implements UserDao{
	
	private JdbcTemplate jdbcTemplate;
	
	public UserDaoJdbc() {
		// TODO Auto-generated constructor stub
	}
	
	public void setDataSource(DataSource dataSource) {
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private RowMapper<User> userMapper= new RowMapper<User>() {

		@Override
		public User mapRow(ResultSet rs, int arg1) throws SQLException {
			// TODO Auto-generated method stub
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			user.setEmail(rs.getString("email"));
			
			return user;
		}
	};

	@Override
	public void add( final User user) {
	
		this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend, email) values(?,?,?,?,?,?, ? )",
				user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
	
	}
	
	@Override
	public User get(String id) {
		
		return this.jdbcTemplate.queryForObject( "select * from users where id = ?", new Object[] {id}, this.userMapper);
	}
	
	@Override
	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}
	
	@Override
	public int getCount() {
	
		return this.jdbcTemplate.queryForInt( "select count(*) from users");
		
	}
	
	@Override
	public List<User> getAll(){
		
		return this.jdbcTemplate.query("select * from users", this.userMapper);
		}

	@Override
	public int update(User user) {
		// TODO Auto-generated method stub
		return this.jdbcTemplate.update("update users set name = ?, password = ?, level =? , login = ? , recommend = ?, email=? where id = ?"
				, user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
	}	

	
}

