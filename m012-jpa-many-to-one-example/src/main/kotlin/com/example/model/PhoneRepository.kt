package com.example.model

import org.springframework.data.jpa.repository.JpaRepository

interface PhoneRepository: JpaRepository<Phone, Long> {

    fun save(phone: Phone)

}