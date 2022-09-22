package it.schwarz.dbtesting.configs

import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate


@Configuration
class MongoConfig {

    @Bean
    fun getMongoTemplate(): MongoTemplate {
        return MongoTemplate(
            MongoClients.create("mongodb://localhost:27028/TestDatabase"),
            "TestDatabase"
        )
    }
}