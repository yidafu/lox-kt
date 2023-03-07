package dev.yidafu.kotlin.lox

import kotlin.test.Test
import kotlin.test.assertEquals

class VisitorTest {
    @Test
    fun printTest() {
        val expr = Binary(
            Unary(
                Token(TokenType.MINUS, "-"),
                Literal(123),
            ),
            Token(TokenType.STAR, "*"),
            Grouping(Literal(123.456)),
        )

        assertEquals("(* (- 123) (group 123.456))", AstPrinter().print(expr))
    }
}
