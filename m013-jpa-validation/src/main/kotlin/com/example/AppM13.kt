package com.example

import com.example.model.User
import com.example.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.LocalDateTime

@SpringBootApplication
class AppM13: CommandLineRunner {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun run(vararg args: String?) {

        val user = User("Alice", LocalDateTime.now())
        userRepository.save(user)

        println(user)
    }
}

fun main() {
    runApplication<AppM13>()
}