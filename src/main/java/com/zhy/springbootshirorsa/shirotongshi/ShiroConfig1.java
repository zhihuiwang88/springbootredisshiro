package com.zhy.springbootshirorsa.shirotongshi;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author cwm
 * @date 2020/2/18
 * @desc shiro 配置文件
 **/
@Configuration
public class ShiroConfig1 {


    /**
     *  开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * @return
     */

    /**
     *     //    @ConditionalOnMissingBean  //@ConditionOnBean在判断list的时候，如果list没有值，返回false，否则返回true
     *     //@ConditionOnMissingBean在判断list的时候，如果list没有值，返回true，否则返回false，其他逻辑都一样
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAap = new DefaultAdvisorAutoProxyCreator();
        defaultAap.setProxyTargetClass(true);
        return defaultAap;
    }

    /**
     *  将自己的验证方式加入容器
     * @return
     */
    @Bean
    public MyRealms1 myRealms() {
        MyRealms1 realms = new MyRealms1();
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("md5");
        matcher.setHashIterations(1);
        realms.setCredentialsMatcher(matcher);
        //添加缓存
//        realms.setCacheManager(redisCacheManager());
        return realms;
    }

    /**
     * 配置session 监听器
     * @return
     */
    @Bean
    public ShiroSessionListener1 shiroSessionListener(){
        ShiroSessionListener1 shiroSessionListener=new ShiroSessionListener1();
        return shiroSessionListener;
    }

    /**
     *共享session
     * @return
     */
  /*  @Bean
    public RedisSessionDao redisSessionDao()
    {
        RedisSessionDao dao=new RedisSessionDao();
        return dao;
    }*/

    /**
     * 权限管理，配置主要是Realm的管理认证
     * @return
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //添加realms
        securityManager.setRealm(myRealms());
        //添加session
        securityManager.setSessionManager(sessionManager());
        //添加缓存配置
//        securityManager.setCacheManager(redisCacheManager());
        //记住我设置
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    /**
     * Filter工厂，设置对应的过滤条件和跳转条件
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 设置自定义的过滤器
        Map<String, Filter> mapfilter = shiroFilterFactoryBean.getFilters();
        mapfilter.put("authc",new CustomeFormAuthenticationFilter1());
        mapfilter.put("perms",new CustomPermissionsAuthorizationFilter1());
        shiroFilterFactoryBean.setFilters(mapfilter);

        Map<String, String> map = new HashMap<>(16);
        //对登录页放行
        map.put("/convention/login", "anon");
        //放行验证码
        map.put("/security/open/**","anon");
        //微信登录放行
        map.put("/weixin/login","anon");
        //微信授权地址放行
        map.put("/weixin/aouth","anon");
        //微信获取用户信息放行
        map.put("/weixin/getuserinfo","anon");
        //swagger接口权限 开放
        map.put("/swagger-ui.html", "anon");
        map.put("/webjars/**", "anon");
        map.put("/v2/**", "anon");
        map.put("/swagger-resources/**", "anon");
        //对所有用户认证
        map.put("/**", "authc");
        //错误页面，认证不通过跳转
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    /**
     * 自定义sessionManager
     * @return
     */
    @Bean
    public SessionManager sessionManager(){
        ShiroSessionManager1 shiroSessionManager = new ShiroSessionManager1();
        //这里可以不设置。Shiro有默认的session管理。如果缓存为Redis则需改用Redis的管理
        Collection<SessionListener> listeners = new ArrayList<SessionListener>();
        listeners.add(shiroSessionListener());
        shiroSessionManager.setSessionListeners(listeners);
//        shiroSessionManager.setSessionDAO(redisSessionDao());
        shiroSessionManager.setSessionIdCookie(sessionIdCookie());
        //全局会话超时时间（单位毫秒），默认30分钟  暂时设置为10秒钟 用来测试
        shiroSessionManager.setGlobalSessionTimeout(1000000000);
        //是否开启删除无效的session对象  默认为true
        shiroSessionManager.setDeleteInvalidSessions(true);
        //是否开启定时调度器进行检测过期session 默认为true
        shiroSessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
        //设置该属性 就不需要设置 ExecutorServiceSessionValidationScheduler 底层也是默认自动调用ExecutorServiceSessionValidationScheduler
        //暂时设置为 5秒 用来测试
        shiroSessionManager.setSessionValidationInterval(50000000);

        //取消url 后面的 JSESSIONID
        shiroSessionManager.setSessionIdUrlRewritingEnabled(false);
        return shiroSessionManager;
    }

    /**
     * 缓存配置
     * 可以放开
     * @return
     */
 /*   @Bean
    public RedisCacheManager redisCacheManager()
    {
        RedisCacheManager redisCacheManager=new RedisCacheManager();
        return redisCacheManager;
    }*/
    /**
     * 配置会话ID生成器
     * @return
     */
    @Bean
    public SessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }
    /**
     * 配置保存sessionId的cookie
     * 注意：这里的cookie 不是上面的记住我 cookie 记住我需要一个cookie session管理 也需要自己的cookie
     * @return
     */
    @Bean
    public SimpleCookie sessionIdCookie(){
        //这个参数是cookie的名称
        SimpleCookie simpleCookie = new SimpleCookie("sid");
        //setcookie的httponly属性如果设为true的话，会增加对xss防护的安全系数。它有以下特点：
        //setcookie()的第七个参数
        //设为true后，只能通过http访问，javascript无法访问
        //防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");
        //maxAge=-1表示浏览器关闭时失效此Cookie
        simpleCookie.setMaxAge(-1);
        return simpleCookie;
    }

    /**
     * cookie 对象 记住我
     * @return
     */
    @Bean
    public SimpleCookie rememberMeCookie()
    {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setHttpOnly(true);
        //cookie生效时间7天,单位秒;
        simpleCookie.setMaxAge(604800);
        return simpleCookie;
    }

    /**
     * cookie 管理对象 ，记住我
     * @return
     */
    @Bean
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        return cookieRememberMeManager;
    }



    //加入注解的使用，不加入这个注解不生效
    /**
     * 开启aop注解支持
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
