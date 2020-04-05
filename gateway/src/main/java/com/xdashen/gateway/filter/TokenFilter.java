package com.xdashen.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xdashen.gateway.util.TokenUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
public class TokenFilter extends ZuulFilter {

    @Value("${cloud.token.key}")
    private String key;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        final HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        return !request.getRequestURI().contains("/login") && !HttpMethod.OPTIONS.matches(request.getMethod());
    }

    @Override
    public Object run() throws ZuulException {
        final RequestContext context = RequestContext.getCurrentContext();
        final HttpServletRequest request = context.getRequest();
        final String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization)) {
            if (!TokenUtils.verify(authorization, key)) {
                context.getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());
                context.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
                context.setResponseBody("登陆已过期，请重新登录");
                context.setSendZuulResponse(false);
            }
        } else {
            context.getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());
            context.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            context.setResponseBody("您无权访问本系统");
            context.setSendZuulResponse(false);
        }
        return null;
    }
}
