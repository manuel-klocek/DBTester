package it.schwarz.dbtesting.services

import org.bson.Document
import org.springframework.stereotype.Service

@Service
class TestService {
    fun compare(got: List<Document>, want: List<Document>): Boolean {
        if(!compareListsByLength(got, want)) return false
        if(!compareListsByContent(got, want)) return false
        return true
    }

    private fun compareListsByLength(dataByUserQuery: List<Document>, expectedData: List<Document>): Boolean {
        return dataByUserQuery.size == expectedData.size
    }

    private fun compareListsByContent(dataByUserQuery: List<Document>, expectedData: List<Document>): Boolean {
        for(item in dataByUserQuery){
            if(!checkForSameEntry(item, expectedData)) return false
        }
        return true
    }

    private fun checkForSameEntry(itemByUserQuery: Document, expectedData: List<Document>): Boolean {
        for(expected in expectedData) {
            if(itemByUserQuery.getString("id") == expected.getString("id")) return true
        }
        return false
    }
}