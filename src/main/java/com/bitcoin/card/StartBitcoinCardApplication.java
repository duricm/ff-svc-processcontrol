package com.bitcoin.card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartBitcoinCardApplication {

    // start everything
    public static void main(String[] args) {
    	
    	System.setProperty("server.servlet.context-path", "/v1");

        SpringApplication.run(StartBitcoinCardApplication.class, args);
    }


}