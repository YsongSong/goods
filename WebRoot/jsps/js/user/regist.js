$(function(){
	/*
	 * 1.得到所有的错误信息，循环遍历。调用一个方法来确定是否显示错误信息!
	 */
	$(".errorClass").each(function() {
		showError($(this));//遍历每个元素，使用每个元素来调用showError方法
	});
	
	/*
	 * 2.切换注册按钮图片
	 */
	$("#submitBtn").hover(
		function (){
			$("#submitBtn").attr("src","/goods/images/regist2.jpg");
		},
		function (){
			$("#submitBtn").attr("src","/goods/images/regist1.jpg");
		}
	);
	
	/*
	 * 3.输入框得到焦点隐藏错误信息
	 */
	$(".inputClass").focus(function() {
		var lableId = $(this).attr("id") + "Error";//通过输入框找到对应的lable的id
		$("#" + lableId).text("");//把内容清空！
		showError($("#" + lableId));//隐藏没有错误信息的lable
	});
	
	/*
	 * 4.输入框失去焦点进行校验
	 */
	$(".inputClass").blur(function() {
		var id = $(this).attr("id");
		var funName = "validate" + id.substring(0,1).toUpperCase() + id.substring(1) + "()";//得到对应的校验函数名
		eval(funName);//执行函数调用(将字符串当成JavaScript代码执行 )
	});
	
	/*
	 * 5. 表单提交时进行校验
	 */
	$("#registForm").submit(function() {
		var bool = true;//表示校验通过
		if(!validateLoginname()) {
			bool = false;
		}
		if(!validateLoginpass()) {
			bool = false;
		}
		if(!validateReloginpass()) {
			bool = false;
		}
		if(!validateEmail()) {
			bool = false;
		}
		if(!validateVerifyCode()) {
			bool = false;
		}
		
		return bool;
	});
});

/**
 * 登录名校验方法
 */
function validateLoginname() {
	var id = "loginname";
	var value = $("#" + id).val();//获取输入框内容
	var flg = 1;
	/*
	 * 1. 非空校验
	 */
	if(!value) {
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#" + id + "Error").text("用户名不能为空！");
		showError($("#" + id + "Error"));
		flg = 0;
		return flg;
	}
	
	/*
	 * 2.长度校验
	 */
	if(value.length < 3 || value.length > 20) {
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#" + id + "Error").text("用户名长度必须在3 ~ 20之间！");
		showError($("#" + id + "Error"));
		flg = 0;
		return flg;
	}
	
	/*
	 * 3.是否注册校验
	 */
	$.ajax({
		url:"/goods/userServlet",
		data:{method:"ajaxValidateLoginname", loginname:value},
		type:"POST",
		dataType:"json",
		async:false,
		cache:false,
		success:function(result){
			if(!result){//如果校验失败
				$("#" + id + "Error").text("用户名已被注册！");
				showError($("#" + id + "Error"));
				flg = 0;
				return flg;
			}
		}
	});
	return flg;
}

/**
 * 登录密码校验方法
 */
function validateLoginpass() {
	var id = "loginpass";
	var value = $("#" + id).val();//获取输入框内容
	var flg = 1;
	/*
	 * 1. 非空校验
	 */
	if(!value) {
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#" + id + "Error").text("密码不能为空！");
		showError($("#" + id + "Error"));
		flg = 0;
		return flg;
	}
	
	/*
	 * 2.长度校验
	 */
	if(value.length < 3 || value.length > 20) {
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#" + id + "Error").text("密码长度必须在3 ~ 20之间！");
		showError($("#" + id + "Error"));
		flg = 0;
		return flg;
	}
	
	return true;
}

/**
 * 确认密码校验方法
 */
function validateReloginpass() {
	var id = "reloginpass";
	var value = $("#" + id).val();//获取输入框内容
	/*
	 * 1. 非空校验
	 */
	if(!value) {
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#" + id + "Error").text("确认密码不能为空！");
		showError($("#" + id + "Error"));
		return false;
	}
	
	/*
	 * 2.长度校验
	 */
	if(value != $("#loginpass").val()) {
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#" + id + "Error").text("两次输入不一致！");
		showError($("#" + id + "Error"));
		return false;
	}
	
	return true;
}

/**
 * Email校验方法
 */
function validateEmail() {
	var id = "email";
	var value = $("#" + id).val();//获取输入框内容
	var flg = 1;
	/*
	 * 1. 非空校验
	 */
	if(!value) {
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#" + id + "Error").text("email不能为空！");
		showError($("#" + id + "Error"));
		flg = 0;
		return flg;
	}
	
	/*
	 * 2.长度校验
	 */
	if(!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(value)) {
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#" + id + "Error").text("Emial格式错误！");
		showError($("#" + id + "Error"));
		flg = 0;
		return flg;
	}
	
	/*
	 * 3. 邮箱是否被注册校验
	 */
	$.ajax({
		url:"/goods/userServlet",
		data:{method:"ajaxValidateEmail", email:value},
		type:"POST",
		dataType:"json",
		async:false,
		cache:false,
		success:function(result){
			if(!result){
				$("#" + id + "Error").text("邮箱已被注册！");
				showError($("#" + id + "Error"));
				flg = 0;
				return flg;
			}
		}
	});
	return flg;
}

/**
 * 验证码校验方法
 */
function validateVerifyCode() {
	var id = "verifyCode";
	var value = $("#" + id).val();//获取输入框内容
	var flg = 1;
	/*
	 * 1. 非空校验
	 */
	if(!value) {
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#" + id + "Error").text("验证码不能为空！");
		showError($("#" + id + "Error"));
		flg = 0;
		return flg;
	}
	
	/*
	 * 2.长度校验
	 */
	if(value.length != 4) {
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#" + id + "Error").text("验证码错误！");
		showError($("#" + id + "Error"));
		flg = 0;
		return flg;
	}
	/*
	 * 3.验证码是否正确校验
	 */
	$.ajax({
		url:"/goods/userServlet",//要请求的servlet
		data:{method:"ajaxValidateVerifyCode", verifyCode:value},//给服务器的参数
		type:"POST",
		dataType:"json",
		async:false,//是否异步请求，如果是异步，那么不会等服务器返回，我们这个函数就向下运行了。
		cache:false,
		success:function(result) {
			if(!result) {//如果校验失败
				$("#" + id + "Error").text("验证码错误！");
				showError($("#" + id + "Error"));
				flg = 0;
				return flg;
			}
		}
	});
	return flg;
}

/**
 * 判断当前元素是否存在内容，如果存在显示，不存在不显示！
 */
function showError(ele) {
	var text = ele.text();//获取元素内容
	if(!text){//没有内容
		ele.css("display","none");//隐藏元素
	}else{
		ele.css("display","");//显示元素
	}
}

/**
 * 换一种验证码
 */
function _hyz() {
	/*
	 * 1. 获取<img>元素
	 * 2. 重新设置它的src
	 * 3. 使用毫秒来添加参数
	 */
	$("#imgVerifyCode").attr("src", "/goods/verifyCode?a=" + new Date().getTime());
}
