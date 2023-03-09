package dev.yidafu.kotlin.lox

abstract class Statement {
    abstract fun <R> accept(visitor: Visitor<R>): R

    interface Visitor<R> {
        fun visitBlockStatement(statement: Block): R
        fun visitExprStatement(statement: Expr): R
        fun visitIfStatement(statement: If): R
        fun visitPrintStatement(statement: Print): R
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
