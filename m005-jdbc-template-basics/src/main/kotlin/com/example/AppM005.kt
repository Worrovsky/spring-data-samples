package com.example

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject

@SpringBootApplication
class App004(private val jdbcTemplate: JdbcTemplate): CommandLineRunner {

//    @Autowired
//    private lateinit var jdbcTemplate: JdbcTemplate

    override fun run(vararg args: String?) {
        val rowCount = jdbcTemplate.queryForObject<Int>("select count(*) from countries")
        println("table size: $rowCount")
    }
}

fun main() {
    runApplication<App004>()
}