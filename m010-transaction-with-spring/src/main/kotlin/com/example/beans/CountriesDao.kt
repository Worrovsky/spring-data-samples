package com.example.beans

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet
import javax.sql.DataSource

class CountriesDao(private val dataSource: DataSource) {

    private val jdbcTemplate = JdbcTemplate(dataSource)

    fun createTable() {
        jdbcTemplate.update(
            """create table countries
            (id int not null, name varchar(60));"""
        )
    }

    fun insert(id: Int, name: String) {
        jdbcTemplate.update("insert into countries (id, name) values (?, ?);", id, name)
    }

    fun printCountries() {

        val mapper = { rs: ResultSet, _: Int ->
            val id = rs.getLong("id")
            val name = rs.getString("name")
            println("id: $id, name: $name")
        }
        jdbcTemplate.query("select id, name from countries", mapper)
    }

}