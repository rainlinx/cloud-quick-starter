package com.xdashen.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xdashen.gateway.model.Token;
import com.xdashen.gateway.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

@Slf4j
@Component
public class ResponseFilter extends ZuulFilter {
    @Value("${cloud.token.key}")
    private String key;
    @Value("${cloud.token.expiration}")
    private long expiration;

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        final RequestContext context = RequestContext.getCurrentContext();
        if (Objects.equals(context.getResponseStatusCode(), HttpStatus.SC_OK)) {
            if (context.getRequest().getRequestURI().contains("/login")) {
                final String responseBody;
                try {
                    responseBody = StreamUtils.copyToString(context.getResponseDataStream(), Charset.forName(StandardCharsets.UTF_8.name()));
                } catch (IOException e) {
                    log.error("获取用户信息失败", e);
                    throw new RuntimeException("获取用户信息失败", e);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                final String userName;
                Token token = new Token();
                try {
                    final Map map = objectMapper.readValue(responseBody, Map.class);
                    userName = String.valueOf(map.get("userName"));
                    token.setUserName(userName);
                    context.setResponseBody(responseBody);
                } catch (IOException e) {
                    log.error("获取用户信息失败", e);
                    throw new RuntimeException("获取用户信息失败", e);
                }
                context.getResponse().addHeader("Authorization", TokenUtils.create(token, key, expiration));
            } else {
                final String tokenString = context.getRequest().getHeader("Authorization");
                context.getResponse().addHeader("Authorization", TokenUtils.update(tokenString, key, expiration));
            }
        }
        return null;
    }
}
