package it.schwarz.dbtesting

import com.mongodb.assertions.Assertions.assertTrue
import it.schwarz.dbtesting.services.RequestDataService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestServiceTest(private val request: RequestDataService) {

    @Test
    fun testIfListFromJsonContainsElements() {
        assertTrue(request.getExpectedOutput().isNotEmpty())
    }
}