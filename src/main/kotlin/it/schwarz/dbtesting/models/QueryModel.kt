package it.schwarz.dbtesting.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("DBTesting")
class QueryModel {

    @Id
    var id: String? = null
    var query: String = ""
}
