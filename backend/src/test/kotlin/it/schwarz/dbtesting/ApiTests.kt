package it.schwarz.dbtesting

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.schwarz.dbtesting.models.DocumentModel
import it.schwarz.dbtesting.services.PersistenceService
import org.bson.Document
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTests (@Autowired val persistenceService: PersistenceService) {

    val uri: String = "http://localhost:8080/"
    lateinit var client: HttpClient
    lateinit var builder: HttpRequest.Builder
    lateinit var given: List<Document>
    lateinit var query: DocumentModel

    @BeforeEach
    fun setup() {
        client = HttpClient.newHttpClient()
        builder = HttpRequest.newBuilder()

        given = readAsDocuments("/assets/given.json")
        query = readAsDocumentModel("/assets/query.json")
    }

    @Test
    fun getAllTest() {
        val request = builder
            .uri(URI(uri + "getAll"))
            .GET()
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        val got = mapResponse(response.body())

        assertEquals(given, got)
    }

    @Test
    fun getByIdTest() {
        val request = builder
            .uri(URI(uri + "get/2"))
            .GET()
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        val got = mapResponse(response.body())
        assertEquals(listOf(given[1]), got)
    }

    @Test
    fun getByQueryTest() {
        val request = builder
            .uri(URI(uri + "getByQuery"))
            .headers("Content-Type", "application/json")
            .POST(BodyPublishers.ofString({}.javaClass.getResource("/assets/query.json")!!.readText()))
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        val got = mapResponse(response.body())
        val want = readAsDocuments("/assets/want.json")

        assertEquals(want, got)
    }

    @Test
    fun testQueryTest() {
        val request = builder
            .uri(URI(uri + "test"))
            .headers("Content-Type", "application/json")
            .POST(BodyPublishers.ofString({}.javaClass.getResource("/assets/testCases/case.json")!!.readText()))
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        val got: List<List<Document>> = jacksonObjectMapper().readValue(response.body())
        val want: List<List<Document>> = jacksonObjectMapper().readValue({}.javaClass.getResource("/assets/testCases/case-want.json")!!.readText())

        assertEquals(want, got)
    }

    @Test
    fun addMultipleEntriesTest() {
        val request = builder
            .uri(URI(uri + "create"))
            .headers("Content-Type", "application/json")
            .POST(BodyPublishers.ofString({}.javaClass.getResource("/assets/testCases/create.json")!!.readText()))
            .build()

        val response = client.send(request, BodyHandlers.ofString())

        //delete inserted Entry to contain db state
        persistenceService.deleteSingleEntry(listOf(Document("_id", 4)))

        assertTrue(response.statusCode() == 200)
    }

    @Test
    fun editEntryTest() {
        //Setup
        val doc = Document()
        doc["_id"] = 1
        doc["name"] = "John Doe"
        persistenceService.updateSingleEntry(listOf(Document("name", "Marcus Aurelius")), listOf(doc))

        val request = builder
            .uri(URI(uri + "edit"))
            .headers("Content-Type", "application/json")
            .PUT(BodyPublishers.ofString({}.javaClass.getResource("/assets/testCases/edit.json")!!.readText()))
            .build()

        val failRequest = builder
            .uri(URI(uri + "edit"))
            .headers("Content-Type", "application/json")
            .PUT(BodyPublishers.ofString({}.javaClass.getResource("/assets/testCases/edit-fail.json")!!.readText()))
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        val failResponse = client.send(failRequest, BodyHandlers.ofString())

        assertTrue(response.body() == "202 ACCEPTED")
        assertTrue(failResponse.body() == "Item does not exist in DB or multiple Objects got found!")
        assertTrue(persistenceService.checkForEntryInDB(listOf(Document("name", "Marcus Aurelius"))))
    }

    @Test
    fun deleteEntryTest() {
        //Setup
        persistenceService.insertMany(listOf(Document("_id", 4)))

        val request = builder
            .uri(URI(uri + "delete"))
            .headers("Content-Type", "application/json")
            .method("DELETE", BodyPublishers.ofString({}.javaClass.getResource("/assets/testCases/delete.json")!!.readText()))
            .build()

        val response = client.send(request, BodyHandlers.ofString())

        assertTrue(response.statusCode() == 200)
        assertFalse(persistenceService.checkForEntryInDB(listOf(Document("_id", 4))))
    }


    private fun mapResponse(response: String): List<Document> {
        return jacksonObjectMapper().readValue(response)
    }
}