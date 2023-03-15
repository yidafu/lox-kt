package dev.yidafu.kotlin.lox.common

import dev.yidafu.kotlin.lox.parser.Token

abstract class Statement {
    abstract fun <R> accept(visitor: Visitor<R>): R

    interface Visitor<R> {
        fun visitBlockStatement(statement: Block): R
        fun visitClassStatement(statement: Class): R
        fun visitExprStatement(statement: Expr): R
        fun visitIfStatement(statement: If): R
        fun visitPrintStatement(statement: Print): R
        fun visitFuncStatement(statement: Func): R
        fun visitReturnStatement(statement: Return): R
        fun visitVarStatement(statement: Var): R
        fun visitWhileStatement(statement: While): R
    }
}

class Block(
    val statements: List<Statement>,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitBlockStatement(this)
    }
}

class Class(
    val name: Token,
    val supperClass: Variable?,
    val methods: List<Func>,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitClassStatement(this)
    }
}

class Expr(
    val expr: Expression,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitExprStatement(this)
    }
}

class If(
    val condition: Expression,
    val thenBranch: Statement,
    val elseBranch: Statement?,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitIfStatement(this)
    }
}

class Print(
    val expr: Expression,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitPrintStatement(this)
    }
}

class Func(
    val name: Token,
    val params: List<Token>,
    val body: List<Statement>,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitFuncStatement(this)
    }
}

class Return(
    val keyword: Token,
    val value: Expression?,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitReturnStatement(this)
    }
}

class Var(
    val name: Token,
    val init: Expression?,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitVarStatement(this)
    }
}

class While(
    val condition: Expression,
    val body: Statement,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitWhileStatement(this)
    }
}
