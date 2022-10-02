package com.example

import com.example.model.User
import com.example.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AppM011: CommandLineRunner{

    @Autowired
    private lateinit var repo: UserRepository

    override fun run(vararg args: String?) {
        
        val user1 = User("Alice")
        repo.save(user1)

        val user2 = User("Bob")
        repo.save(user2)

        val users = repo.findAll()
        users.forEach {
            println(it)
        }
    }
}

fun main() {
    runApplication<AppM011>()
}