package dev.yidafu.kotlin.lox // ktlint-disable filename

abstract class Expression {
    abstract fun <R> accept(visitor: Visitor<R>): R

    interface Visitor<R> {
        fun visitAssignExpression(expression: Assign): R
        fun visitBinaryExpression(expression: Binary): R
        fun visitFunCallExpression(expression: FunCall): R
        fun visitGroupingExpression(expression: Grouping): R
        fun visitLiteralExpression(expression: Literal): R
        fun visitLogicalExpression(expression: Logical): R
        fun visitUnaryExpression(expression: Unary): R
        fun visitVariableExpression(expression: Variable): R
    }
}

class Assign(
    val name: Token,
    val value: Expression,
) : Expression() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitAssignExpression(this)
    }
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

class FunCall(
    val callee: Expression,
    val paren: Token,
    val args: List<Expression>,
) : Expression() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitFunCallExpression(this)
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

class Logical(
    val left: Expression,
    val operator: Token,
    val right: Expression,
) : Expression() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitLogicalExpression(this)
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

class Variable(
    val name: Token,
) : Expression() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitVariableExpression(this)
    }
}
