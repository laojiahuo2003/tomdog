package com.laojiahuo.Servlet_FilterChain_support.engine.mapping;

import jakarta.servlet.Servlet;

public class ServletMapping extends AbstractMapping {

    public final Servlet servlet;

    public ServletMapping(String urlPattern, Servlet servlet) {
        super(urlPattern);
        this.servlet = servlet;
    }
}
