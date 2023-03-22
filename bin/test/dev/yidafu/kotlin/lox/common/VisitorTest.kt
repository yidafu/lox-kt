package dev.yidafu.kotlin.lox.common

import dev.yidafu.kotlin.lox.parser.Parser
import dev.yidafu.kotlin.lox.parser.Scanner
import dev.yidafu.kotlin.lox.parser.Token
import dev.yidafu.kotlin.lox.parser.TokenType
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

    @Test
    fun printFotStatement() {
        val output = AstPrinter().print(
            Parser(
                Scanner(
                    """
            for (var a = 1; a < 4; a = a + 1) {
                print a;
            }
                    """.trimIndent(),
                ).scanTokens(),
            ).parse(),
        )
        assertEquals("{ (var [a] = 1.0)  while ((< [a] 4.0)) { { (print [a]) }  (expr (a = (+ [a] 1.0))) } }", output)
    }
}
