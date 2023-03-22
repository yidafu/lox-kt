package dev.yidafu.kotlin.lox.Parser

import dev.yidafu.kotlin.lox.parser.Scanner
import dev.yidafu.kotlin.lox.parser.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals

class ScannerTest {
    @Test
    fun testIfStatement() {
        val tokens = Scanner("if (true) 1.23 else \"string\"").scanTokens()

        assertEquals(tokens[0].type, TokenType.IF)
        assertEquals(tokens[1].type, TokenType.LEFT_PAREN)
        assertEquals(tokens[2].type, TokenType.TRUE)
        assertEquals(tokens[3].type, TokenType.RIGHT_PAREN)
        assertEquals(tokens[4].type, TokenType.NUMBER)
        assertEquals(tokens[5].type, TokenType.ELSE)
        assertEquals(tokens[6].type, TokenType.STRING)
    }

    @Test
    fun varDeclareTest() {
        val tokens = Scanner("var a = 1 + 3").scanTokens()
        assertEquals(tokens[0].type, TokenType.VAR)
        assertEquals(tokens[1].type, TokenType.IDENTIFIER)
        assertEquals(tokens[2].type, TokenType.EQUAL)
        assertEquals(tokens[3].type, TokenType.NUMBER)
        assertEquals(tokens[4].type, TokenType.PLUS)
        assertEquals(tokens[5].type, TokenType.NUMBER)
    }
}
