package dev.yidafu.kotlin.lox

import kotlin.test.Test
import kotlin.test.assertEquals

class PaserTest {
    fun parse(src: String): Expression {

        val tokens = Scanner(src).scanTokens()
        return Parser(tokens).parse()
    }

    @Test
    fun parseTest() {
        val expr = parse("1 + 2 * 3 - 1")
        assertEquals("(- (+ 1.0 (* 2.0 3.0)) 1.0)", AstPrinter().print(expr))
    }
}
