package it.schwarz.dbtesting.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import it.schwarz.dbtesting.configs.MongoConfig
import it.schwarz.dbtesting.models.QueryModel
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct


@Service
class RequestDataService(private val mongoConf: MongoConfig) {

    private final val COLLECTION_NAME = "DBTesting"
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
            //Comes with "" in String
            val queryItemId = item.findValue("id").toString()
            val queryItemQuery = item.findValue("query").toString()
            queryModel.id = queryItemId.substring(1, queryItemId.length - 1)
            queryModel.query = queryItemQuery.substring(1, queryItemQuery.length - 1)
            queryModelList.add(queryModel)
        }
        return queryModelList
    }

    fun getDataByInput(userQueryInput: String): List<QueryModel> {
        val query = Query()
        query.addCriteria(Criteria.where("query").`is`(userQueryInput))
        return mongo.find(query, QueryModel::class.java, COLLECTION_NAME).toList()
    }
}