package com.example

import com.example.config.AppConfig
import com.example.config.Dao
import org.springframework.beans.factory.getBean
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.sql.Connection
import javax.sql.DataSource

fun main() {

    val ctx = AnnotationConfigApplicationContext(AppConfig::class.java)
    ctx.registerShutdownHook()

    val dao = ctx.getBean<Dao>()
    dao.createTable()
    dao.printCountries("")

    val dataSource = ctx.getBean<DataSource>()


    dirtyReadExample(dataSource)

    nonrepeatableRead(dataSource, Connection.TRANSACTION_READ_UNCOMMITTED, "READ UNCOMMITTED")
    nonrepeatableRead(dataSource, Connection.TRANSACTION_READ_COMMITTED, "READ COMMITTED")
    nonrepeatableRead(dataSource, Connection.TRANSACTION_REPEATABLE_READ, "REPEATABLE READ")

    try {
        lostUpdate(dataSource)
    } catch (e: java.lang.Exception) {
        println(e.message)
    }

}

private fun dirtyReadExample(dataSource: DataSource) {

    println("\n\n============= DIRTY READ ============")

    clearCountries(dataSource)

    val con1 = dataSource.connection
    con1.autoCommit = false
    val stat1 = con1.createStatement()
    val insertSql = "INSERT INTO countries (id, name) VALUES (2, 'FRANCE');"
    println("execute '$insertSql' in T1 with any transaction level without commit")
    stat1.execute(insertSql)

    println("\nRead in T2 with READ UNCOMMITTED:")
    readTableWithTransactionLevel(dataSource, Connection.TRANSACTION_READ_UNCOMMITTED)

    println("\nRead in T2 with READ COMMITTED:")
    readTableWithTransactionLevel(dataSource, Connection.TRANSACTION_READ_COMMITTED)

    con1.commit()
    stat1.close()
    con1.close()
    println("\nT1 committed")

    println("\nRead in T2 with any transaction level")
    readTableWithTransactionLevel(dataSource, Connection.TRANSACTION_READ_UNCOMMITTED)
}

private fun nonrepeatableRead(dataSource: DataSource, level: Int, desc: String) {

    println("\n================= NONREPEATABLE READ ($desc) =================")

    clearCountries(dataSource)

    val con1 = dataSource.connection
    val stat1 = con1.createStatement()
    val insertSql = "INSERT INTO countries (id, name) VALUES (11, 'FRANCE');"
    stat1.execute(insertSql)
    stat1.close()
    con1.close()

    val con2 = dataSource.connection
    con2.autoCommit = false
    println("\nOpen T1 with $desc")
    con2.transactionIsolation = level
    println("\nRead in T1 before T2")
    readTableWithConnection(con2)

    val con3 = dataSource.connection
    val stat3 = con3.createStatement()
    stat3.execute("UPDATE countries SET NAME='New France' WHERE id=11;")
    stat3.close()
    con3.close()
    println("\nT2 change data, T1 still open.")

    println("\nRead in T1 after T2")
    readTableWithConnection(con2)
    con2.close()

}

private fun lostUpdate(dataSource: DataSource) {

    println("\n=================== LOST UPDATE ======================")
    clearCountries(dataSource)

    val t = dataSource.connection
    val s = t.createStatement()
    s.execute("INSERT INTO countries (id, name) VALUES (4, 'FRANCE');")

    val t1 = dataSource.connection
    t1.autoCommit = false
    val s1 = t1.createStatement()
    val sql1 = "UPDATE countries SET NAME='New France 1111' WHERE id=4;"
    s1.execute(sql1)
    println("\nexecute '$sql1'")

    val t2 = dataSource.connection
    t2.autoCommit = false
    val s2 = t2.createStatement()
    val sql2 = "UPDATE countries SET NAME='New France 2222' WHERE id=4;"
    println("\ntry execute '$sql2'")
    s2.execute(sql2)

    // this code unreachable
    s1.close()
    s2.close()

    println("\nTry commit, but DB cannot do this")
    t2.commit()
    t1.commit()
}

private fun clearCountries(dataSource: DataSource) {
    val connection = dataSource.connection
    val statement = connection.createStatement()
    statement.execute("delete from countries")
    statement.close()
    connection.close()
}

private fun readTableWithTransactionLevel(dataSource: DataSource, transactionLevel: Int) {
    val connection = dataSource.connection
    connection.transactionIsolation = transactionLevel
    val statement = connection.createStatement()
    statement.execute("select id, name from countries")
    val resultSet = statement.resultSet
    while (resultSet.next()) {
        val id = resultSet.getLong("id")
        val name = resultSet.getString("name")
        println("id: $id, name:  $name")
    }
    statement.close()
    connection.close()
}

private fun readTableWithConnection(connection: Connection) {
    val statement = connection.createStatement()
    statement.execute("select id, name from countries")
    val resultSet = statement.resultSet
    while (resultSet.next()) {
        val id = resultSet.getLong("id")
        val name = resultSet.getString("name")
        println("id: $id, name:  $name")
    }
    statement.close()
}