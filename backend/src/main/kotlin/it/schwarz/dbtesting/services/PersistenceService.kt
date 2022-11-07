package it.schwarz.dbtesting.services

import com.mongodb.MongoBulkWriteException
import com.mongodb.MongoCommandException
import com.mongodb.client.model.Filters
import it.schwarz.dbtesting.configs.MongoConfig
import it.schwarz.dbtesting.readAsDocuments
import org.bson.Document
import org.springframework.stereotype.Service

const val collectionName = "DBTesting"

@Service
class PersistenceService(mongoConfig: MongoConfig) {

    val collection = mongoConfig.getMongoTemplate().db.getCollection(collectionName)

    fun getWant(): List<Document> {
        return readAsDocuments("/assets/want.json")
    }

    fun insertMany(documents: List<Document>) {
        collection.insertMany(documents)
    }

    fun deleteAll() {
        collection.deleteMany(Filters.empty())
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
        val gotDocs = getEntryByProperties(got)
        if(gotDocs.size != 1) return false

        val gotKeys = mutableListOf<String>()
        val wantKeys = mutableListOf<String>()

        gotDocs.forEach { doc -> gotKeys.add(doc.keys.toList()[0].toString()) }
        want.forEach { doc -> wantKeys.add(doc.keys.toList()[0].toString()) }

        return collection.findOneAndReplace(got[0], checkForDeletionsAndRemove(want[0])[0]) !== null
    }

    fun checkForEntryInDB(got: List<Document>): Boolean {
        return collection.find(got[0]).toList().size == 1
    }

    private fun getEntryByProperties(got: List<Document>): List<Document> {
        return collection.find(got[0]).toList()
    }

    private fun checkForDeletionsAndRemove(want: Document): List<Document> {
        val doc = Document()
        for(prop in want){
            val key = prop.key
            val value = prop.value
            if(value != "DELETE!") {
                doc[key] = value
            }
        }
        return listOf(doc)
    }
}