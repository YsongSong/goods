package com.lwaiting.goods.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

import com.lwaiting.goods.user.dao.UserDao;
import com.lwaiting.goods.user.domain.User;
import com.lwaiting.goods.user.servlet.exception.UserException;

/**
 * 用户模块业务层
 * @author Administrator
 *
 */
public class UserService {
	private UserDao userDao = new UserDao();
	
	/**
	 * 用户名注册校验
	 * @param loginname
	 * @return
	 */
	public boolean ajaxValidateLoginname(String loginname) {
		try {
			return userDao.ajaxValidateLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Email校验
	 * @param email
	 * @return
	 */
	public boolean ajaxValidateEmail(String email) {
		try {
			return userDao.ajaxValidateEmail(email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 注册功能
	 * @param user
	 */
	public void regist(User user) {
		/*
		 * 1. 数据的补齐
		 */
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid() + CommonUtils.uuid());
		/*
		 * 2. 向数据库插入
		 */
		try {
			userDao.add(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		/*
		 * 3. 发邮件
		 */
		
		// a. 首先把配置文件加载到prop中
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// b. 登录邮件服务器，的到session
		String host = prop.getProperty("host");
		String name = prop.getProperty("username");
		String pass = prop.getProperty("password");
		Session session = MailUtils.createSession(host, name, pass);
		// c. 创建Mail对象
		String from = prop.getProperty("from");//发件人
		String to = user.getEmail();//收件人  注册的用户邮箱
		String subject = prop.getProperty("subject");//主题
		// MessageForm.format方法会把第一个参数中的{0},使用第二个参数来替换。
		// 例如MessageFormat.format("你好{0}, 你{1}!", "张三", "去死吧"); 返回“你好张三，你去死吧！”
		String content = MessageFormat.format(prop.getProperty("content"), user.getActivationCode());
		Mail mail = new Mail(from, to, subject, content);
		// d. 发邮件
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 激活功能
	 * @param code
	 * @throws UserException 
	 */
	public void activation(String code) throws UserException{
		/*
		 * 1. 通过激活码查询用户
		 * 2. 如果User为null，说明是无效激活码，抛出异常，给出异常信息（无效激活码）
		 * 3. 查看用户状态是否为true，如果为true，抛出异常，给出异常信息（请不要二次激活）
		 * 4. 修改用户状态为true
		 */
		try{
			User user = userDao.findByCode(code);
			if(user == null){
				throw new UserException("无效的激活码");
			}
			if(user.isStatus()){
				throw new UserException("您已经激活过了，无需再次激活！");
			}
			userDao.updateStatus(user.getUid(), true);
		}catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 登录功能
	 * @param user
	 * @return
	 */
	public User login(User user) {
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * 修改密码
	 * @param uid
	 * @param password
	 * @param newloginpass
	 * @throws UserException 
	 */
	public void updatePassword(String uid, String password, String newloginpass) throws UserException{
		/*
		 * 1. 校验老密码，使用uid和old去访问dao，的到结果
		 * 		* 如果校验失败，抛出异常
		 * 2. 校验通过，使用uid和newpassword来访问dao，完成修改密码
		 */
		try {
			boolean bool = userDao.findByUidAndPassword(uid, password);
			if(!bool){
				throw new UserException("旧密码错误!");
			}
			userDao.updatePassword(uid, newloginpass);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
	
}
