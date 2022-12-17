package com.stone.it.micro.ifeast.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author cj.stone
 * @Date 2022/11/13
 * @Desc
 */
@Configuration
@MapperScan(basePackages = "com.stone.it.**.dao")
public class MybatisMapScan {
}
