package it.schwarz.dbtesting.services

import it.schwarz.dbtesting.models.DifferenceModel
import org.bson.Document
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class DifferService {
    fun getDifference(want: List<Document>, got: List<Document>): List<List<DifferenceModel>> {
        //list for every 1st Level key including its value
        val gotList: MutableList<Document> = arrayListOf()
        got.spliterator().forEachRemaining { doc -> gotList.add(doc) }
        val wantList: MutableList<Document> = arrayListOf()
        want.spliterator().forEachRemaining { doc -> wantList.add(doc) }

        //list of differences
        val differList = mutableListOf<List<DifferenceModel>>()
        for(i in 0 until getBiggestIndex(gotList, wantList)) {
            val gotListItems = mutableListOf<String>()
            val wantListItems = mutableListOf<String>()
            try { gotList[i].mapKeys { key -> gotListItems.add(key.toString())} } catch(_: Exception) {}
            try { wantList[i].mapKeys { key -> wantListItems.add(key.toString()) } } catch(_: Exception) {}

            getBiggestIndex(gotListItems, wantListItems)

            val differences = mutableListOf<DifferenceModel>()
            for (j in 0 until getBiggestIndex(gotListItems, wantListItems)) {
                val gotString: String = try { gotListItems[j] } catch (_: Exception) { "" }
                val wantString: String = try { wantListItems[j] } catch (_: Exception) { "" }

                val diffModel = DifferenceModel()
                if (gotString == wantString) continue
                else if (gotString == "") {
                    diffModel.expected = wantString
                    diffModel.got = "Missing!"
                } else if (wantString == "") {
                    diffModel.expected = "Nothing!"
                    diffModel.got = gotString
                } else {
                    diffModel.expected = wantString
                    diffModel.got = gotString
                }
                differences.add(diffModel)
            }
            if (differences.size != 0) differList.add(differences)
        }
        return differList
    }

    fun getBiggestIndex(list1: List<Any>, list2: List<Any>): Int {
        return if(list1.size > list2.size) list1.size else list2.size
    }
}