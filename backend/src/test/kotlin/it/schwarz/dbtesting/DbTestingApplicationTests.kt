package it.schwarz.dbtesting

import it.schwarz.dbtesting.services.PersistenceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DbTestingApplicationTests(
	@Autowired private val persistenceService: PersistenceService
) {

	@BeforeEach
	fun beforeEach(){
		persistenceService.deleteAll()
	}

	@Test
	fun something() {
		val given = readAsDocuments("/assets/given.json")
		persistenceService.insertMany(given)

		val query = readAsDocumentModel("/assets/query.json").payload
		val got = persistenceService.read(query)
		val want = persistenceService.getWant()
		assertEquals(want, got)
	}

}