package com.kakao.globalid.guid.config;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.stereotype.Component;

//import javax.sql.DataSource;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.boot.jdbc.DataSourceBuilder;

import java.util.concurrent.*;

@Configuration
@Component
public class GuidConfig{

    @Value("${env.layer}")  //application.properties 설정 세팅
    private String worklayer;

    public String getWorkLayer(){
        //System.out.println("###DEBUG### - WORK LAYER config :: " + worklayer);
        return worklayer;
    }

    @Bean
    public ExecutorService threadPoolExecutor() {
        int corePoolSize = 1000;
        int maxPoolSize = 5000;
        long keepAliveTime = 60L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                timeUnit,
                workQueue
        );
    }
    
    }

