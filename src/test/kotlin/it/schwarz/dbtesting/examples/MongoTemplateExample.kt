package it.schwarz.dbtesting.examples

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import org.bson.Document
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query



@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoTemplateExample {

    private lateinit var mongoTemplate: MongoTemplate

    val collectionName = "collection"

    @BeforeAll
    fun beforeAll() {
        //start the mongo container before: docker-compose -f
        val client = MongoClients.create("mongodb://localhost:27028")
        mongoTemplate = MongoTemplate(client, "database")

        val x = object {}.javaClass.getResource("fileName")?.readText() ?: ""
    }

    @AfterEach
    fun afterEach() {
        mongoTemplate.getCollection(collectionName).deleteMany(Filters.empty())
    }

    @Test
    fun `test case one`(){
        mongoTemplate.insert(
            Document("shape", "circle"),
            collectionName
        )

        val documents = mongoTemplate.find(Query(), Document::class.java, collectionName).toList()
        assertEquals("circle", documents.first().getString("shape"))
    }
}