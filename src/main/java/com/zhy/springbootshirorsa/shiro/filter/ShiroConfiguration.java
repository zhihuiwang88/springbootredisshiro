package com.zhy.springbootshirorsa.shiro.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Filter;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zhy.springbootshirorsa.redis.cache.RedisCacheManager;
import com.zhy.springbootshirorsa.shiro.realm.CustomRealm;
import com.zhy.springbootshirorsa.shiro.session.CustomShiroSessionManager;
import com.zhy.springbootshirorsa.shiro.session.RedisSessionDao;
import com.zhy.springbootshirorsa.shiro.session.ShiroSessionListener;


@Configuration
public class ShiroConfiguration {

    /**
     * https://www.jianshu.com/p/819c35f63e71
     * 当我们写好了自定义Filter后，如何在Shiro中使用它呢？在config类中注入ShiroFilter的bean
    * ShiroFilter主要配置
     * @param securityManager
    * @return
    */
   @Bean
   public ShiroFilterFactoryBean shiroFilter (SecurityManager securityManager){
       ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
       shiroFilterFactoryBean.setSecurityManager(securityManager);

       //自定义拦截器
       Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
       filtersMap.put("authorizationFilter", new CustomAuthorizationFilter());
       filtersMap.put("authc", new CustomAuthenticationFilter());
       shiroFilterFactoryBean.setFilters(filtersMap);

       Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
       //注意过滤器配置顺序 不能颠倒
       //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了，登出后跳转配置的loginUrl
       filterChainDefinitionMap.put("/logout", "logout");
       // 配置不会被拦截的链接 顺序判断
       //filterChainDefinitionMap.put("/hello", "anon");
       filterChainDefinitionMap.put("/user/login1", "anon");
       filterChainDefinitionMap.put("/testRole", "anon");
       filterChainDefinitionMap.put("/**", "authorizationFilter[admin,admin1]");
       // 对所有用户进行认证
       filterChainDefinitionMap.put("/**", "authc");
       // 静态资源放行
       filterChainDefinitionMap.put("/static/**", "anon");
       //自动跳去登录的地址
       shiroFilterFactoryBean.setLoginUrl("/user/login1");
       //上面提到的匿名地址,匿名拦截器，即不需要登录即可访问；一般用于静态资源过滤
//       shiroFilterFactoryBean.setUnauthorizedUrl();
       shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
       return shiroFilterFactoryBean;
   }
	
   
   /**  
	  * cookie对象;  
	  * rememberMeCookie()方法是设置Cookie的生成模版，比如cookie的name，cookie的有效时间等等。  
	  * @return  
	 */  
	@Bean  
	public SimpleCookie rememberMeCookie(){  
	      //System.out.println("ShiroConfiguration.rememberMeCookie()");  
	      //这个参数是cookie的名称
	      SimpleCookie simpleCookie = new SimpleCookie("rememberMe");  
	      //<!-- 记住我cookie生效时间30天 ,单位秒;-->  
	      simpleCookie.setMaxAge(259200);  
	      return simpleCookie;  
	}  
	  
	/**  
	  * cookie管理对象;  
	  * rememberMeManager()方法是生成rememberMe管理器，而且要将这个rememberMe管理器设置到securityManager中  
	  * @return  
	 */  
	@Bean  
	public CookieRememberMeManager rememberMeManager(){  
	      //System.out.println("ShiroConfiguration.rememberMeManager()");  
	      CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();  
	      cookieRememberMeManager.setCookie(rememberMeCookie());  
	      //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)  
	      cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));  
	      return cookieRememberMeManager;  
	}  
	  
	/**
	 * 权限管理，配置主要是Realm的管理认证
	 * @param realm
	 * @return
	 */
	@Bean(name = "securityManager")  
	public DefaultWebSecurityManager defaultWebSecurityManager(CustomRealm realm){  
	      DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();  
	      //设置realm  
	      securityManager.setRealm(realm);  
	      //用户授权/认证信息Cache, 采用redis缓存  
	      securityManager.setCacheManager(redisCacheManager());  
	      // session管理
	      securityManager.setSessionManager(sessionManager());
	      //注入记住我管理器  
	      securityManager.setRememberMeManager(rememberMeManager());  
	      return securityManager;  
	}
	
	    @Bean
	    public RedisCacheManager redisCacheManager(){
	        RedisCacheManager cacheManager = new RedisCacheManager();
	        return  cacheManager;
	    }
   
	    /**
	     * 配置session 监听器
	     * @return
	     */
	    @Bean
	    public ShiroSessionListener shiroSessionListener(){
	        ShiroSessionListener shiroSessionListener=new ShiroSessionListener();
	        return shiroSessionListener;
	    }
	    
	    /**
	     *  将自己的验证方式加入容器
	     * @return
	     */
	    @Bean
	    public CustomRealm myRealms() {
	    	CustomRealm realms = new CustomRealm();
	        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
	        matcher.setHashAlgorithmName("md5");
	        matcher.setHashIterations(1);
	        realms.setCredentialsMatcher(matcher);
	        //添加缓存
	        realms.setCacheManager(redisCacheManager());
	        return realms;
	    }

	    /**
	     *共享session
	     * @return
	     */
	    @Bean
	    public RedisSessionDao redisSessionDao(){
	        RedisSessionDao dao=new RedisSessionDao();
	        return dao;
	    }
	    
	   
	    
	    
	    /**
	     * 自定义sessionManager
	     * @return
	     */
	    @Bean
	    public SessionManager sessionManager(){
	    	CustomShiroSessionManager shiroSessionManager = new CustomShiroSessionManager();
	        //这里可以不设置。Shiro有默认的session管理。如果缓存为Redis则需改用Redis的管理
	        Collection<SessionListener> listeners = new ArrayList<SessionListener>();
	        listeners.add(shiroSessionListener());
	        shiroSessionManager.setSessionListeners(listeners);
	        shiroSessionManager.setSessionDAO(redisSessionDao());
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
	     * 配置保存sessionId的cookie
	     * 注意：这里的cookie 不是上面的记住我 cookie 记住我需要一个cookie， session管理 也需要自己的cookie
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
	     * 配置会话ID生成器
	     * @return
	     */
	    @Bean
	    public SessionIdGenerator sessionIdGenerator() {
	        return new JavaUuidSessionIdGenerator();
	    }
	    
}
