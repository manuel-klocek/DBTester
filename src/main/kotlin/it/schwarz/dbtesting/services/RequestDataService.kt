package it.schwarz.dbtesting.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.MongoBulkWriteException
import com.mongodb.MongoCommandException
import com.mongodb.client.MongoCollection
import it.schwarz.dbtesting.configs.MongoConfig
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
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

    fun deleteSingleEntry(got: List<Document>): Boolean {
        try {
            db.findOneAndDelete(got[0])
        } catch(ex: MongoCommandException) {
            println("Please use the element itself! Query is not supported within this function")
        }
        return true
    }

    fun createMultipleEntries(got: List<Document>): Boolean {
        try {
            db.insertMany(got)
        } catch (ex: MongoBulkWriteException) {
            println("Item with same Id already exists!")
            return false
        }
        return true
    }

    fun updateSingleEntry(got: List<Document>, want: List<Document>): Boolean {
        return db.findOneAndReplace(got[0], want[0]) !== null
    }

    fun checkForEntryInDB(got: List<Document>): Boolean {
        return !db.find(got[0]).equals("[]")
    }
}