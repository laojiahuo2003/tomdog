package com.laojiahuo.Servlet_FilterChain_support.engine.mapping;

import jakarta.servlet.Filter;

public class FilterMapping extends AbstractMapping {

    public final Filter filter;

    public FilterMapping(String urlPattern, Filter filter) {
        super(urlPattern);
        this.filter = filter;
    }
}
