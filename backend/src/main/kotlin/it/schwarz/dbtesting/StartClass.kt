package it.schwarz.dbtesting

import it.schwarz.dbtesting.services.PersistenceService
import it.schwarz.dbtesting.services.equals
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class StartClass(val persistenceService: PersistenceService) {

    @PostConstruct
    fun startApplication(): Boolean {
        val got = persistenceService.read()
        val want = persistenceService.getWant()
        return equals(got, want)
    }
}