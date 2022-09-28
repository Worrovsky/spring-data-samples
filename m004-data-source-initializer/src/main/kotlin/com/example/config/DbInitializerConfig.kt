package com.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.jdbc.datasource.init.DatabasePopulator
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

@Configuration
class DbInitializerConfig(
    @Value("classpath:/db-schema.sql") private val schemaScript: Resource,
    @Value("classpath:/db-data.sql") private val dataScript: Resource
) {
    @Bean
    fun dataSourceInitializer(dataSource: DataSource): DataSourceInitializer {
        val initializer: DataSourceInitializer = DataSourceInitializer()
        initializer.setDataSource(dataSource)
        initializer.setDatabasePopulator(getPopulator())

        return initializer
    }

    private fun getPopulator(): DatabasePopulator {
        val populator: ResourceDatabasePopulator = ResourceDatabasePopulator()
        populator.addScript(schemaScript)

        return populator
    }
}