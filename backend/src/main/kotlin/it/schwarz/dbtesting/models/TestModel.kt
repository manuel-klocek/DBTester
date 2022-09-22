package it.schwarz.dbtesting.models

import org.bson.Document

class TestModel {
    var header: String = ""
    var query: List<Document> = arrayListOf()
    var want: List<Document> = arrayListOf()
}