package it.schwarz.dbtesting.repositories

import it.schwarz.dbtesting.models.QueryModel
import org.springframework.data.mongodb.repository.MongoRepository

interface QueryRepository : MongoRepository<QueryModel, String> {
}