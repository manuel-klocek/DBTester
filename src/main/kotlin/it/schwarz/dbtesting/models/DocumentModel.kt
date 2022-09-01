package it.schwarz.dbtesting.models

import org.bson.Document

class DocumentModel {
    var header: String = ""
    var payload: List<Document> = arrayListOf()
}