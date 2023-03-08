package dev.yidafu.kotlin.lox

import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {
    private fun parse(src: String): List<Statement> {
        val tokens = Scanner(src).scanTokens()
        return Parser(tokens).parse()
    }

    @Test
    fun parseTest() {
        val stats = parse("1 + 2 * 3 - 1;")
        assertEquals("(expr (- (+ 1.0 (* 2.0 3.0)) 1.0))", AstPrinter().print(stats[0]))
    }
}
