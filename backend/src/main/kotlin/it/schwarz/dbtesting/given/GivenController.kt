package it.schwarz.dbtesting.given

import org.bson.Document
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("db-tester/given")
class GivenController {

    private val fakeDatabase = mutableListOf(
        Given("example", listOf(Document("hello", "world")))
    )

    @GetMapping("{name}")
    fun get(@PathVariable name: String): Given {
        return fakeDatabase.find { it.name == name } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("{name}")
    fun delete(@PathVariable name: String) {
        fakeDatabase.removeIf { it.name == name }
    }

    @PostMapping("")
    fun post(@RequestBody given: Given): HttpStatus {
        if (fakeDatabase.find { it.name == given.name } == null) {
            fakeDatabase.add(given)
            return HttpStatus.OK
        }
        return HttpStatus.BAD_REQUEST
    }

    @PutMapping("")
    fun put(@RequestBody given: Given) {
        fakeDatabase.replaceAll { if (it.name == given.name) given else it }
    }
}