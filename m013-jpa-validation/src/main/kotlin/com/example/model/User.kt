package com.example.model

import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "users")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    @Column(name = "name", unique = true, length = 42, insertable = false)
//    @NotNull
    @Size(min = 3)
    var name: String = ""

    private var createdAt: LocalDateTime? = null

    private constructor() {

    }

    constructor(name: String, createdAt: LocalDateTime) {
        this.name = name
        this.createdAt = createdAt
    }

    override fun toString(): String {
        return "User(id=$id, name='$name', createdAt=$createdAt)"
    }


}