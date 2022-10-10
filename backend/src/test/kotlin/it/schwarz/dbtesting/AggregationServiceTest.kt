package it.schwarz.dbtesting

import it.schwarz.dbtesting.services.AggregationService
import org.bson.Document
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AggregationServiceTest(@Autowired private val aggregationService: AggregationService) {

    @Test
    fun aggregationTest() {
        aggregationService.collection.deleteMany(Document())
        val oldState = listOf<Document>()
        val newState = readAsDocumentModel("/assets/testCases/aggregate.json")
        val want = readAsDocuments("/assets/testCases/aggregate-want.json")[0]["changes"] as List<Document>

        aggregationService.aggregate(oldState, newState.replace!!)

        assertEquals(mapToListOfDocument(want), aggregationService.collection.find(Document()).toList()[0]["changes"] as List<Document>)
    }

    private fun mapToListOfDocument(want: List<Document>): List<Document>{
        val list = mutableListOf<Document>()
        for(i in want.indices) {
            want[i].mapKeys { list.add(Document(it.key, it.value.toString())) }
        }
        return list
    }
}