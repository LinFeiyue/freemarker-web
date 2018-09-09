package com.pache.framework.web.security.filter;

import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义过滤器
 * 继承的抽象方法：
 * 包含了AccessDecisionManager(决策管理器)、AuthenticationManager(身份认证管理器)的setter， 可以通过Spring自动注入，
 * 另外，"资源角色授权器"需要单独自定义注入
 */
public class CustomSecurityFilter extends AbstractSecurityInterceptor implements Filter {

    //过去器申请常量
    private static final String FILTER_APPLIED = "__spring_security_filterSecurityInterceptor_filterApplied";

    private static boolean observeOncePerRequest = true;
    //资源角色授权器
    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    private String defaultFailureUrl = "/login";

    public static String getFilterApplied() {
        return FILTER_APPLIED;
    }

    public FilterInvocationSecurityMetadataSource getSecurityMetadataSource() {
        return securityMetadataSource;
    }

    public void setSecurityMetadataSource(FilterInvocationSecurityMetadataSource securityMetadataSource) {
        this.securityMetadataSource = securityMetadataSource;
    }

    public String getDefaultFailureUrl() {
        return defaultFailureUrl;
    }

    public void setDefaultFailureUrl(String defaultFailureUrl) {
        this.defaultFailureUrl = defaultFailureUrl;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        FilterInvocation filterInvocation = new FilterInvocation(servletRequest,servletResponse,filterChain);
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        //anonymousUser 匿名登陆用户
        if("anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            this.loginFaile(request,response);
        }else{
            this.invoke(filterInvocation);
        }
    }

    /**
     * 这个类参考的是FilterSecurityInterceptor这个类写的，只是修改了FILTER_APPLIED的值。
     * 主要是重写了父类的方法，添加认证数据源值的设置。使父类在调用方法是能找到对应的数据。
     * @param fi
     * @throws IOException
     * @throws ServletException
     */
    public void invoke(FilterInvocation fi) throws IOException, ServletException {
        if (fi.getRequest() != null && fi.getRequest().getAttribute(this.FILTER_APPLIED) != null && this.observeOncePerRequest) {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } else {
            if (fi.getRequest() != null) {
                fi.getRequest().setAttribute(this.FILTER_APPLIED, Boolean.TRUE);
            }

            InterceptorStatusToken token = super.beforeInvocation(fi);

            try {
                fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
            } finally {
                super.finallyInvocation(token);
            }

            super.afterInvocation(token, (Object)null);
        }

    }

    /**
     * 登陆失败后跳转
     * @param request
     * @param response
     * @throws IOException
     */
    private void loginFaile(HttpServletRequest request,HttpServletResponse response) throws IOException {
        if (this.defaultFailureUrl.indexOf("http://") > -1) {
            response.sendRedirect(this.defaultFailureUrl);
        } else {
            response.sendRedirect(request.getContextPath() + this.defaultFailureUrl);
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public Class<?> getSecureObjectClass() {
        return null;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return null;
    }
}
