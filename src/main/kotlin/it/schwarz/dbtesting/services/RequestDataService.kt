package it.schwarz.dbtesting.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.schwarz.dbtesting.configs.MongoConfig
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct


private const val COLLECTION_NAME = "DBTesting"
@Service
class RequestDataService(private val mongoConf: MongoConfig) {

    private lateinit var mongo: MongoTemplate
    @PostConstruct
    fun setMongoTemp(){
        mongo = mongoConf.getMongoTemplate()
    }

    fun getExpectedOutput(): List<Document> {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(File("./src/main/resources/assets/want.json").readText())
    }

    fun getDataByQuery(): List<Document> {
        val test = listOf(
            Document(
                "\$match",
                Document("name", "Marcus Aurelius")
            )
        )
        println(test)
        return mongo.db.getCollection(COLLECTION_NAME).aggregate(test).toList()
    }
}