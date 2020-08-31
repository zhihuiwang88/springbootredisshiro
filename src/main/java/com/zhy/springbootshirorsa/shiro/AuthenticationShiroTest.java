package com.zhy.springbootshirorsa.shiro;

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
		// 新增账户
		SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();
		simpleAccountRealm.addAccount("lisi", "123");
		// 管理安全账户
		DefaultSecurityManager dManager = new DefaultSecurityManager();
		dManager.setRealm(simpleAccountRealm);
		SecurityUtils.setSecurityManager(dManager);
		// 添加用户认证信息
		Subject  subject =  SecurityUtils.getSubject();
		//把用户名和密码封装成UsernamePasswordToken对象
		UsernamePasswordToken token  = new UsernamePasswordToken("lisi","123");
		// 登录记住me
		token.setRememberMe(true);
		try {
			// 进行验证
			subject.login(token);
			boolean bool = subject.isAuthenticated();
		    System.out.println("用户名和密码正确==：" + bool);
		    subject.logout();
			System.out.println("退出登录==：" + subject.isAuthenticated());
		} catch (Exception e) {
			e.getMessage();
		}
		
	}
	
	
	/**
	 * 
	 * @param sysUser
	 */

	
	
	
}
