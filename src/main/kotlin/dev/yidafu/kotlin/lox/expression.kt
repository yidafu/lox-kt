package dev.yidafu.kotlin.lox // ktlint-disable filename

interface Visitor<R> {
    fun visitBinaryExpression(expression: Binary): R
    fun visitGroupingExpression(expression: Grouping): R
    fun visitLiteralExpression(expression: Literal): R
    fun visitUnaryExpression(expression: Unary): R
}

abstract class Expression {
    abstract fun <R> accept(visitor: Visitor<R>): R
}

class Binary(
    val left: Expression,
    val operator: Token,
    val right: Expression,
) : Expression() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitBinaryExpression(this)
    }
}

class Grouping(
    val expr: Expression,
) : Expression() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitGroupingExpression(this)
    }
}

class Literal(
    val value: Any,
) : Expression() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitLiteralExpression(this)
    }
}

class Unary(
    val operator: Token,
    val right: Expression,
) : Expression() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitUnaryExpression(this)
    }
}
