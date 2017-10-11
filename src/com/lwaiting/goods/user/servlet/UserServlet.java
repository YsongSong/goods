package com.lwaiting.goods.user.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

import com.lwaiting.goods.user.domain.User;
import com.lwaiting.goods.user.service.UserService;
import com.lwaiting.goods.user.servlet.exception.UserException;

/**
 * 用户模块控制层(Web层)
 * @author Administrator
 *
 */
public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();
	
	/**
	 * ajax用户名是否注册校验
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateLoginname(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 获取用户名
		 */
		String loginname = request.getParameter("loginname");
		/*
		 * 2. 通过service得到校验结果
		 */
		boolean b = userService.ajaxValidateLoginname(loginname);
		/*
		 * 3. 发给客户端
		 */
		response.getWriter().print(b);
		return null;
	}
	
	/**
	 * ajax Email是否注册校验
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateEmail(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 获取Email
		 */
		String email = request.getParameter("email");
		/*
		 * 2. 通过service得到校验结果
		 */
		boolean b = userService.ajaxValidateEmail(email);
		/*
		 * 3. 发给客户端
		 */
		response.getWriter().print(b);
		return null;
	}
	
	/**
	 * ajax 验证码是否正确校验
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateVerifyCode(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 获取输入的验证码
		 */
		String verifyCode = request.getParameter("verifyCode");
		/*
		 * 2. 获取图片上真是的验证码(存在session中)
		 */
		String vcode = (String) request.getSession().getAttribute("vCode");
		/*
		 * 3. 进行忽略大小写的比较，得到结果
		 */
		boolean b = vcode.equalsIgnoreCase(verifyCode);
		/*
		 * 4. 发送给客户端
		 */
		response.getWriter().print(b);
		return null;
	}
	
	/**
	 * 注册功能
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String regist(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到User对象中
		 */
		User formUser = CommonUtils.toBean(request.getParameterMap(), User.class);
		/*
		 * 2. 校验 ，如果校验失败，保存错误信息，返回到regist.jsp页面
		 */
		Map<String,String> errors = validateRegist(formUser, request.getSession());
		if(errors.size() > 0) {
			request.setAttribute("form", formUser);
			request.setAttribute("errors", errors);
			return "f:/jsps/user/regist.jsp";
		}
		/*
		 * 3. 使用service完成业务
		 */
		userService.regist(formUser);
		/*
		 * 4. 保存成功信息，转发到msg.jsp显示
		 */
		request.setAttribute("code", "success");
		request.setAttribute("msg", "注册成功，请到您的邮箱进行激活！");
		System.out.println("regist成功");
		return "f:/jsps/msg.jsp";
	}
	
	/**
	 * 注册校验
	 * 对表单的字段进行逐个校验，如果有错误，使用当前字段名称为key，错误信息为value，保存到map中
	 * @param userFrom
	 * @param session
	 * @return map
	 */
	private Map<String, String> validateRegist(User userFrom, HttpSession session){
		Map<String, String> errors = new HashMap<String, String>();
		
		// a. 用户名校验
		String loginname = userFrom.getLoginname();
		if(loginname == null || loginname.trim().isEmpty()){
			errors.put("loginname", "用户名不能为空！");
		}else if(loginname.length() < 3 || loginname.length() > 20){
			errors.put("loginname", "用户名长度必须为3~20位");
		}else if(!userService.ajaxValidateLoginname(loginname)){
			errors.put("loginname", "用户名已被注册！");
		}
		
		// b. 登录密码校验
		String loginpass= userFrom.getLoginpass();
		if(loginpass == null || loginpass.trim().isEmpty()){
			errors.put("loginpass", "密码不能为空！");
		}else if(loginpass.length() < 3 || loginpass.length() > 20){
			errors.put("loginpass", "密码长度必须为3~20位");
		}
		
		// c. 确认密码校验
		String reloginpass= userFrom.getReloginpass();
		if(reloginpass == null || reloginpass.trim().isEmpty()){
			errors.put("reloginpass", "确认密码不能为空！");
		}else if(!reloginpass.equals(loginpass)){
			errors.put("reloginpass", "两次输入不一致！");
		}
		
		// d. Email校验
		String email = userFrom.getEmail();
		if(email == null || email.trim().isEmpty()){
			errors.put("email", "Email不能为空！");
		}else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")){
			errors.put("email", "Email格式错误");
		}else if(!userService.ajaxValidateEmail(email)){
			errors.put("email", "Email已被注册！");
		}
		
		// e. 验证码校验
		String verifyCode= userFrom.getVerifyCode();
		String vCode = (String) session.getAttribute("vCode");
		if(verifyCode == null || verifyCode.trim().isEmpty()){
			errors.put("verifyCode", "验证码不能为空！");
		}else if(!verifyCode.equalsIgnoreCase(vCode)){
			errors.put("verifyCode", "验证码错误！");
		}
		
		return errors;
	}
	
	/**
	 * 激活功能实现
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String activation(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 获取激活码
		 * 2. 用激活码调用service方法完成激活
		 * 		> service方法有可能抛出异常，把异常信息取得，保存到request中，转发到msg.jsp中显示
		 * 3. 保存成功信息到request中，转发到msg.jsp显示
		 */
		String code = request.getParameter("activationCode");
		try {
			userService.activation(code);
			request.setAttribute("code", "success");//通知msg.jsp显示错号的图片，success为对号
			request.setAttribute("msg", "恭喜你激活成功，请去登录！");
		} catch (UserException e) {
			//说明service抛出了异常
			request.setAttribute("code", "error");//通知msg.jsp显示错号的图片，success为对号
			request.setAttribute("msg", e.getMessage());
		}
		System.out.println("激活码： " + code);
		return "f:/jsps/msg.jsp";
	}
	
	/**
	 * 登录功能
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到User
		 * 2. 校验表单数据
		 * 3. 使用service查询，得到User
		 * 4. 查看用户是否存在，如果不存在：
		 *   * 保存错误信息：用户名或密码错误
		 *   * 保存用户数据：为了回显
		 *   * 转发到login.jsp
		 * 5. 如果存在，查看状态，如果状态为false：
		 *   * 保存错误信息：您没有激活
		 *   * 保存表单数据：为了回显
		 *   * 转发到login.jsp
		 * 6. 登录成功：
		 * 　　* 保存当前查询出的user到session中
		 *   * 保存当前用户的名称到cookie中，注意中文需要编码处理。
		 */
		
		/*
		 * 1. 封装表单数据
		 */
		User formUser = CommonUtils.toBean(request.getParameterMap(), User.class);
		
		/*
		 * 2. 校验表单数据（自己完成）
		 */
		
		/*
		 * 3. 使用service查询，得到User
		 */
		User user = userService.login(formUser);
		/*
		 * 4. 开始判断
		 */
		if(user == null){
			request.setAttribute("msg", "用户名或密码错误！");
			request.setAttribute("formUser", formUser);
			return "f:/jsps/user/login.jsp";
		}else{
			if(!user.isStatus()){
				request.setAttribute("msg", "你还未激活，请登录邮箱进行激活！");
				request.setAttribute("formUser", formUser);
				return "f:/jsps/user/login.jsp";
			}else{
				// 保存用户到session
				request.getSession().setAttribute("sessionUser", user);
				// 获取用户名保存到cookie中
				String loginname = user.getLoginname();
				loginname = URLEncoder.encode(loginname, "utf-8");
				Cookie cookie = new Cookie("loginname", loginname);
				cookie.setMaxAge(60 * 60 * 24 * 10);// 保存cookie 10天
				response.addCookie(cookie);
				return "r:/index.jsp";//重定向到主页
			}
		}
	}
	
	/**
	 * 修改密码
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updatePassword(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到user中
		 * 2. 校验表单数据,从Seeeion中获取uid
		 * 3. 获取当前用户的名称，以及表单数据，给service来完成修改密码
		 * 		*service有可能抛出异常
		 * 		* 把异常信息保存到request中
		 * 		* 转发到pwd.jsp
		 * 4. 如果没有异常，保存成功信息，转发到msg.jsp显示
		 */
		User formUser = CommonUtils.toBean(request.getParameterMap(), User.class);
		User user = (User) request.getSession().getAttribute("sessionUser");
		//如果用户没登录
		if(user == null){
			request.setAttribute("msg", "未登录不能修改密码！");
			return "f:/jsps/user/login.jsp";
		}
		
		try {
			userService.updatePassword(user.getUid(), formUser.getLoginpass(),
					formUser.getNewpass());
			//如果没有抛出异常则密码修改成功
			request.setAttribute("msg", "密码修改成功");
			request.setAttribute("code", "success");
			return "f:/jsps/msg.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());//保存异常信息
			request.setAttribute("formUser", formUser);//为了回显
			return "f:/jsps/user/pwd.jsp";
		}
		
	}
	
	/**
	 * 退出功能
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String quit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().invalidate();
		return "r:/jsps/user/login.jsp";
	}
	
}
