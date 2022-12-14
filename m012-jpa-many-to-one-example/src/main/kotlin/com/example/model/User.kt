package com.example.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    @Column(name = "name", unique = true)
    var name: String = ""

    private var createdAt: LocalDateTime? = null

    @Enumerated(value = EnumType.STRING)
    private var gender: Gender? = null

    private constructor() {

    }

    constructor(name: String, gender: Gender, createdAt: LocalDateTime) {
        this.name = name
        this.createdAt = createdAt
        this.gender = gender
    }

    override fun toString(): String {
        return "User(id=$id, name='$name', createdAt=$createdAt)"
    }


}