package com.zhy.springbootshirorsa.realm;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

import com.zhy.springbootshirorsa.pojo.SysUser;

public class AuthenticationShiroTest {

	/**
	 * shiro 认证
	 */
	@Test
	public  void authentication() {
		CustomRealmT customRealmT = new CustomRealmT();
		// 管理安全账户
		DefaultSecurityManager dManager = new DefaultSecurityManager();
		dManager.setRealm(customRealmT);
		SecurityUtils.setSecurityManager(dManager);
		// 添加用户认证信息
		Subject  subject =  SecurityUtils.getSubject();
		//把用户名和密码封装成UsernamePasswordToken对象
		UsernamePasswordToken token  = new UsernamePasswordToken("lisi","1234");
		// 登录记住me
		try {
			// 进行验证
			subject.login(token);
			boolean bool = subject.isAuthenticated();
		    System.out.println("用户名和密码正确==：" + bool);
		} catch (Exception e) {
			e.getMessage();
		}
		
	}
	
	
	/**
	 * 对象接收参数
	 * @param sysUser
	 */
	public void testOne(SysUser sysUser) {
		// 获取用户主体
		Subject subject = SecurityUtils.getSubject();
		// 用户名密码转对象
		UsernamePasswordToken token = new UsernamePasswordToken(sysUser.getUserName(),sysUser.getPassword());
		token.setRememberMe(true);
		try {
			// 验证		
			subject.login(token);
			boolean bool = subject.isAuthenticated();		
			if(bool) {
				System.out.println("认证通过");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
		}
		
		
		
	}
	
}
