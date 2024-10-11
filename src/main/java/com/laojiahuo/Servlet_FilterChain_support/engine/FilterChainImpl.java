package com.laojiahuo.Servlet_FilterChain_support.engine;

import jakarta.servlet.*;

import java.io.IOException;

public class FilterChainImpl implements FilterChain {

    final Filter[] filters;
    final Servlet servlet;
    final int total;
    int index = 0;

    public FilterChainImpl(Filter[] filters, Servlet servlet) {
        this.filters = filters;
        this.servlet = servlet;
        this.total = filters.length;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        // 终止条件:索引大于过滤器链个数
        if (index < total) {
            int current = index;
            index++;
            // 下一个过滤器运行(req,resp需要传递下去)
            filters[current].doFilter(request, response, this);
        } else {
            servlet.service(request, response);
        }
    }
}
