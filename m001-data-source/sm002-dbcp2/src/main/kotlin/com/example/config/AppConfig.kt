package com.example.config

import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class AppConfig {

    @Bean("dataSource")
    fun getDataSource(): DataSource {
        val dataSource: BasicDataSource = BasicDataSource()
        dataSource.driverClassName = "org.h2.Driver"
        dataSource.url = "jdbc:h2:~/test"

        dataSource.initialSize = 5
        val initSize = dataSource.initialSize
        println("pool size: $initSize")
        return dataSource
    }

}