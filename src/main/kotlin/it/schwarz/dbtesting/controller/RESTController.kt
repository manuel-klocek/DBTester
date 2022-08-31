package it.schwarz.dbtesting.controller

import it.schwarz.dbtesting.services.TranslateService
import org.bson.Document
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class RESTController(private val translateService: TranslateService) {

    @PutMapping()
    fun addQuery(@RequestBody doc: List<Document>): ResponseEntity<String> {
        translateService.translateToQueryDoc(doc[0]["payload"].toString())
        return ResponseEntity.ok("Adding Query if not already in DB")
    }
}