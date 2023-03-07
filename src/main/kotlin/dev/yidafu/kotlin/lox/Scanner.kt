package dev.yidafu.kotlin.lox

class Scanner(
    private val source: String,
    private val tokens: MutableList<Token> = mutableListOf<Token>(),
    private var start: Int = 0,
    private var current: Int = 0,
    private var line: Int = 0,
) {
    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        tokens.add(Token(TokenType.EDF, "", Any(), line))
        return tokens
    }

    private fun scanToken() {
        val char = advance()
        if (TokenType.isSeparetor(char)) {
            addToken(TokenType.toType(char))
        } else if (isDigit(char)) {
            number()
        } else if (isAlpha(char)) {
            identifier()
        } else {
            when (char) {
                '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
                '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
                '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
                '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
                '/' -> {
                    if (match('/')) {
                        while (peek() != '\n' && !isAtEnd()) advance()
                    } else {
                        addToken(TokenType.SLASH)
                    }
                }
                ' ', '\r', '\t' -> {}
                '\n' -> line++
                '"' -> string()
                else -> error(line, "Unexpected character -> $char")
            }
        }
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) {
            error(line, "Unterminated String.")
            return
        }
        advance() // the closing "
        val str = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, str)
    }

    private fun number() {
        while (isDigit(peek())) advance()

        if (peek() == '.' && isDigit(peek2())) {
            advance() // consume the "."

            while (isDigit(peek())) advance()
        }
        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun identifier() {
        while (isAlphaNumberic(peek())) advance()
        val text = source.substring(start, current)
        val type = TokenType.toType(text) ?: TokenType.IDENTIFIER
        addToken(type)
    }
    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '_'
    }

    private fun isAlphaNumberic(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun addToken(type: TokenType, literal: Any = Any()) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false

        if (source[current] != expected) return false

        current++
        return true
    }
    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun peek(): Char {
        if (isAtEnd()) return 0.toChar()
        return source[current]
    }

    private fun peek2(): Char {
        if (current + 1 >= source.length) return 0.toChar()
        return source[current + 1]
    }
}
