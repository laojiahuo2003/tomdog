package com.laojiahuo.HTTP;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class SimpleHttpServer implements HttpHandler,AutoCloseable {
    final Logger logger = Logger.getLogger(getClass().getName());
    final int port;
    final String host;
    final HttpServer httpServer;

    public SimpleHttpServer(String host,int port) throws IOException {
        this.port = port;
        this.host = host;
        // 创建的时候需要用到host和端口号
        this.httpServer = HttpServer.create(new InetSocketAddress(host,port),0,"/",this);
        // 将路径"/"与当前的SimpleHttpServer实例绑定，这个实例实现了HttpHandler接口
        this.httpServer.createContext("/", this);  // "/" 表示根路径

        this.httpServer.start();
        logger.info("start tomdog http server at" + host + ":" + port);
    }

    /**
     * 启动一个简单的HTTP服务器
     * @param args
     */
    public static void main(String[] args) {
        try(SimpleHttpServer simpleHttpServer = new SimpleHttpServer("localhost",8080)){
            for(;;){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理收到的HTTP请求
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 获取请求的信息
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String rawQuery = uri.getRawQuery();
        logger.info("method:" + method + " path:" + path + " rawQuery:" + rawQuery);
        // 输出响应
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/html; charset=utf-8");
        responseHeaders.set( "Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, 0);
        String s = "<h1>服务端返回的信息</h1><p>" + LocalDateTime.now().withNano(0) + "</p>";
        try(OutputStream out = exchange.getResponseBody()){
            out.write(s.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void close() throws Exception {
        this.httpServer.stop(3);
    }
}
