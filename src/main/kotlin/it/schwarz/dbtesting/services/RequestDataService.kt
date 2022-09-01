package it.schwarz.dbtesting.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.client.MongoCollection
import it.schwarz.dbtesting.configs.MongoConfig
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct


private const val COLLECTION_NAME = "DBTesting"
private lateinit var db: MongoCollection<Document>
@Service
class RequestDataService(private val mongoConf: MongoConfig) {

    private lateinit var mongo: MongoTemplate
    @PostConstruct
    fun setMongoTemp(){
        mongo = mongoConf.getMongoTemplate()
        db = mongo.db.getCollection(COLLECTION_NAME)
    }

    fun getExpectedOutput(): List<Document> {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(File("./src/main/resources/assets/want.json").readText())
    }

    fun getDataByQuery(query: List<Document> = listOf()): List<Document> {
        return db.aggregate(query).toList()
    }

    fun deleteQueryById(id: Int) {
        db.deleteOne(Document("_id", 1))
    }

    fun createMultipleQueries(payload: List<Document>): ResponseEntity<HttpStatus> {
        db.insertMany(payload)
        return ResponseEntity.ok(HttpStatus.CREATED)
    }
}