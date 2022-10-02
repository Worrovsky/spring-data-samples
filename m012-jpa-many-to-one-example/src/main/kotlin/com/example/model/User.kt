package com.example.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    private var name: String = ""

    private constructor() {

    }

    constructor(name: String) {
        this.name = name
    }

    override fun toString(): String {
        return "User(id=$id, name='$name')"
    }


}