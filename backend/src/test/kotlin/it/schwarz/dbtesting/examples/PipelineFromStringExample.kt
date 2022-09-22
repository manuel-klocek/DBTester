package it.schwarz.dbtesting.examples

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import it.schwarz.dbtesting.readFile
import org.bson.Document
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

    private val folderName = "pipelineFromStringExample"

    @BeforeEach
    fun beforeEach(){
        collection.deleteMany(Filters.empty())
    }

    @Test
    fun `deserialize string to array of documents`() {

        val given = readAsDocuments("/$folderName/given.json")
        collection.insertMany(given)

        val query = readAsDocuments("/$folderName/query.json")
        val got = collection.aggregate(query).toList()

        val want = readAsDocuments("/$folderName/want.json")
        assertEquals(got, want)
    }

    private fun readAsDocuments(fileName: String): List<Document> {
        return jacksonObjectMapper().readValue(
            readFile(fileName)
        )
    }

}