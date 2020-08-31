package com.zhy.springbootshirorsa.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class AuthorizationShiroTest {

	/**
	 * 测试不对
	 * shiro授权
	 * 给李四添加角色为老师
	 * https://www.imooc.com/video/16954
	 */
	@Test
	public void authorization() {
		
		SimpleAccountRealm sAccountRealm = new SimpleAccountRealm();
		sAccountRealm.addAccount("lisi", "1234", "laoshi");
		DefaultSecurityManager dSecurityManager = new DefaultSecurityManager();
		dSecurityManager.setRealm(sAccountRealm);
		
		SecurityUtils.setSecurityManager(dSecurityManager);
		// 获取当前主体
		Subject subject = SecurityUtils.getSubject();
		// 用户名密码角色转为对象
		UsernamePasswordToken token = new UsernamePasswordToken("lisi","1234");
		try {
			// 进行认证
			subject.login(token);
			boolean bool = subject.isAuthenticated();
			System.out.println("用户角色认证通过：" + bool);
			subject.checkRole("laoshi");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
		}
		
		
	}

	
	
}
