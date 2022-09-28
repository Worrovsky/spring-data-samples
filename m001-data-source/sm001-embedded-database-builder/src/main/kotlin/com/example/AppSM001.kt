package com.example

import com.example.config.AppConfig
import org.springframework.beans.factory.getBean
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import javax.sql.DataSource

fun main() {

    val ctx = AnnotationConfigApplicationContext(AppConfig::class.java)
    ctx.registerShutdownHook()

    val dataSource = ctx.getBean<DataSource>()

    val conn = dataSource.connection
    println(conn.transactionIsolation)

    conn.close()

}