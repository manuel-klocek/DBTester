package it.schwarz.dbtesting.services

import org.bson.Document
import org.springframework.stereotype.Service

@Service
class DifferService {
    fun get(want: List<Document>, got: List<Document>, showMissing: Boolean = true): List<List<Document>> {


        //TODO make differ service independent of key order
        val differenceList = mutableListOf<List<Document>>()
        for (i in 0 until getBiggestIndex(got, want)) {
            val keyList = mutableListOf<String>()
            var gotKeys = mutableListOf<String>()
            val wantKeys = mutableListOf<String>()
            val gotValues = mutableListOf<String?>()
            val wantValues = mutableListOf<String?>()
            if(got.isNotEmpty()) {
                got[i].mapKeys { item -> gotKeys.add(item.key) }
                got[i].mapValues { item -> gotValues.add(item.value?.toString()) }
            }
            if(want.isNotEmpty()) {
                want[i].mapKeys { item -> wantKeys.add(item.key) }
                want[i].mapValues { item -> wantValues.add(item.value?.toString()) }
            }

            if(got.isNotEmpty()) {
                got[i].mapKeys { keyList.add(it.key)}
            }
            if(want.isNotEmpty()) {
                want[i].mapKeys { if(!keyList.contains(it.key)) keyList.add(it.key) }
            }

            if(!showMissing) gotKeys = wantKeys
            println(wantKeys)
            println(gotKeys)

            val differences = arrayListOf<Document>()
            for (j in 0 until getBiggestIndex(gotKeys, wantKeys)) {
                val difference = Document()
                if (j >= gotKeys.size) {
                    difference["got"] = "Nothing"
                    difference["expected"] = wantValues[j]
                    differences.add(Document(wantKeys[j], difference))
                    continue
                } else if (j >= wantKeys.size) {
                    difference["got"] = gotValues[j]
                    difference["expected"] = "Nothing"
                } else if (gotValues[j] != wantValues[j] && gotKeys[j] == wantKeys[j]) {
                    difference["got"] = gotValues[j]
                    difference["expected"] = wantValues[j]
                } else {
                    continue
                }
                differences.add(Document(gotKeys[j], difference))
            }
            differenceList.add(differences)
        }
        println(differenceList)
        return differenceList
    }

    private fun getBiggestIndex(list1: List<Any>, list2: List<Any>): Int {
        return if(list1.size > list2.size) list1.size else list2.size
    }
}