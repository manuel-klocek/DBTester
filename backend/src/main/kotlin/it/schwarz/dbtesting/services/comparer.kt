package it.schwarz.dbtesting.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.Document


fun equals(a: List<Document>, b: List<Document>): Boolean {
    return hasEqualHash(a, b) && hasEqualSize(a, b) && isEqualAsJson(a, b)
}

private fun hasEqualHash(a: List<Document>, b: List<Document>): Boolean {
    return a.hashCode() == b.hashCode()
}

private fun hasEqualSize(a: List<Document>, b: List<Document>): Boolean {
    return a.size == b.size
}

private fun isEqualAsJson(a: List<Document>, b: List<Document>): Boolean {
    return toJsonNode(a) == toJsonNode(b)
}

private fun toJsonNode(a: List<Document>): JsonNode {
    return ObjectMapper().readTree(Document("", a).toJson())
}
