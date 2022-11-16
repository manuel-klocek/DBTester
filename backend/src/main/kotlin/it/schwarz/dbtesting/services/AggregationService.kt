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
        MongoClients.create("mongodb://localhost:27028"),
        "AggregationDB"
    )
        .getCollection("AggregationCollection")

    fun set(old: Document = Document(), new: Document): Boolean {
        if (new.isEmpty()) return false

        val editedOld = Document()
        old.forEach {
            if (it.key != "_id") editedOld[it.key] = it.value
        }

        val differences = difference.get(listOf(editedOld), listOf(new), false).first()
        if (differences.isEmpty()) return false

        val changes = differences.map {
            val key = it.keys.first()
            val subDoc = getSubDocument(it, key)
            Document(key, subDoc["got"])
        }

        val idDoc = Document(
            mapOf(
                "_id" to if (new.getInteger("_id") != null) new.getInteger("_id") else old.getInteger("_id"),
                "timestamp" to Date().time
            )
        )

        val doc = Document(
            mapOf(
                "_id" to idDoc,
                "changes" to changes
            )
        )

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
        var aggregationList = mutableListOf<Document>()
        var keyList = mutableListOf<String>()
        val changes = mutableListOf<List<Document>>()
        val timestamps = mutableListOf<Long>()

        for (document in aggregationsDocs) {
            val aggregations: List<Document> = document["changes"] as List<Document>
            var newState = aggregations
            keyList = checkKeys(aggregations, keyList)
            changes.add(aggregations)
            timestamps.add(getSubDocument(document, "_id").getLong("timestamp"))
            if (!checkForAllKeys(aggregations, keyList)) newState =
                addPropertiesToElement(aggregations, keyList, aggregationsDocs)

            aggregationList.add(
                Document(
                    mapOf(
                        "new" to newState
                    )
                )
            )
        }

        aggregationList = fixOrder(aggregationList)
        aggregationList = addTimestamps(aggregationList, timestamps)
        aggregationList = addChangedKeys(aggregationList, changes)

        return aggregationList
    }

    private fun checkKeys(aggregations: List<Document>, keyList: List<String>): MutableList<String> {
        val returnList: MutableList<String>
        val aggKeyList = mutableListOf<String>()
        aggregations.forEach { doc -> aggKeyList.add(doc.keys.toList()[0] as String) }
        if (keyList.isEmpty() && aggKeyList.isNotEmpty()) returnList = aggKeyList
        else {
            returnList = keyList as MutableList<String>
            for (key in aggKeyList) {
                if (keyList.contains(key)) continue
                returnList.add(key)
            }
        }
        return returnList
    }

    private fun checkForAllKeys(docs: List<Document>, keys: List<String>): Boolean {
        return docs.size == keys.size
    }

    private fun addPropertiesToElement(
        docs: List<Document>,
        keys: List<String>,
        allAggregations: List<Document>
    ): List<Document> {
        val returnList = mutableListOf<Document>()
        val keyList = mutableListOf<String>()
        val notContainedKeys = mutableListOf<String>()
        docs.reversed().forEach {
            keyList.add(it.keys.first().toString())
            returnList.add(it)
        }

        //Aggregation doesn't contain all alive keys used in the past
        keys.forEach { if (!keyList.contains(it)) notContainedKeys.add(it) }
        //New Key that is unknown yet
        keyList.forEach { if (!keys.contains(it) && !notContainedKeys.contains(it)) notContainedKeys.add(it) }

        for (key in notContainedKeys) {
            returnList.add(Document(key, searchForLastValueOf(key, allAggregations)))
        }

        return returnList
    }

    private fun searchForLastValueOf(key: String, allAggregations: List<Document>): Any? {
        allAggregations.reversed().forEach { aggregation ->
            (aggregation["changes"] as List<Document>).forEach {
                if (it.keys.contains(key)) return it.getValue(key)
            }
        }
        return null
    }

    private fun fixOrder(aggregations: List<Document>): MutableList<Document> {
        val keyList = mutableListOf<String>()
        val changes = mutableListOf<List<Document>>()
        aggregations.forEach { aggregation ->
            changes.add(aggregation["new"] as List<Document>)
        }

        //adds all keys to keyList
        changes.forEach { change ->
            change.forEach { doc ->
                doc.forEach {
                    if (!keyList.contains(it.key)) keyList.add(it.key)
                }
            }
        }

        val returnList = mutableListOf<Document>()
        for (change in changes) {
            val itemList = mutableListOf<Document>()
            for (key in keyList) {
                for (doc in change) {
                    if(doc.keys.first() == key) {
                        itemList.add(doc)
                        continue
                    }
                }
            }
            returnList.add(Document("new", itemList))
        }
        return returnList
    }

    fun addTimestamps(aggregations: List<Document>, timestamps: List<Long>): MutableList<Document> {
        val aggregationList = mutableListOf<Document>()
        for((index, element) in aggregations.withIndex()) {
            element["timestamp"] = timestamps[index]
            aggregationList.add(element)
        }
        return aggregationList
    }

    private fun addChangedKeys(list: List<Document>, changesList: List<List<Document>>): MutableList<Document> {
        val aggregationList = mutableListOf<Document>()

        val changeKeys = mutableListOf<List<String>>()
        for(changes in changesList) {
            val keys = mutableListOf<String>()
            for(change in changes){
                keys.add(change.keys.first())
            }
            changeKeys.add(keys)
        }

        for((index, change) in list.withIndex()) {
            change["changedKeys"] = changeKeys[index]
            aggregationList.add(change)
        }
        return aggregationList
    }
}