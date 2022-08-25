package it.schwarz.dbtesting.examples

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * This annotation makes it so Junit creates only one instance of the JunitExample class.
 * Otherwise, Junit creates one instance per function with @Test annotation.
 * Since Kotlin has no static methods you can not use @BeforeAll in this case.
 * You would have to use a Kotlin companion object to get a similar effect.
 * Also, performance is better if only one instance is used (tests tend to become slow in practice which is annoying).
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JunitExample {

    @BeforeAll
    fun beforeAll() {
        println("Executed before all testcases")
    }

    @AfterEach
    fun afterEach() {
        println("Executed after each test case")
    }

    @Test
    fun `test case one`(){
        val truth = true
        assertTrue(truth)
    }

    @Test
    fun `test case two`(){
        val one = 1
        assertEquals(one, 1)
    }
}