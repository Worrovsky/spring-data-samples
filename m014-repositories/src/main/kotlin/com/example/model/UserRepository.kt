package com.example.model

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface UserRepository: Repository<User, Long> {

    fun save(user: User)

    fun findAll(): Collection<User>

    fun findByName(name: String): User

    @Query("select u from User u where u.name = ?1")
    fun customQuery(name: String): User
}