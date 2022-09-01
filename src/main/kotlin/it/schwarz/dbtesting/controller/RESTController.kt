package it.schwarz.dbtesting.controller

import it.schwarz.dbtesting.models.DocumentModel
import it.schwarz.dbtesting.services.RequestDataService
import org.bson.Document
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class RESTController(private val request: RequestDataService) {

    @GetMapping("getAll")
    fun getAllQueries(): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.getDataByQuery(arrayListOf<Document>()))
    }

    @GetMapping("get/{id}")
    fun getQueryById(@PathVariable id: String): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.getDataByQuery(listOf(Document("\$match", Document("_id", 1)))))
    }

    @PostMapping("post")
    fun addQuery(@RequestBody docModel: DocumentModel): ResponseEntity<HttpStatus> {
        request.createMultipleQueries(docModel.payload)
        return ResponseEntity.ok(HttpStatus.CREATED)
    }

    //TODO USE PUT AS EDIT
    @PutMapping("put")
    fun editQuery(@RequestBody docModel: DocumentModel): ResponseEntity<HttpStatus> {
        request.getDataByQuery(docModel.payload)
        return ResponseEntity.ok(HttpStatus.ACCEPTED)
    }

    @DeleteMapping("delete/{id}")
    fun deleteQueryById(@PathVariable id: Int): ResponseEntity<HttpStatus> {
        request.deleteQueryById(id)
        return ResponseEntity.ok(HttpStatus.ACCEPTED)
    }
}