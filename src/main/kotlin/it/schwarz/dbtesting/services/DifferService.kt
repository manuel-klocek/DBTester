package it.schwarz.dbtesting.services

import org.bson.Document
import org.springframework.stereotype.Service

@Service
class DifferService {
    fun getDifference(want: List<Document>, got: List<Document>): List<String> {
        //list for every 1st Level key including its value
        val gotList: MutableList<String> = arrayListOf()
        got.spliterator().forEachRemaining{ doc -> doc.mapKeys {
                key -> gotList.add(key.toString()) }}
        val wantList: MutableList<String> = arrayListOf()
        want.spliterator().forEachRemaining{ doc -> doc.mapKeys {
                key -> wantList.add(key.toString()) }}

        //gets bigger lists to iterate through every entry
        var biggerList = wantList
        var smallerList = gotList
        if(gotList.size >= wantList.size){
            biggerList = gotList
            smallerList = wantList
        }

        //list of differences
        val differList: MutableList<String> = arrayListOf()
        for((index, element) in biggerList.withIndex()) {
            if(index >= smallerList.size){
                differList.add(element)
                differList.add("Missing!")
            } else if(element != smallerList[index]) {
                differList.add(element)
                differList.add(smallerList[index])
            }
        }
        return differList
    }
}