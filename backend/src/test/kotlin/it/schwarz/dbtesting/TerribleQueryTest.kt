package it.schwarz.dbtesting

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TerribleQueryTest {

    private val collection = MongoTemplate(
        MongoClients.create("mongodb://localhost:27017"),
        "TestDB"
    ).getCollection("TestCollection")

    private val folderName = "assets/terribleQueryCase"

    @BeforeEach
    fun beforeEach(){
        collection.deleteMany(Filters.empty())
    }

    //TODO how to update terrible-query
    @Test
    fun terribleQueryTest() {

        val given = readAsDocuments("/$folderName/terrible-query-given.json")
        collection.insertMany(given)

        val query = readAsDocuments("/$folderName/terrible-query.json")
        val got = collection.aggregate(query).toList()

        val want = readAsDocuments("/$folderName/terrible-query-want.json")
        assertNotEquals(want, got)
    }
}