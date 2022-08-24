package it.schwarz.dbtesting.services

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import it.schwarz.dbtesting.models.QueryModel
import it.schwarz.dbtesting.repositories.QueryRepository
import org.bson.BsonDocument
import org.bson.json.JsonObject
import org.springframework.stereotype.Service
import java.io.File

@Service
class RequestDataService(private val queryRepos: QueryRepository) {

    fun getExpectedOutput(): String {
        val mapper = jacksonObjectMapper()
        mapper.registerKotlinModule()
        mapper.registerModule(JavaTimeModule())

        val jsonString: String = File("./src/main/resources/assets/MockData.json").readText(Charsets.UTF_8)
        val test = BsonDocument(jsonString)
        val bson = JsonObject(jsonString).toBsonDocument()
        var queryModel = QueryModel()
        queryModel.query = bson["name"]?.asString()!!.value
        println(queryModel.query)
        return ""
    }

    fun getDataByInput(userQueryInput: String): List<QueryModel> {
        val queryList: List<QueryModel> = queryRepos.findAll()
        return queryList
    }

    fun getUserInput(): String {
        print("Enter query: ")
        val userInput: String? = readLine()
        return userInput ?: "No Input!"
    }

    fun getAllQueries(): List<QueryModel> {
        return queryRepos.findAll()
    }
}