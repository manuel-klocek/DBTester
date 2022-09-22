package it.schwarz.dbtesting

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.schwarz.dbtesting.models.DocumentModel
import it.schwarz.dbtesting.services.PersistenceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DbTestingApplicationTests(
	@Autowired private val start: StartClass,
	@Autowired private val persistenceService: PersistenceService
) {

	@Test
	fun workingCheck() {
		assertFalse(start.startApplication())
	}

	@Test
	fun something() {
		val query = jacksonObjectMapper().readValue<DocumentModel>(
			readFile("/assets/query.json")
		).payload
		val got = persistenceService.read(query)
		val want = persistenceService.getWant()
		assertEquals(want, got)
	}

}