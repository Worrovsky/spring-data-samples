package com.example

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet

@SpringBootApplication
class AppM006(private val jdbcTemplate: JdbcTemplate): CommandLineRunner {

    override fun run(vararg args: String?) {

        // ----------- RowMapper -----------------------
        val rowMapper: (ResultSet, Int) -> String = { rs: ResultSet, _: Int ->
            val name = rs.getString(2)
            println(name)
            name
        }

        val listOfNames = jdbcTemplate.query("select * from countries", rowMapper)
        listOfNames.forEach {
            println("name: $it")
        }

        // ------------- RowCallbackHandler ------------
        var sumOfId = 0
        val rowCallbackHandler = { rs: ResultSet ->
            val id = rs.getInt("id")
            sumOfId += id
        }
        jdbcTemplate.query("select id, name from countries", rowCallbackHandler)
        println("Sum of id = $sumOfId")

        // -------------- ResultSetExtractor ----------
        val extractor = { resultSet: ResultSet ->
            var sum = 0
            while (resultSet.next()) {
                val id = resultSet.getInt("id")
                sum += id
                if (id >= 3) break
            }
            sum
        }
        val sumFirst3 = jdbcTemplate.query("select id, name from countries", extractor)
        println("sum from extractor: $sumFirst3")

    }
}

fun main() {
    runApplication<AppM006>()
}

data class Country(val id: Long, val name: String)