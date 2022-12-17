package com.stone.it.micro.ifeast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author cj.stone
 * @Date 2022/12/9
 * @Desc
 */
@SpringBootApplication
public class IfeastApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(IfeastApplication.class);
    public static void main(String[] args) {
        LOGGER.info("Ifeast Application Start now ........");
        SpringApplication.run(IfeastApplication.class, args);
        LOGGER.info("Ifeast Application Running ........");
    }

}
