package it.schwarz.dbtesting.controllers

import it.schwarz.dbtesting.models.QueryModel
import it.schwarz.dbtesting.repositories.QueryRepository
import it.schwarz.dbtesting.services.RequestDataService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

//@RestController
//@RequestMapping("/")
class QueryController(queryRepos: QueryRepository) {

    private val queryRepos: QueryRepository = queryRepos

    @GetMapping("query")
    fun getQueries(): ResponseEntity<String> {
        val request = RequestDataService(queryRepos)
        //request.getDataByInput(request.getUserInput())
        //request.getTestData()
        //request.someMethodName()
        return ResponseEntity.ok(request.getAllQueries().toString())
    }

    @GetMapping("query/{input}")
    fun addQuery(@PathVariable input: String): ResponseEntity<List<QueryModel>>{
        val request = RequestDataService(queryRepos)
        val query = QueryModel()
        query.query = input
        queryRepos.save(query)
        return ResponseEntity.ok(request.getAllQueries())
    }
}