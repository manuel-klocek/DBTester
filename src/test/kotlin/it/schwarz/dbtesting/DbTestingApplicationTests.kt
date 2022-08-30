package it.schwarz.dbtesting

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DbTestingApplicationTests(@Autowired private val start: StartClass) {

	@Test
	fun workingCheck() {
		assertTrue(start.startApplication())
	}
}
