package com.lwaiting.goods.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.jdbc.TxQueryRunner;

import com.lwaiting.goods.user.domain.User;

/**
 * 用户模块持久层
 * @author Administrator
 *
 */
/*
 * 属性哪里来
 * 1. t_user表：因为我们需要把t_user表查询出的数据封装到User对象中
 * 2. 该模块所有表单：因为我们需要把表单数据封装到User对象中
 */
public class UserDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 校验用户名是否注册
	 * @param loginname
	 * @return
	 * @throws SQLException 
	 */
	public boolean ajaxValidateLoginname(String loginname) throws SQLException {
		String sql = "select count(1) from t_user where loginname=?";
		Number number = (Number)qr.query(sql, new ScalarHandler(), loginname);
		if(number.intValue()==0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 校验Email是否注册
	 * @param email
	 * @return
	 * @throws SQLException 
	 */
	public boolean ajaxValidateEmail(String email) throws SQLException {
		String sql = "select count(1) from t_user where email=?";
		Number number = (Number)qr.query(sql, new ScalarHandler(), email);
		if(number.intValue()==0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 添加用户
	 * @param user
	 * @throws SQLException
	 */
	public void add(User user) throws SQLException {
		String sql = "insert into t_user values(?,?,?,?,?,?)";
		Object[] params = {user.getUid(), user.getLoginname(), user.getLoginpass(), user.getEmail(),
				user.isStatus(), user.getActivationCode()};
		qr.update(sql, params);
	}
	
	/**
	 * 通过验证码查询用户对象
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public User findByCode(String code) throws SQLException{
		String sql = "select * from t_user where activationCode=?";
		User user = qr.query(sql, new BeanHandler<User>(User.class), code);
		return user;
	}
	
	/**
	 * 修改用户状态
	 * @param uid
	 * @param status
	 * @throws SQLException
	 */
	public void updateStatus(String uid, boolean status) throws SQLException{
		String sql = "update t_user set status=? where uid=?";
		qr.update(sql, status, uid);
	}
	
	/**
	 * 通过用户名和密码查询用户
	 * @param loginname
	 * @param loginpass
	 * @return
	 * @throws SQLException
	 */
	public User findByLoginnameAndLoginpass(String loginname, String loginpass) throws SQLException{
		String sql = "select * from t_user where loginname=? and loginpass=?";
		User user = qr.query(sql, new BeanHandler<User>(User.class), loginname, loginpass);
		return user;
	}
	
	/**
	 * 通过uid和密码查询
	 * @param uid
	 * @param password
	 * @return
	 * @throws SQLException 
	 */
	public boolean findByUidAndPassword(String uid, String password) throws SQLException{
		boolean bool = false;
		String sql = "select count(*) from t_user where uid=? and loginpass=?";
		Number number = (Number) qr.query(sql, new ScalarHandler(), uid, password);
		if(number.intValue() > 0){
			bool = true;
		}
		return bool;
	}
	
	/**
	 * 修改密码
	 * @param uid
	 * @param password
	 * @throws SQLException 
	 */
	public void updatePassword(String uid, String password) throws SQLException{
		String sql = "update t_user set loginpass=? where uid=?";
		qr.update(sql, password, uid);
	}
}
