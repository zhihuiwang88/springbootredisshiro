package com.zhy.springbootshirorsa.shiro.session;

import java.io.Serializable;

import javax.servlet.ServletRequest;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;


/**
 * https://www.jianshu.com/p/5aa03c2d118e
 * 我们可以通过继承DefaultWebSessionManager来自定义我们的SessionManager，
 * 如下，我们将session存入到会话中的request，这样就不用每次都去缓存中取,提高性能.
 * @author Dell
 *
 */
public class CustomShiroSessionManager extends DefaultWebSessionManager {

	
		/**
		 * 重写DefaultSessionManager类中的retrieveSession方法。
		 * 检索会话
		 */
	    @Override
	    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
	    	// 获取sessionID
	        Serializable sessionId = getSessionId(sessionKey);
	        ServletRequest request = null;
	        // 判断对象是否为一个类的实例
	        if(sessionId instanceof WebSessionKey){
	            request = ((WebSessionKey)sessionKey).getServletRequest();
	        }
	        if(request != null && sessionId != null){
	             Session session =  (Session)request.getAttribute(sessionId.toString());
	             if (session != null) {
	            	 return session;
	             }
	        }
	        //将session存入到会话中的request
	        Session session = super.retrieveSession(sessionKey);
	        if(request !=null && sessionId != null){
	            request.setAttribute(sessionId.toString(),session);
	        }
	        return session;
	    }
	    
	    
	    
	
}
