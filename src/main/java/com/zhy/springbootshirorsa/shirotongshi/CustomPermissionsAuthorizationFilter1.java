package com.zhy.springbootshirorsa.shirotongshi;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 * @author cwm
 * @date 2020/3/3
 * @desc 授权过滤器重写
 **/
public class CustomPermissionsAuthorizationFilter1 extends PermissionsAuthorizationFilter {
    /**
     * 根据请求接口路径进行验证
     * @param request
     * @param response
     * @param mappedValue
     * @return
     * @throws IOException
     */
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        // 获取接口请求路径
        String servletPath = WebUtils.toHttp(request).getServletPath();
//        mappedValue = new String[]{servletPath};
        return super.isAccessAllowed(request, response, new String[]{servletPath});
    }

    /**
     * 解决权限不足302问题
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = getSubject(request, response);
        if (subject.getPrincipal() == null) {
            saveRequestAndRedirectToLogin(request, response);
        } else {
            WebUtils.toHttp(response).setContentType("application/json; charset=utf-8");
//            WebUtils.toHttp(response).getWriter().print(ResultDTO.loginFail("没有权限"));
        }
        return false;
    }
}
