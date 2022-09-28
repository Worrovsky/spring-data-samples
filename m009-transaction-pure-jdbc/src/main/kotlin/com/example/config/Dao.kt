package com.example.config

import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet
import javax.sql.DataSource

class Dao(private val dataSource: DataSource) {

    private val jdbcTemplate = JdbcTemplate(dataSource)

    fun createTable() {
        jdbcTemplate.update(
            """create table countries
                   (id int not null, name varchar(60));
                 """.trimMargin()
        )
        jdbcTemplate.update("INSERT INTO countries (id, name) VALUES (1, 'USA');")
    }

    fun printCountries(message: String) {

        println("\nCountries $message:")
        val mapper = { rs: ResultSet, _: Int ->
            val id = rs.getLong("id")
            val name = rs.getString("name")
            println("id: $id, name: $name")
        }
        jdbcTemplate.query("select id, name from countries", mapper)
    }
}