package it.schwarz.dbtesting.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.MongoBulkWriteException
import com.mongodb.MongoCommandException
import it.schwarz.dbtesting.configs.MongoConfig
import org.bson.Document
import org.springframework.stereotype.Service
import java.io.File

const val collectionName = "DBTesting"

@Service
class PersistenceService(mongoConfig: MongoConfig) {

    val collection = mongoConfig.getMongoTemplate().db.getCollection(collectionName)

    fun getWant(): List<Document> {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(File("./src/main/resources/assets/want.json").readText())
    }

    fun read(query: List<Document> = listOf()): List<Document> {
        return collection.aggregate(query).toList()
    }

    fun deleteSingleEntry(got: List<Document>): Boolean {
        try {
            collection.findOneAndDelete(got[0])
        } catch (ex: MongoCommandException) {
            println("Please use the element itself! Query is not supported within this function")
        }
        return true
    }

    fun createMultipleEntries(got: List<Document>): Boolean {
        try {
            collection.insertMany(got)
        } catch (ex: MongoBulkWriteException) {
            println("Item with same Id already exists!")
            return false
        }
        return true
    }

    fun updateSingleEntry(got: List<Document>, want: List<Document>): Boolean {
        return collection.findOneAndReplace(got[0], want[0]) !== null
    }

    fun checkForEntryInDB(got: List<Document>): Boolean {
        return !collection.find(got[0]).equals("[]")
    }
}