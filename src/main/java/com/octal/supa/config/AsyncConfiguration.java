package com.octal.supa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean(name = "inventoryExecutor")
    public Executor handleInventorySync() {return new ThreadPoolTaskExecutor();}

    @Bean(name = "customerSyncExecutor")
    public Executor handleCustomerSync() {return new ThreadPoolTaskExecutor();}

}
