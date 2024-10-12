package com.laojiahuo.Servlet_FilterChain_support.connector;


import com.laojiahuo.Servlet_FilterChain_support.engine.HttpServletRequestImpl;
import com.laojiahuo.Servlet_FilterChain_support.engine.HttpServletResponseImpl;
import com.laojiahuo.Servlet_FilterChain_support.engine.ServletContextImpl;
import com.laojiahuo.Servlet_FilterChain_support.engine.filter.HelloFilter;
import com.laojiahuo.Servlet_FilterChain_support.engine.filter.LogFilter;
import com.laojiahuo.Servlet_FilterChain_support.engine.servlet.HelloServlet;
import com.laojiahuo.Servlet_FilterChain_support.engine.servlet.IndexServlet;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;

public class HttpConnector implements HttpHandler, AutoCloseable {

    final Logger logger = LoggerFactory.getLogger(getClass());

    final ServletContextImpl servletContext;
    final HttpServer httpServer;
    final Duration stopDelay = Duration.ofSeconds(5);

    public HttpConnector() throws IOException {
        this.servletContext = new ServletContextImpl();
        this.servletContext.initServlets(List.of(IndexServlet.class, HelloServlet.class));
        this.servletContext.initFilters(List.of(LogFilter.class, HelloFilter.class));
        // start http server:
        String host = "0.0.0.0";
        int port = 8080;
        this.httpServer = HttpServer.create(new InetSocketAddress(host, port), 0, "/", this);
        this.httpServer.start();
        logger.info("jerrymouse http server started at {}:{}...", host, port);
    }

    @Override
    public void close() {
        this.httpServer.stop((int) this.stopDelay.toSeconds());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var adapter = new HttpExchangeAdapter(exchange);
        var request = new HttpServletRequestImpl(adapter);
        var response = new HttpServletResponseImpl(adapter);
        // process:
        try {
            this.servletContext.process(request, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
