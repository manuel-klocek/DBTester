package it.schwarz.dbtesting

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.bson.Document

fun readFile(relativePath: String): String {
    return {}.javaClass.getResource(relativePath)!!.readText()
}

fun readAsDocuments(fileName: String): List<Document> {
    return jacksonObjectMapper().readValue(
        readFile(fileName)
    )
}