package com.example.beans

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

// NOTE: class and ALL public method should be open for proxying
//      or use @Component or @Transactional on class,
//      in order to all-open plugin makes open its
open class CountryService(private val countriesDao: CountriesDao) {

    open fun create() {
        countriesDao.createTable()
    }

    open fun insert(id: Int, name: String) {
        countriesDao.insert(id, name)
    }

    open fun show() {
        countriesDao.printCountries()
    }

    open fun insertOneAndTrowException(id: Int, name: String) {
        println("\ninsert ($id, $name)")
        countriesDao.insert(id, name)
        throw IllegalArgumentException("after insert throw exception, but there is no explicit transaction")
    }

    @Transactional
    open fun insertOneAndTrowExceptionInTransaction(id: Int, name: String) {
        println("\ninsert ($id, $name)")
        countriesDao.insert(id, name)
        throw IllegalArgumentException("after insert throw exception. There is explicit transaction")
    }

}