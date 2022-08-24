package it.schwarz.dbtesting.models

import org.springframework.data.mongodb.core.mapping.Document

@Document("DBTesting")
class QueryModel {
    var query: String = ""
}
