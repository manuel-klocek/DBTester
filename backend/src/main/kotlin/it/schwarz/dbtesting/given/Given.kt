package it.schwarz.dbtesting.given

import org.bson.Document

data class Given(
    val name: String,
    val data: List<Document>
)