package com.easyweibo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.easyweibo.mapper")
public class EasyWeiboApplication {
  public static void main(String[] args) {
    SpringApplication.run(EasyWeiboApplication.class, args);
  }
}