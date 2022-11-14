package it.schwarz.dbtesting.services

import org.bson.Document
import org.springframework.stereotype.Service

@Service
class DifferService {
    fun get(want: List<Document>, got: List<Document>, showMissing: Boolean = true): List<List<Document>> {
        val differenceList = mutableListOf<List<Document>>()
        for (i in 0 until getBiggestIndex(got, want)) {
            val keyList = mutableListOf<String>()
            val gotDoc = got[i]
            val wantDoc = want[i]

            gotDoc.mapKeys { keyList.add(it.key) }
            wantDoc.mapKeys { if (!keyList.contains(it.key)) keyList.add(it.key) }

            val differences = arrayListOf<Document>()
            for (key in keyList) {
                val difference = Document()
                val gotValue = gotDoc.getOrDefault(key, "Missing")
                val wantValue = wantDoc.getOrDefault(key, "Nothing")

                if (!showMissing && gotValue == "Missing") continue
                else if (wantValue == "Nothing") {
                    difference["got"] = gotValue
                    difference["expected"] = wantValue
                } else if (gotValue != wantValue) {
                    difference["got"] = gotValue
                    difference["expected"] = wantValue
                } else {
                    continue
                }
                differences.add(Document(key, difference))
            }
            differenceList.add(differences)
        }
        return differenceList
    }

    private fun getBiggestIndex(list1: List<Any>, list2: List<Any>): Int {
        return if (list1.size > list2.size) list1.size else list2.size
    }
}