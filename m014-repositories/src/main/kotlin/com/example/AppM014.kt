package com.example

import com.example.model.User
import com.example.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AppM011: CommandLineRunner{

    @Autowired
    private lateinit var userService: UserService

    override fun run(vararg args: String?) {
        
        val user1 = User("Alice")
        userService.save(user1)

        val user2 = User("Bob")
        userService.save(user2)

        val users = userService.findAll()
        users.forEach {
            println(it)
        }

        println(userService.findAlice())

        println(userService.findBob())
    }
}

fun main() {
    runApplication<AppM011>()
}