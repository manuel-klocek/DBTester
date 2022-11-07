package it.schwarz.dbtesting.controller

import it.schwarz.dbtesting.models.DocumentModel
import it.schwarz.dbtesting.models.TestModel
import it.schwarz.dbtesting.services.AggregationService
import it.schwarz.dbtesting.services.DifferService
import it.schwarz.dbtesting.services.PersistenceService
import it.schwarz.dbtesting.services.equals
import org.bson.Document
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/")
class RESTController(
    private val request: PersistenceService,
    private val difference: DifferService,
    private val aggregate: AggregationService
) {

    @GetMapping("getAll")
    fun getAllEntries(): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.read())
    }

    @GetMapping("get/{id}")
    fun getEntriesById(@PathVariable id: Int): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.read(listOf(Document("\$match", Document("_id", id)))))
    }

    @PostMapping("getByQuery")
    fun getEntriesByQuery(@RequestBody docModel: DocumentModel): ResponseEntity<List<Document>> {
        return ResponseEntity.ok(request.read(docModel.payload))
    }

    @PostMapping("test")
    fun testQuery(@RequestBody testModel: TestModel): ResponseEntity<List<List<Document>>> {
        val got = request.read(testModel.query)
        val want = testModel.want
        var diff = listOf<List<Document>>()
        if(!equals(got, want)) diff = difference.get(want, got)
        return ResponseEntity.ok(diff)
    }

    @PostMapping("create")
    fun addMultipleEntries(@RequestBody docModel: DocumentModel): ResponseEntity<String> {
        if(!request.createMultipleEntries(docModel.payload)) return ResponseEntity.ok("Item with same Id already exists!")
        return ResponseEntity.ok(HttpStatus.CREATED.toString())
    }

    //TO DELETE A PROPERTY YOU NEED TO WRITE <DELETE!> *in caps without <>*
    @PutMapping("edit")
    fun editExistingEntry(@RequestBody docModel: DocumentModel): ResponseEntity<String> {
        if(request.checkForEntryInDB(docModel.payload)) {
            val currentState = request.collection.find(docModel.payload[0]).toList()
            request.updateSingleEntry(docModel.payload, docModel.replace!!)
            aggregate.aggregate(currentState, docModel.replace!!)
        } else {
            return ResponseEntity.ok("Item does not exist in DB or multiple Objects got found!")
        }
        return ResponseEntity.ok(HttpStatus.ACCEPTED.toString())
    }

    @DeleteMapping("delete")
    fun deleteQueryById(@RequestBody docModel: DocumentModel): ResponseEntity<HttpStatus> {
        request.deleteSingleEntry(docModel.payload)
        return ResponseEntity.ok(HttpStatus.ACCEPTED)
    }
}