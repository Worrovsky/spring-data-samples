package com.example.config

import com.example.beans.CountriesDao
import com.example.beans.CountryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class AppConfig {

    @Bean("dataSource")
    fun getDataSource(): DataSource {
        val dataSource: DataSource = EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build()
        return dataSource
    }

    @Bean
    fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean
    fun countriesDao(dataSource: DataSource) = CountriesDao(dataSource)

    @Bean
    fun countriesService(countriesDao: CountriesDao): CountryService {
        return CountryService(countriesDao)
    }
}