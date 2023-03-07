package dev.yidafu.kotlin.lox

import kotlin.test.Test
import kotlin.test.assertEquals

class InterperterTest {
    private fun parse(src: String): Expression {
        val tokens = Scanner(src).scanTokens()
        return Parser(tokens).parse()
    }

    @Test
    fun interperterTest() {
        val expr = parse("1 + 2 * 3 - 1")

        assertEquals(6.0, Interperter().interpert(expr))
    }
}
