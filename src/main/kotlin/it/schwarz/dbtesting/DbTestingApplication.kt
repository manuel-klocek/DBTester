package it.schwarz.dbtesting

import it.schwarz.dbtesting.services.RequestDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.annotation.PostConstruct

@SpringBootApplication
class DbTestingApplication

	fun main(args: Array<String>) {
	 	runApplication<DbTestingApplication>(*args)
   	}

