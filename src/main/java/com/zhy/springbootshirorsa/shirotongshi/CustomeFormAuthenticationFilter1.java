package com.zhy.springbootshirorsa.shirotongshi;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import com.alibaba.druid.util.StringUtils;


/**
 * @author cwm
 * @date 2020/3/3
 * @desc 前端请求接口时会出现302错误，解决办法是继承
 **/
public class CustomeFormAuthenticationFilter1 extends FormAuthenticationFilter {
    /**
     * 屏蔽OPTIONS请求
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        boolean accessAllowed = super.isAccessAllowed(request, response, mappedValue);
        if (!accessAllowed) {
            // 判断请求是否是options请求
            String method = WebUtils.toHttp(request).getMethod();
//            if (StringUtils.equalsIgnoreCase(OPTIONS, method)) {
            
            if (StringUtils.equalsIgnoreCase("", method)) {
                return true;
            }
        }
        return accessAllowed;
    }

    /**
     * 解决未登录302问题
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                return executeLogin(request, response);
            } else {
                return true;
            }
        } else {
            // 返回固定的JSON串
            WebUtils.toHttp(response).setContentType("application/json; charset=utf-8");
//            WebUtils.toHttp(response).getWriter().print(JSONObject.fromObject(ResultDTO.loginFail("用户未登录")).toString());
            return false;
        }
    }
}
