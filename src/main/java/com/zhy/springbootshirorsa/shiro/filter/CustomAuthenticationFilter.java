package com.zhy.springbootshirorsa.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.thymeleaf.util.StringUtils;


/**
 * 自定义认证过滤器
 * @author Dell
 *
 */
public  class CustomAuthenticationFilter extends AuthenticationFilter{

	private static final  String OPTIONS = "OPTIONS";
	  /**
	   * 屏蔽OPTIONS请求
	   */
	  protected  boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue){
		boolean   accessAllowed = super.isAccessAllowed(request, response, mappedValue);
		  // 判断请求是否是options请求
		 if (!accessAllowed) {
	            String method = WebUtils.toHttp(request).getMethod();
	            if (StringUtils.equalsIgnoreCase(OPTIONS, method)) {
	                return true;
	            }
	        }
		  return accessAllowed;
	  }
	
	  /**
	   * 当访问拒绝时如何处理
	   */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		Subject subject = getSubject(request, response);
        if (subject.getPrincipal() == null) {
            saveRequestAndRedirectToLogin(request, response);
        } else {
            WebUtils.toHttp(response).setContentType("application/json; charset=utf-8");
            WebUtils.toHttp(response).setCharacterEncoding("UTF-8");
            WebUtils.toHttp(response).getWriter().print("没有权限");
        }
		return false;
	}

}
