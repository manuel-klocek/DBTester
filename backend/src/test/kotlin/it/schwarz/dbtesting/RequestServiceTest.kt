package it.schwarz.dbtesting

import com.mongodb.assertions.Assertions.assertTrue
import it.schwarz.dbtesting.configs.MongoConfig
import it.schwarz.dbtesting.services.PersistenceService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestServiceTest {

    private val persistenceService = PersistenceService(MongoConfig())

    @Test
    fun testIfListFromJsonContainsElements() {
        assertTrue(persistenceService.getWant().isNotEmpty())
    }
}