package it.schwarz.dbtesting

import it.schwarz.dbtesting.services.PersistenceService
import it.schwarz.dbtesting.services.equals
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class StartClass (val request: PersistenceService) {

    @PostConstruct
    fun startApplication(): Boolean {
        val got = request.read()
        val want = request.getWant()
        return equals(got, want)
    }
}