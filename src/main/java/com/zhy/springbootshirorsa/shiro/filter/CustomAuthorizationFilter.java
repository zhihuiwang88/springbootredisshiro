package com.zhy.springbootshirorsa.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 * 自定义授权过滤器
 * @author Dell
 *
 */
public class CustomAuthorizationFilter extends AuthorizationFilter{

	/*
	 * https://www.jianshu.com/p/054c925cd45d
	 * 允许访问，
	 * mappedValue就是[urls]配置中拦截器参数部分，如果允许访问返回true，否则false
	 * (non-Javadoc)
	 * @see org.apache.shiro.web.filter.AccessControlFilter#isAccessAllowed(javax.servlet.ServletRequest, javax.servlet.ServletResponse, java.lang.Object)
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		// 获取主体
		Subject subject = getSubject(request, response);
		// 判断
		String[] roles = (String[]) mappedValue;
		if(roles.length == 0 || roles == null) {
			return true;
		}
		for (String role : roles) {
			if(subject.hasRole(role)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 当访问拒绝时如何处理
	 * 表示当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；如果返回false表示该拦截器实例已经处理了，将直接返回即可
	 */
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		Subject subject = getSubject(request, response);
		if (subject.getPrincipal() == null) {
			// 如果未登录，保存当前页面，重定向到登录页面
			saveRequestAndRedirectToLogin(request, response);
		} else {
			WebUtils.toHttp(response).setContentType("application/json; charset=utf-8");
			WebUtils.toHttp(response).setCharacterEncoding("UTF-8");
			// 匿名访问地址
			String unauthorizedUrl = getUnauthorizedUrl();
			if (StringUtils.hasText(unauthorizedUrl)) {
				// 如果匿名访问地址存在，则跳转去匿名访问地址
				WebUtils.issueRedirect(request, response, unauthorizedUrl);
			} else {
				// 不存在则返回404
				WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}
		return false;
	}
	
	
}
