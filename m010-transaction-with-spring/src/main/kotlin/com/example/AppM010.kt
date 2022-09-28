package com.example

import com.example.beans.CountryService
import com.example.config.AppConfig
import org.springframework.beans.factory.getBean
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main() {

    val ctx = AnnotationConfigApplicationContext(AppConfig::class.java)
    ctx.registerShutdownHook()

    val service: CountryService = ctx.getBean<CountryService>()

    service.create()

    service.insert(1, "France")
    service.insert(2, "Germany")

    try {
        service.insertOneAndTrowException(3, "Poland")
    } catch (e: Exception) {
        println(e.message)
    }

    try {
        service.insertOneAndTrowExceptionInTransaction(4, "Denmark")
    } catch (e: Exception) {
        println(e.message)
    }
    service.show()

}