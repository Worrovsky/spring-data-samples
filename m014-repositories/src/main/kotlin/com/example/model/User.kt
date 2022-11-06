package com.example.model

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "users")
class User: Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String = ""

    private constructor() {
        // required by JPA spec
    }

    constructor(name: String) {
        this.name = name
    }

    override fun toString(): String {
        return "User(id=$id, name='$name')"
    }

}