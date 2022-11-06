package com.example.services

import com.example.model.User
import com.example.model.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Service
class UserService(val repository: UserRepository) {

    fun save(user: User) {
        repository.save(user)
    }

    fun findAll(): Collection<User> {
        return repository.findAll()
    }

    fun findAlice(): User {
        return repository.findByName("Alice")
    }

    fun findBob(): User {
        return repository.customQuery("Bob")
    }
}