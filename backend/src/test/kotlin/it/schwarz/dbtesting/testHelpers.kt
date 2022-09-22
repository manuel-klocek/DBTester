package it.schwarz.dbtesting

fun readFile(relativePath: String): String {
    return {}.javaClass.getResource(relativePath)!!.readText()
}
