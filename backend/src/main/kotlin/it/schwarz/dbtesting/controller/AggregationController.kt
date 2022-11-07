package it.schwarz.dbtesting.controller

import it.schwarz.dbtesting.services.AggregationService
import org.bson.Document
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("/aggregation/")
class AggregationController(private val aggregation: AggregationService) {

    @GetMapping("id={id}")
    fun getById(@PathVariable id: Int): ResponseEntity<List<Document>> {
        val aggregations = aggregation.collection.find(Document("_id._id", id)).toList()
        return ResponseEntity.ok(aggregation.layout(aggregations))
    }
}