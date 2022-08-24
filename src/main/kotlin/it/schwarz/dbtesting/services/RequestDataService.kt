package it.schwarz.dbtesting.services

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import it.schwarz.dbtesting.models.QueryModel
import it.schwarz.dbtesting.repositories.QueryRepository
import org.bson.Document
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
        val jsonArr = getJsonArrayFromJsonString(jsonString)
        var queryList = getQueryFromEveryArray(jsonArr)
        println(queryList[0].query)
        println(queryList[1].query)
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

    fun getJsonArrayFromJsonString(jsonString: String): List<String> {
        var string= jsonString.replace("[", "").replace("]", "")
        var stringList = string.split("},").toMutableList()

        for(i in 0 until stringList.size - 1) {
            stringList[i] += "}"
        }
        return stringList
    }

    fun getDocForEveryQuery(jsonArr: List<String>): List<Document> {
        var docList: MutableList<Document> = arrayListOf()
        for(item in jsonArr) {
            docList.add(Document.parse(item))
        }
        return docList
    }

    fun getQueryFromEveryArray(jsonArr: List<String>): List<QueryModel> {
        var docList = getDocForEveryQuery(jsonArr)
        var queryList: MutableList<QueryModel> = arrayListOf()
        var queryModel = QueryModel()
        var f = 0
        for (item in docList) {
            queryModel.query = item.getString("query")
            queryList.add(queryModel)
            println(queryList[f].query)
            f++
        }
        return queryList
    }
}