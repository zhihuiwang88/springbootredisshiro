package com.zhy.springbootshirorsa.shirotongshi;

import java.io.Serializable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;

/**
 * @author cwm
 * @date 2020/2/21
 * @desc sessionManager
 **/
public class ShiroSessionManager1 extends DefaultWebSessionManager {

    /**
     * 添加日志
     */
    private static final Logger logger= LoggerFactory.getLogger(ShiroSessionManager1.class);

    private static final String CWM_AUTHORIZATION_TOKEN = "cwm_auth_token";
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String id = WebUtils.toHttp(request).getHeader(CWM_AUTHORIZATION_TOKEN);
//        System.out.println("sessionid："+id);
        if(StringUtils.isEmpty(id)){
            //如果没有携带id参数则按照父类的方式在cookie进行获取
            System.out.println("super_cookie："+super.getSessionId(request, response));
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,ShiroHttpServletRequest.URL_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID,id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID,Boolean.TRUE);
            return id;
        }
        return super.getSessionId(request, response);
    }

    /**
     * 重写retrieveSession，为了避免多次调用缓存
     * @param sessionKey
     * @return
     * @throws UnknownSessionException
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request =null;
        if(sessionKey instanceof WebSessionKey){
            request= ((WebSessionKey)sessionKey).getServletRequest();
        }
        if(request!=null &&sessionId!=null){
            Session session= (Session) request.getAttribute(sessionId.toString());
            if(session!=null){
                return session;
            }
        }
        Session session=super.retrieveSession(sessionKey);
        if(session!=null&&sessionId!=null)
        {
            request.setAttribute(sessionId.toString(),session);
        }
        return session;
    }
}
