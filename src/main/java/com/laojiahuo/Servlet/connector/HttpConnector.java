package com.laojiahuo.Servlet.connector;

import com.laojiahuo.Servlet.engine.HttpServletRequestImpl;
import com.laojiahuo.Servlet.engine.HttpServletResponseImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

public class HttpConnector implements HttpHandler ,AutoCloseable{
    final Logger logger = LoggerFactory.getLogger(getClass());
    final HttpServer httpServer;
    final String  host;
    final int port;

    public HttpConnector(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.httpServer = HttpServer.create(new InetSocketAddress(host, port), 0, "/", this);
        this.httpServer.start();
        logger.info("HttpConnector start success, host:{}, port:{}", host, port);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //适配器实现了HttpExchangeRequest,HttpExchangeResponse的接口，HttpServletRequestImpl实现类内部放了HttpExchangeRequest的对象，只能使用自己的接口。功能进行了聚合再拆分

        HttpExchangeAdapter adapter = new HttpExchangeAdapter(exchange);
        process(new HttpServletRequestImpl(adapter),new HttpServletResponseImpl(adapter));
    }

    private void process(HttpServletRequestImpl request, HttpServletResponseImpl response) throws IOException {
        String name = request.getParameter("name");
        String html = "<h1>welcome, " + (name == null ? "无参数" : name) + ".</h1>";
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.write(html);
        writer.close();
    }

    @Override
    public void close() throws Exception {
        this.httpServer.stop(3);
    }
}
