package com.example.model

import javax.persistence.*

@Entity
@Table(name = "phones")
class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    var number: String = ""

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = ForeignKey(name = "USER_ID_FK"))
    private var user: User? = null

    private constructor()

    constructor(user: User, number: String) {
        this.user = user
        this.number = number
    }

    override fun toString(): String {
        return "Phone(id=$id, number='$number', user=$user)"
    }


}