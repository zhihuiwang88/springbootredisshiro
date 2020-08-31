package com.zhy.springbootshirorsa.shiro.realm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import com.zhy.springbootshirorsa.dao.SysUserDao;
import com.zhy.springbootshirorsa.pojo.SysUser;
import com.zhy.springbootshirorsa.service.SysUserService;

public class CustomRealm  extends AuthorizingRealm{

	@Autowired
	private SysUserDao  sysUserDao;
	@Autowired
    private  SysUserService  sysUserService;
	
	
	/**
	 * 负责授权检测,
	 * 授权三种写法具体自己的喜好选择
	 * 没有测试
	 * */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 获取用户权限(用户-->角色-->权限)，获取用户信息
		SysUser user = (SysUser) SecurityUtils.getSubject().getSession().getAttribute("user");
		// 根据用户信息获取用户访问权限
		List<String>  list = sysUserDao.findUserPermissions(user.getId());
		// 去重复的权限
		Set<String> set = new HashSet<String>();
		for (String string : list) {
			if(string != null) {
				set.add(string);
			}
		}
		// 封装权限
		SimpleAuthorizationInfo sAuthorizationInfo = new SimpleAuthorizationInfo();
		sAuthorizationInfo.addStringPermissions(set);
		return sAuthorizationInfo;
	}

	/**
	 * 授权第二种写法（采纳）
	 */
    protected AuthorizationInfo doGetAuthorizationInfo2(PrincipalCollection principals) {
        //获取用户名
        String username = (String) principals.getPrimaryPrincipal();
        //此处从数据库获取该用户的角色
        Set<String> roles = getRolesByUserName(username);
        //此处从数据库获取该角色的权限
        Set<String> permissions = getPermissionsByUserName(username);
        //放到info里返回
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permissions);
        info.setRoles(roles);
        return info;
    }
	
	
	private Set<String> getPermissionsByUserName(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	private Set<String> getRolesByUserName(String username) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * 授权第三种写法
	 */
	   protected AuthorizationInfo doGetAuthorizationInfo3(PrincipalCollection principalCollection) {
	        /**
	         * 获取用户名
	         */
	        SysUser sysuser = (SysUser) principalCollection.getPrimaryPrincipal();
	        /**
	         * 添加角色和权限
	         */
	        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
	        /**
	         * 添加角色
	         */
//	        simpleAuthorizationInfo.addRole(sysuser.getRoleId().toString());
	        return simpleAuthorizationInfo;
	    }

	/**
	 * 登录后检测用户名密码三种写法，选择自己喜欢的。
	 * 此方法负责认证检测(检测用户身份是否存在,密码是否正确)
	 * 测试不完整
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 获取用户名
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		String userName = usernamePasswordToken.getUsername();
		System.out.println("userNameAuthentication:" + userName);
		// 根据用户名对象
//		SysUser  sysUser= sysUserService.findByUserName(userName);
		// 初始化SimpleAuthenticationInfo对象
//		ByteSource saltSource = ByteSource.Util.bytes(sysUser.getSalt().getBytes());
		SimpleAuthenticationInfo sAuthenticationInfo = new SimpleAuthenticationInfo(
				// 用户名
				"lisi",
//				sysUser.getUserName(),
				// 已经加密的密码
				"123",
//				sysUser.getPassword(),
				//盐值对应的ByteSource
//				saltSource,
				// realm的名字
				getName()
				);
		// 存储用户信息
//		SecurityUtils.getSubject().getSession().setAttribute("user", sysUser);
		return sAuthenticationInfo;
	}

	/**
	 *检测 第二方法（采纳）
	 */
    protected AuthenticationInfo doGetAuthenticationInfo2(AuthenticationToken token) throws AuthenticationException {
        //1.从主体传过来的认证信息中，获得用户名
        String username = (String) token.getPrincipal();
        //从数据库读取该用户        
        SysUser users = sysUserService.getPasswordByUsername(username);
        if(users == null){
            return null;
        }
        String password = users.getPassword();
        //将数据放入info中返回
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username,password,getName());
        //加盐，表示加密的时候加盐加密
        //info.setCredentialsSalt(ByteSource.Util.bytes("Hiway"));
        return info;
    }
	
	/**
	 * 检测 第三方法
	 */
    protected AuthenticationInfo doGetAuthenticationInfo3(AuthenticationToken authenticationToken) throws AuthenticationException {
        if(authenticationToken.getPrincipal()==null){
           return null;
       }
        //获取账号
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        //根据账号查询用户信息
        SysUser user = sysUserService.getUsersByAccount(token.getUsername());
        if(user == null){
            return null;
        }else{
            //这里验证authenticationToken和simpleAuthenticationInfo的信息
            SimpleAuthenticationInfo simpleAuthenticationInfo=new SimpleAuthenticationInfo(user,user.getPassword(),getName());
        	//加盐后的设置，要放开
//            simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(wazConfig.getSalt()));
        	return simpleAuthenticationInfo;
        }
    }
	
	
	  /**
     * 如果修改用户权限手动清楚缓存
     */
	public void clearCache() {
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		super.clearCache(principalCollection);
	}
	
}
