package com.zhy.springbootshirorsa.realm;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.zhy.springbootshirorsa.dao.SysUserDao;
import com.zhy.springbootshirorsa.pojo.SysUser;
import com.zhy.springbootshirorsa.service.SysUserService;

public class CustomRealmT  extends AuthorizingRealm{

	@Autowired
	private SysUserService  sysUserService;
	@Autowired
	private SysUserDao  sysUserDao;
	
	Map<String, String> userMap = new HashMap<String, String>(16);
	{
		userMap.put("lisi", "1234");
		super.setName("sysUserRealm");
	}
	
	
	
	/**负责授权检测*/
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		
		
		
		return null;
	}

	/**
	 * 此方法负责认证检测(检测用户身份是否存在,密码是否正确)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 获取用户名
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		String userName = usernamePasswordToken.getUsername();
		System.out.println("======:" + userName);
		// 根据用户名对象,如果用对象获取名字，测试类报错：空指针。控制器测试没有 
//		SysUser  sysUser= sysUserDao.findByUserName(userName);
		// 初始化SimpleAuthenticationInfo对象
//		ByteSource saltSource = ByteSource.Util.bytes(sysUser.getSalt().getBytes());
		SimpleAuthenticationInfo sAuthenticationInfo = new SimpleAuthenticationInfo(
				// 用户名
				"lisi",
				// 已经加密的密码
				"1234",
//				sysUser.getPassword(),
//				盐值对应的ByteSource
//				saltSource,
				// realm的名字
				getName()
				);
		// 存储用户信息
		SecurityUtils.getSubject().getSession().setAttribute("user", userMap.get("lisi"));
		System.out.println("getName:" + getName());
		System.out.println("---:" + 
				SecurityUtils.getSubject().getSession().getAttribute("user")
				);
		
		return sAuthenticationInfo;
	}

	
	
	
}
