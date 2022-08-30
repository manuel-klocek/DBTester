package it.schwarz.dbtesting.services

import it.schwarz.dbtesting.models.QueryModel
import org.springframework.stereotype.Service

@Service
class TestService {
    fun compare(got: List<QueryModel>, want: List<QueryModel>) {
        if(!compareListsByLength(got, want)) println("Size is different!")
        if(!compareListsByContent(got, want)) println("Content is different!")
    }

    private fun compareListsByLength(dataByUserQuery: List<QueryModel>, expectedData: List<QueryModel>): Boolean {
        return dataByUserQuery.size == expectedData.size
    }

    private fun compareListsByContent(dataByUserQuery: List<QueryModel>, expectedData: List<QueryModel>): Boolean {
        for(item in dataByUserQuery){
            if(!checkForSameEntry(item, expectedData)) return false
        }
        return true
    }

    private fun checkForSameEntry(itemByUserQuery: QueryModel, expectedData: List<QueryModel>): Boolean {
        for(expected in expectedData) {
            if(itemByUserQuery.id == expected.id) {
                return true
            }
        }
        return false
    }
}