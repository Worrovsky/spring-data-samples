package com.example

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import java.sql.ResultSet

fun main() {
    runApplication<AppM006>()
}

@SpringBootApplication
class AppM006(private val jdbcTemplate: JdbcTemplate): CommandLineRunner {

    override fun run(vararg args: String?) {

        // 1. queryForObject() from JdbcOperationsExtensions.kt
        val sql1 = "select name from countries where id = 3"
        val name1 = jdbcTemplate.queryForObject<String>(sql1)
        println("name of country: $name1")

        // 2. queryForObject()
        val sql2 = "select name from countries where id = ?"
        val id = 4
        val name2 = jdbcTemplate.queryForObject(sql2, String::class.java, id)
        println("name of country with id = $id: $name2")

        // 3. queryForObject() + RowMapper
        val sql3 = "select id, name from countries where id = 2"
        val mapper = {rs: ResultSet, _: Int ->
            val name: String = rs.getString("name")
            val id: Long = rs.getLong("id")
            Country(id, name)
        }
        val country = jdbcTemplate.queryForObject(sql3, mapper)
        println("country: $country")

        // 4. queryForList()
        println("\nqueryForList():")
        val listOfMap = jdbcTemplate.queryForList("select name, id from countries")
        listOfMap.forEach {
            println(it) // {NAME=Italy, ID=4} for example
        }

        // 5. queryForList() with type
        println("\nqueryForList(.., Class):")
        val listOfNames = jdbcTemplate.queryForList("select name from countries", String::class.java)
        listOfNames.forEach {
            println(it)
        }

        // 6. queryForMap()
        println("\nqueryForMap():")
        val map = jdbcTemplate.queryForMap("select name, id from countries where id = 2")
        println(map)

        jdbcTemplate.update("")

    }
}


data class Country(val id: Long, val name: String)