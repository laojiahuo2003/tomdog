package com.laojiahuo.Servlet;

import com.laojiahuo.Servlet.connector.HttpConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Start {
    static Logger logger = LoggerFactory.getLogger(Start.class);
    public static void main(String[] args) {
        try(HttpConnector connector = new HttpConnector("localhost",8080)){
            for(;;){
                Thread.sleep(1000);
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        logger.info("jerrymouse http server was shutdown.");
    }
}
