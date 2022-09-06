package it.schwarz.dbtesting.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.Document
import org.springframework.stereotype.Service

@Service
class TestService {
    fun compare(got: List<Document>, want: List<Document>): Boolean {
        if (!compareByHash(got, want)) return false
        if (!compareListsByLength(got, want)) return false
        if (!compareListsByContent(got, want)) return false
        return true
    }

    private fun compareByHash(got: List<Document>, want: List<Document>): Boolean {
        return got.hashCode() == want.hashCode()
    }

    private fun compareListsByLength(dataByUserQuery: List<Document>, expectedData: List<Document>): Boolean {
        return dataByUserQuery.size == expectedData.size
    }

    fun compareListsByContent(got: List<Document>, want: List<Document>): Boolean {
        return ObjectMapper().readTree(Document("", got).toJson()) ==
                ObjectMapper().readTree(Document("", want).toJson())
    }
}