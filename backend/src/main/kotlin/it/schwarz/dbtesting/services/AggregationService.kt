package it.schwarz.dbtesting.services

import com.mongodb.client.MongoClients
import it.schwarz.dbtesting.getSubDocument
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
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

        if (!checkIfChangeAlreadyExists(changes)) collection.insertOne(doc)
    }

    private fun checkIfChangeAlreadyExists(changes: List<Document>): Boolean {
        return collection.find(Document("changes", changes)).toList().isNotEmpty()
    }
}