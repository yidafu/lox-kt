package dev.yidafu.kotlin.lox

class Parser(
    private val tokens: List<Token>,
    private var current: Int = 0,
) {

    fun parse(): List<Statement> {
        val stats = mutableListOf<Statement>()

        while (!isAtEnd()) {
            stats.add(statement())
        }
        return stats
    }

    private fun statement(): Statement {
        return when {
            match(TokenType.VAR) -> declaration()
            match(TokenType.PRINT) -> printStatement()
            match(TokenType.LEFT_BRACE) -> block()
            else -> expressionStatement()
        }
    }

    private fun declaration(): Statement {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name")
        var init: Expression? = null
        if (match(TokenType.EQUAL)) {
            init = expreesion()
        }
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration")
        return Var(name, init)
    }
    private fun printStatement(): Print {
        val expr = expreesion()
        consume(TokenType.SEMICOLON, "Expect ; after value")
        return Print(expr)
    }

    private fun block(): Statement {
        val stats = mutableListOf<Statement>()
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            stats.add(statement())
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block")
        return Block(stats)
    }

    private fun expressionStatement(): Expr {
        val expr = expreesion()
        consume(TokenType.SEMICOLON, "expect ; after expression")
        return Expr(expr)
    }

    fun expreesion(): Expression {
        return assignment()
    }

    private fun assignment(): Expression {
        val expr = equality()
        if (match(TokenType.EQUAL)) {
//            val equals = previous()
            val value = assignment()
            if (expr is Variable) {
                return Assign(expr.name, value)
            }
            unreachable()
        }
        return expr
    }

    private fun equality(): Expression {
        var expr = comparison()

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val rignt = comparison()
            expr = Binary(expr, operator, rignt)
        }
        return expr
    }

    private fun comparison(): Expression {
        var expr = term()

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expression {
        var expr = factor()

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expression {
        var expr = unary()

        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expression {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }

        return primary()
    }

    private fun primary(): Expression {
        return when {
            match(TokenType.FALSE) -> Literal(false)
            match(TokenType.TRUE) -> Literal(true)
            match(TokenType.NIL) -> Literal(Any())
            match(TokenType.NUMBER, TokenType.STRING) -> Literal(previous().literal)
            match(TokenType.LEFT_PAREN) -> {
                val expr = expreesion()
                consume(TokenType.RIGHT_PAREN, "Expect ')' after expression")
                Grouping(expr)
            }
            match(TokenType.IDENTIFIER) -> Variable(previous())
            else -> unreachable()
        }
    }

    private fun consume(type: TokenType, msg: String): Token {
        if (check(type)) return advance()
        throw Exception(msg)
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType): Boolean {
        return if (isAtEnd()) false else peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++

        return previous()
    }

    private fun isAtEnd(): Boolean {
        return peek().type == TokenType.EOF
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }
}
