package com.stone.it.micro.rcms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author jichen
 */
@EnableEurekaClient
@SpringBootApplication
public class TmdbApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmdbApplication.class);

    public static void main(String[] args) {
        LOGGER.info("TMDB Application Start now ........");
        SpringApplication.run(TmdbApplication.class, args);
    }

}
