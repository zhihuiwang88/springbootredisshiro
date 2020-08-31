package com.zhy.springbootshirorsa.shirotongshi;


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

import com.zhy.springbootshirorsa.pojo.SysUser;
import com.zhy.springbootshirorsa.service.SysUserService;

/**
 * @author cwm
 * @date 2020/2/18
 * @desc 自定义Realms
 **/
public class MyRealms1 extends AuthorizingRealm {

    @Autowired
    private SysUserService userService;
   

    /**
     * 授权信息
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
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
        simpleAuthorizationInfo.addRole("");
        return simpleAuthorizationInfo;
    }

    /**
     * 身份信息
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if(authenticationToken.getPrincipal()==null){
           return null;
       }
        //获取账号
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        //根据账号查询用户信息
        SysUser user = userService.getUsersByAccount(token.getUsername());
        if(user==null){
            return null;
        }else{
            //这里验证authenticationToken和simpleAuthenticationInfo的信息
            SimpleAuthenticationInfo simpleAuthenticationInfo=new SimpleAuthenticationInfo(user,user.getPassword(),getName());
            //加盐后的设置
            simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(""));
            return simpleAuthenticationInfo;
        }
    }

    /**
     * 如果修改用户权限手动清楚缓存
     */
    public void clearCache() {
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        super.clearCache(principals);
    }
}
