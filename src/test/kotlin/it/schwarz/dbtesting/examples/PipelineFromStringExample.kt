package it.schwarz.dbtesting.examples

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import org.bson.Document
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.data.mongodb.core.MongoTemplate


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PipelineFromStringExample {

    private val collection = MongoTemplate(
        MongoClients.create("mongodb://localhost:27028"),
        "database"
    ).getCollection("collection")

    @BeforeEach
    fun beforeEach(){
        collection.deleteMany(Filters.empty())
    }

    @Test
    fun `deserialize string to array of documents`() {
        val query = readAsDocuments("/assets/query.json")
        val given = readAsDocuments("/assets/given.json")

        collection.insertMany(given)
        val got = collection.aggregate(query).toList()
        val want = readAsDocuments("/assets/want.json")

        assertEquals(got, want)
    }

    private fun readAsDocuments(fileName: String): List<Document> {
        return jacksonObjectMapper().readValue(
            {}.javaClass.getResource(fileName)!!.readText()
        )
    }

}