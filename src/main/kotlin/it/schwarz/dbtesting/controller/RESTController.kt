package it.schwarz.dbtesting.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.schwarz.dbtesting.models.DifferenceModel
import it.schwarz.dbtesting.models.DocumentModel
import it.schwarz.dbtesting.models.TestModel
import it.schwarz.dbtesting.services.DifferService
import it.schwarz.dbtesting.services.RequestDataService
import it.schwarz.dbtesting.services.TestService
import org.bson.Document
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/")
class RESTController(private val request: RequestDataService,
                     private val test: TestService,
                     private val differ: DifferService) {

    @GetMapping("getAll")
    fun getAllEntries(): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.getDataByQuery(arrayListOf()))
    }

    @GetMapping("get/{id}")
    fun getEntriesById(@PathVariable id: Int): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.getDataByQuery(listOf(Document("\$match", Document("_id", id)))))
    }

    @GetMapping("getByQuery")
    fun getEntriesByQuery(@RequestBody docModel: DocumentModel): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.getDataByQuery(docModel.payload))
    }

    //TODO Change Frontend to Content-Type Json not plain text
    @PostMapping("test")
    fun testQuery(@RequestBody testModelString: String): ResponseEntity<List<DifferenceModel>> {
        val testModel: TestModel = ObjectMapper().readValue(testModelString)
        val got = request.getDataByQuery(testModel.query)
        val want = testModel.want
        var difference = listOf<DifferenceModel>()
        if(!test.compare(got, want)) difference = differ.getDifference(got, want)
        return ResponseEntity.ok(difference)
    }

    @PostMapping("create")
    fun addMultipleEntries(@RequestBody docModel: DocumentModel): ResponseEntity<HttpStatus> {
        request.createMultipleEntries(docModel.payload)
        return ResponseEntity.ok(HttpStatus.CREATED)
    }

    @PutMapping("edit")
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