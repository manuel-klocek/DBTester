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

    fun aggregate(old: List<Document>, new: List<Document>) {
        if(old.size > 1 || new.size != 1) return
        val differences = difference.get(new, old)[0]
        if (differences.isEmpty()) return

        val changes = mutableListOf<Document>()
        for (difference in differences) {
            val key = difference.keys.toList()[0]
            val subDoc: Document = getSubDocument(difference, key)
            changes.add(Document(key, subDoc["expected"]))
        }

        val idDoc = Document()
        idDoc["_id"] = new[0].getInteger("_id")
        idDoc["timestamp"] = Date().time

        val doc = Document()
        doc["_id"] = idDoc
        doc["changes"] = changes

        if (!checkIfChangeAlreadyExists(changes) &&
        !checkIfTimestampAlreadyExists(idDoc)) {
            collection.insertOne(doc)
        }
    }

    private fun checkIfChangeAlreadyExists(changes: List<Document>): Boolean {
        return collection.find(Document("changes", changes)).toList().isNotEmpty()
    }

    private fun checkIfTimestampAlreadyExists(idDoc: Document): Boolean {
        return collection.find(idDoc).toList().isNotEmpty()
    }


    fun layout(aggregationsDocs: List<Document>): List<Document> {

        val aggregationList = mutableListOf<Document>()
        val firstAggregation = aggregationsDocs[0]["changes"] as List<Document>
        var keyList = mutableListOf<String>()
        var oldState = firstAggregation

        //First implementation of an object
        firstAggregation.forEach { doc -> keyList.add(doc.keys.toList()[0].toString()) }
        aggregationList.add(Document("new", firstAggregation))

        for(i in 1 until aggregationsDocs.size){
            val aggregations: List<Document> = aggregationsDocs[i]["changes"] as List<Document>
            var newState = aggregations
            val doc = Document()
            keyList = checkKeys(aggregations, keyList)
            if(!checkForAllKeys(aggregations, keyList)) newState = addPropertiesToElement(aggregations, keyList, aggregationsDocs)
            doc["new"] = newState
            doc["old"] = oldState

            aggregationList.add(doc)
            oldState = newState
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