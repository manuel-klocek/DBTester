package it.schwarz.dbtesting.controller

import it.schwarz.dbtesting.models.DocumentModel
import it.schwarz.dbtesting.models.TestModel
import it.schwarz.dbtesting.services.RequestDataService
import it.schwarz.dbtesting.services.TestService
import org.bson.Document
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class RESTController(private val request: RequestDataService, private val test: TestService) {

    @GetMapping("getAll")
    fun getAllEntries(): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.getDataByQuery(arrayListOf<Document>()))
    }

    @GetMapping("get/{id}")
    fun getEntriesById(@PathVariable id: Int): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.getDataByQuery(listOf(Document("\$match", Document("_id", id)))))
    }

    @GetMapping("getByQuery")
    fun getEntriesByQuery(@RequestBody docModel: DocumentModel): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.getDataByQuery(docModel.payload))
    }

    @GetMapping("test")
    fun testQuery(@RequestBody testModel: TestModel): ResponseEntity<String> {
        val got = request.getDataByQuery(testModel.query)
        val want = testModel.want
        return ResponseEntity.ok(test.compare(got, want).toString())
    }

    @PostMapping("post")
    fun addMultipleEntries(@RequestBody docModel: DocumentModel): ResponseEntity<HttpStatus> {
        request.createMultipleEntries(docModel.payload)
        return ResponseEntity.ok(HttpStatus.CREATED)
    }

    @PutMapping("put")
    fun editExistingEntry(@RequestBody docModel: DocumentModel): ResponseEntity<HttpStatus> {
        if(request.checkForEntryInDB(docModel.payload)) {
            request.updateSingleEntry(docModel.payload, docModel.replace!!)
        } else {
            println("Item does not exist in DB. Use PostMethod (/post) instead")
        }
        return ResponseEntity.ok(HttpStatus.ACCEPTED)
    }

    @DeleteMapping("delete")
    fun deleteQueryById(@RequestBody docModel: DocumentModel): ResponseEntity<HttpStatus> {
        request.deleteSingleEntry(docModel.payload)
        return ResponseEntity.ok(HttpStatus.ACCEPTED)
    }
}