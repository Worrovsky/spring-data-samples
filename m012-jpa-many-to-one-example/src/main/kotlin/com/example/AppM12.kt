package com.example

import com.example.model.Phone
import com.example.model.PhoneRepository
import com.example.model.User
import com.example.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AppM12: CommandLineRunner{

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var phoneRepository: PhoneRepository

    override fun run(vararg args: String?) {

        val user = User("Alice")
        userRepository.save(user)

        val phone = Phone(user, "555-666")
        phoneRepository.save(phone)

        println(phone)
        println(user)
    }
}
 
fun main() {
    runApplication<AppM12>()
}