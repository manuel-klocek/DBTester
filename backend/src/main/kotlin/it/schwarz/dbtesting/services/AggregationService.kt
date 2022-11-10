package it.schwarz.dbtesting.services

import com.mongodb.client.MongoClients
import it.schwarz.dbtesting.getSubDocument
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
@Suppress("UNCHECKED_CAST")
class AggregationService(private val difference: DifferService) {

    val collection = MongoTemplate(
        MongoClients.create("mongodb://localhost:27017"),
        "AggregationDB"
    )
        .getCollection("AggregationCollection")

    fun set(old: Document = Document(), new: Document): Boolean {
        if(new.isEmpty()) return false

        val editedOld = Document()
        old.forEach {
            if(it.key != "_id") editedOld[it.key] = it.value
        }

        val differences = difference.get(listOf(new), listOf(editedOld), false).first()
        if (differences.isEmpty()) return false

        val changes = differences.map {
            val key = it.keys.first()
            val subDoc = getSubDocument(it, key)
            Document(key, subDoc["expected"])
        }

        val idDoc = Document(mapOf(
            "_id" to if(new.getInteger("_id") != null) new.getInteger("_id") else old.getInteger("_id"),
            "timestamp" to Date().time
        ))

        val doc = Document(mapOf(
            "_id" to idDoc,
            "changes" to changes
        ))

        if (!checkIfTimestampAlreadyExists(idDoc)) {
            collection.insertOne(doc)
            return true
        }
        return false
    }

    private fun checkIfTimestampAlreadyExists(idDoc: Document): Boolean {
        return collection.find(idDoc).toList().isNotEmpty()
    }


    fun get(aggregationsDocs: List<Document>): List<Document> {

        val aggregationList = mutableListOf<Document>()
        var keyList = mutableListOf<String>()

        for(document in aggregationsDocs){
            val aggregations: List<Document> = document["changes"] as List<Document>
            var newState = aggregations
            keyList = checkKeys(aggregations, keyList)
            if(!checkForAllKeys(aggregations, keyList)) newState = addPropertiesToElement(aggregations, keyList, aggregationsDocs)

            aggregationList.add(Document(mapOf(
                "new" to newState
            )))
        }
        return aggregationList
    }

    private fun checkKeys(aggregations: List<Document>, keyList: List<String>): MutableList<String> {
        val returnList: MutableList<String>
        val aggKeyList = mutableListOf<String>()
        aggregations.forEach { doc -> aggKeyList.add(doc.keys.toList()[0] as String) }
        if(keyList.isEmpty() && aggKeyList.isNotEmpty()) returnList = aggKeyList
        else {
            returnList = keyList as MutableList<String>
            for(key in aggKeyList){
                if(keyList.contains(key)) continue
                returnList.add(key)
            }
        }
        return returnList
    }

    private fun checkForAllKeys(docs: List<Document>, keys: List<String>): Boolean {
        return docs.size == keys.size
    }

    private fun addPropertiesToElement(docs: List<Document>, keys: List<String>, allAggregations: List<Document>): List<Document> {
        val returnList = mutableListOf<Document>()
        val keyList = mutableListOf<String>()
        val notContainedKeys = mutableListOf<String>()
        docs.forEach { doc -> keyList.add(doc.keys.toList()[0].toString())
            returnList.add(doc) }

        //Aggregation doesn't contain all alive keys used in the past
        keys.forEach { key -> if(!keyList.contains(key)) notContainedKeys.add(key) }
        //New Key that is unknown yet
        keyList.forEach { key -> if(!keys.contains(key) && !notContainedKeys.contains(key)) notContainedKeys.add(key) }

        for(key in notContainedKeys.reversed()) {
            returnList.add(Document(key, searchForLastValueOf(key, allAggregations)))
        }

        return returnList.reversed()
    }

    private fun searchForLastValueOf(key: String, allAggregations: List<Document>): String {
        var value = ""
        for(i in allAggregations.size - 1 downTo 0) {
            (allAggregations[i]["changes"] as List<Document>).forEach { doc -> if(doc.keys.contains(key)) value = doc.getString(key)}
            if(value.isNotEmpty()) break
        }
        return value
    }
}