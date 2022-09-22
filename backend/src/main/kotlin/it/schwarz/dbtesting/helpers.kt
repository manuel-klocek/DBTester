package it.schwarz.dbtesting

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.schwarz.dbtesting.models.DocumentModel
import org.bson.Document

fun readFile(relativePath: String): String {
    return {}.javaClass.getResource(relativePath)!!.readText()
}

fun readAsDocuments(relativePath: String): List<Document> {
    val string = readFile(relativePath)
    return jacksonObjectMapper().readValue(string)
}

fun readAsDocumentModel(relativePath: String): DocumentModel {
    val string = readFile(relativePath)
    return jacksonObjectMapper().readValue(string)
}