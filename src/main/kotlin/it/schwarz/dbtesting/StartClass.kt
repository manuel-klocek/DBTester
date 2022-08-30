package it.schwarz.dbtesting

import it.schwarz.dbtesting.services.RequestDataService
import it.schwarz.dbtesting.services.TestService
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class StartClass (val request: RequestDataService, val testService: TestService) {

    @PostConstruct
    fun startApplication(): Boolean {
        val got = request.getDataByQuery()
        val want = request.getExpectedOutput()
        return testService.compare(got, want)
    }
}