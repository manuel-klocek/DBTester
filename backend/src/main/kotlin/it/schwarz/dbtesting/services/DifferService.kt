package it.schwarz.dbtesting.services

import it.schwarz.dbtesting.models.DifferenceModel
import org.bson.Document
import org.springframework.stereotype.Service

@Service
class DifferService {
    fun getDifference(want: List<Document>, got: List<Document>): List<List<DifferenceModel>> {
        //list for every 1st Level key including its value
        val gotList: MutableList<Document> = arrayListOf()
        got.spliterator().forEachRemaining { doc -> gotList.add(doc) }
        val wantList: MutableList<Document> = arrayListOf()
        want.spliterator().forEachRemaining { doc -> wantList.add(doc) }

        //gets bigger lists to iterate through every entry
        var biggerList = wantList
        var smallerList = gotList
        if (gotList.size >= wantList.size) {
            biggerList = gotList
            smallerList = wantList
        }

        //list of differences
        val differList = mutableListOf<List<DifferenceModel>>()
        val differences = mutableListOf<DifferenceModel>()
        for ((index, doc) in biggerList.withIndex()) {
            val biggerListItems = mutableListOf<String>()
            val smallerListItems = mutableListOf<String>()
            doc.mapKeys { key -> biggerListItems.add(key.toString()) }
            try{  smallerList[index].mapKeys { key -> smallerListItems.add(key.toString()) } } catch (_: Exception){}

            for ((index, element) in biggerListItems.withIndex()) {
                val diffModel = DifferenceModel()
                if (index >= smallerListItems.size) {
                    diffModel.expected = element
                    diffModel.got = ("Missing!")
                } else if (element != smallerListItems[index]) {
                    diffModel.expected = element
                    diffModel.got = smallerListItems[index]
                } else if (element == smallerListItems[index]) {
                    continue
                }
                differences.add(diffModel)
            }
            if(differences.size != 0) differList.add(differences)
        }
        return differList
    }
}