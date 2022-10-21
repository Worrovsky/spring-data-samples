package com.example

import com.example.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.LocalDateTime

@SpringBootApplication
class AppM12: CommandLineRunner{

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var phoneRepository: PhoneRepository

    override fun run(vararg args: String?) {

        val user = User("Alice", Gender.FEMALE, LocalDateTime.now())
        userRepository.save(user)

//        val phone1 = Phone(user, "555-666")
//        phoneRepository.save(phone1)

//        println(phone1)
        println(user)

        user.name = "Alice The great"


        val user2 = userRepository.findById(1L).get()
        println(user2)
//
//        val user3 = userRepository.findById(1L).get()
//        println(user3)
    }
}
 
fun main() {
    runApplication<AppM12>()
}