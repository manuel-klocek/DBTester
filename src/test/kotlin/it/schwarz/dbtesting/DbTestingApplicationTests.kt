package it.schwarz.dbtesting

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.schwarz.dbtesting.models.DocumentModel
import it.schwarz.dbtesting.services.RequestDataService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DbTestingApplicationTests(@Autowired private val start: StartClass,
								@Autowired private val request: RequestDataService) {

	@Test
	fun workingCheck() {
		assertFalse(start.startApplication())
	}

	@Test
	fun something() {
		val got = request.getDataByQuery(jacksonObjectMapper().readValue<DocumentModel>(
			{}.javaClass.getResource("/assets/query.json")!!.readText()).payload)
		val want = request.getExpectedOutput()
		assertEquals(want, got)
	}
}