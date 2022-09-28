package com.example

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import java.sql.ResultSet

fun main() {
    runApplication<AppM006>()
}

@SpringBootApplication
class AppM006(private val jdbcTemplate: JdbcTemplate) : CommandLineRunner {

    override fun run(vararg args: String?) {

        val keyHolder = GeneratedKeyHolder()

        // 1. not idiomatic, just for clarity
        jdbcTemplate.update({ connection ->
            val sql = "insert into countries (name) values(?)"
            val columns = arrayOf("id")
            val ps = connection.prepareStatement(sql, columns)
            ps.setString(1, "Denmark")
            ps
        }, keyHolder)
        println("Denmark id: ${keyHolder.key}")

        // 2. idiomatic way
        val INSERT_SQL = "insert into countries (name) values(?)"
        val name = "Poland"
        jdbcTemplate.update({
            it.prepareStatement (INSERT_SQL, arrayOf("id")).apply { setString(1, name) }
        }, keyHolder)
        val newId = keyHolder.key
        println("new id: $newId")

        val mapper = { rs: ResultSet, _: Int ->
            val id = rs.getLong("id")
            val name = rs.getString("name")
            Country(id, name)
        }
        val list = jdbcTemplate.query("select id, name from countries", mapper)
        list.forEach {
            println(it)
        }

    }
}


data class Country(val id: Long, val name: String)