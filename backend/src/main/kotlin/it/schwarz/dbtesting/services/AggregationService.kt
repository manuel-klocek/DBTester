package it.schwarz.dbtesting.services

import com.mongodb.client.MongoClients
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class AggregationService(private val difference: DifferService) {

    private val collection = MongoTemplate(
        MongoClients.create("mongodb://localhost:27017"),
        "AggregationDB"
    ).getCollection("AggregationCollection")


    fun aggregate(old: List<Document>, new: List<Document>) {
        collection.deleteMany(Document())

        val differences = difference.get(new, old)[0]
        val changes = mutableListOf<Document>()

        for(difference in differences) {
            val key = difference.keys.toList()[0]
            val change = Document()
            val subDoc: Document = difference[key] as Document
            change["old"] = subDoc["got"]
            change["new"] = subDoc["expected"]
            changes.add(Document(key, change))
        }

        val idDoc = Document()
        idDoc["_id"] = old[0].getInteger("_id")
        idDoc["timestamp"] = Date().time

        val doc = Document()
        doc["_id"] = idDoc
        doc["changes"] = changes

        println(doc)

        collection.insertOne(doc)
    }
}