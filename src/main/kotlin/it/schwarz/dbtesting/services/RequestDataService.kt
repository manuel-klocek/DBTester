package it.schwarz.dbtesting.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import it.schwarz.dbtesting.configs.MongoConfig
import it.schwarz.dbtesting.models.QueryModel
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct


@Service
class RequestDataService(private val mongoConf: MongoConfig) {

    private lateinit var mongo: MongoTemplate
    @PostConstruct
    fun setMongoTemp(){
        mongo = mongoConf.mongoTemplate()
    }

    fun getExpectedOutput(): List<QueryModel> {
        return getListOfQueries()
    }

    private fun getListOfQueries(): MutableList<QueryModel> {
        return mapJsonNodeToQueryModel(getJsonNode())
    }

    private fun getJsonNode(): JsonNode {
        val mapper = jacksonObjectMapper()
        mapper.registerKotlinModule()
        mapper.registerModule(JavaTimeModule())
        return mapper.readTree(File("./src/main/resources/assets/MockData.json"))
    }

    private fun mapJsonNodeToQueryModel(json: JsonNode): MutableList<QueryModel> {
        val queryModelList: MutableList<QueryModel> = arrayListOf()
        for(item in json) {
            val queryModel = QueryModel()
            queryModel.id = item.findValue("id").toString()
            queryModel.query = item.findValue("query").toString()
            queryModelList.add(queryModel)
        }
        return queryModelList
    }

    fun getDataByInput(userQueryInput: String): List<QueryModel> {
        val query = Query()
        query.addCriteria(Criteria.where("query").`is`(userQueryInput))
        return mongo.find(query, "DBTesting")
    }
}