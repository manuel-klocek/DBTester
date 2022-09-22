package it.schwarz.dbtesting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DbTestingApplication

fun main(args: Array<String>) {
    runApplication<DbTestingApplication>(*args)
}
