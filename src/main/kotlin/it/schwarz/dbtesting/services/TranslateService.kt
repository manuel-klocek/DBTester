package it.schwarz.dbtesting.services

import org.bson.Document
import org.springframework.stereotype.Service

@Service
class TranslateService {

    fun translateToQueryDoc(payloadAsString: String): List<Document> {
        return formatToQuery(payloadAsString)
    }

    private fun formatToQuery(payload: String): List<Document> {
        val doc: MutableList<Document> = arrayListOf()
        doc.add(Document())
        val docIndex = 0
        var check = 0
        for((index, char) in payload.withIndex()) {
            if (char == '{') {
                if(check > 0) { check = 0; continue }
                doc[docIndex][getCurrentExpression(payload.substring(index + 1, payload.length))] =
                    getFollowingExpressions(payload.substring(index + 1, payload.length))
                check++
            }
        }
        println(doc)
        return doc
    }

    private fun getCurrentExpression (string: String): String {
        var word = ""
        for(i in string.indices) {
            if (string[i] == '=') return word
            word += string[i]
        }
        return word
    }

    private fun getFollowingExpressions(string: String): Any {
        val (isDoc, index) = isFollowingTypeDoc(string)
        if(isDoc) return nextDoc(string.substring(index, string.length))
        return arrayListOf<Document>()
    }

    private fun isFollowingTypeDoc(string: String): Pair<Boolean, Int> {
        for(i in string.indices) {
            if (string[i] == '[') return Pair(false, 0)
            else if(string[i] == '{') return Pair(true, i + 1)
        }
        return Pair(true, 0)
    }

    private fun nextDoc(string: String): Document {
        var lastDoc = false
        for(char in string) {
            if(char == '{'){
                lastDoc = false
                break
            } else if(char == '}') {
                lastDoc = true
                break
            }
        }
        if(lastDoc) return Document(getCurrentExpression(string), getValueForExpression(string))
        return Document("name", "Marcus Aurelius")
    }

    private fun getValueForExpression(string: String): String {
        var index = 0
        var word = ""
        for(i in string.indices) {
            if (string[i] == '=') break
            index++
        }
        for(i in index + 1 until string.length) {
            if (string[i] == '}') return word
            word += string[i]
        }
        return word
    }
}
