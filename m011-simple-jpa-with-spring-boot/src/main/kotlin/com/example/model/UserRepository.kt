package com.example.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.Repository

interface UserRepository: Repository<User, Long> {

    fun save(user: User)

    fun findAll(): Collection<User>
}