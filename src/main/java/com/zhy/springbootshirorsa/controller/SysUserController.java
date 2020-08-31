package com.zhy.springbootshirorsa.controller;

import java.io.IOException;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zhy.springbootshirorsa.pojo.SysUser;
import com.zhy.springbootshirorsa.service.SysUserService;
import com.zhy.springbootshirorsa.shiro.realm.CustomRealm;

@RequestMapping("/user")
@RestController
public class SysUserController {

	@Autowired
	private SysUserService sysUserService;
	
	/**
	 * localhost:8080/user/find
	 * 查询出所有数据
	 * @return
	 */
	@GetMapping("/find")
	public List<SysUser> findAll() {
		return sysUserService.find();
	}
	
	/**
	 * 根据用户名查询信息
	 * @param userName
	 * @return
	 */
	@GetMapping("/findByName")
	public SysUser findByUserName(@RequestParam String userName) {
		return sysUserService.findByUserName(userName);
	}
	
	/**
	 * 新增用户
	 */
	public int addUser(@RequestParam SysUser sysUser) {
		int in = 0;
		try {
			in =  sysUserService.addUser(sysUser);
		} catch (IOException e) {
			e.getMessage();
		}
		return in;
	}
	
	/**
	 * 测试登录1
	 * @param sysUser
	 * @return
	 */
	@PostMapping("/login1")
	public String login1(@RequestBody SysUser sysUser) {
		// 获取主体
		Subject subject  = SecurityUtils.getSubject();
				
		//用户名密码转对象
		UsernamePasswordToken token = new UsernamePasswordToken(sysUser.getUserName(),sysUser.getPassword());
		token.setRememberMe(true);
		try {
			// 验证
			subject.login(token);
			System.out.println("登录成功：" + subject.isAuthenticated());
		} catch (Exception e) {
			e.getMessage();
		}
		return "/index";
	}
	
	/**
	 * https://www.jianshu.com/p/7464327c83fe
	 * 测试登录
	 * @param sysUser
	 */
	@PostMapping("/login")
	public String login(@RequestBody SysUser sysUser) {
		//创建一个默认SecurityManager
		DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
		//创建一个自定义Realm对象
		CustomRealm realm = new CustomRealm();
		//将自定义Realm注入到SecurityManager里
		defaultSecurityManager.setRealm(realm);
		//创建加密Matcher，加密方式为md5，加密次数1
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
		matcher.setHashAlgorithmName("md5");
		matcher.setHashIterations(1);
		//注入到Realm中，这样在验证的时候，会把token传进去的密码自动加密
		realm.setCredentialsMatcher(matcher);
		//获取Subject
		SecurityUtils.setSecurityManager(defaultSecurityManager);
		Subject subject = SecurityUtils.getSubject();
		//设置Token
		AuthenticationToken token = new UsernamePasswordToken("lisi","123");
		//登录验证
		subject.login(token);
		//是否成功验证
		System.out.println("isAuthentication:"+subject.isAuthenticated());
		//是否拥有角色
		subject.checkRoles("admin");
		//是否拥有权限
		subject.checkPermission("user:delete");
		//登出
		subject.logout();
		
		return "/index";
	}
	
	
	
	/**
	 * 测试登录2
	 * @param sysUser
	 * @return
	 */
	@PostMapping("/login2")
	public String loginn2(@RequestBody SysUser sysUser) {
		System.out.println("==:" + sysUser.getUserName() +"," +sysUser.getPassword());
		return "/index";
	}
	
	
	
}
