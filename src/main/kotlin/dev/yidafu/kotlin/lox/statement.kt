package dev.yidafu.kotlin.lox


abstract class Statement {
    abstract fun <R> accept(visitor: Visitor<R>): R

    interface Visitor<R> {
        fun visitBlockStatement(statement: Block): R
        fun visitExprStatement(statement: Expr): R
        fun visitPrintStatement(statement: Print): R
        fun visitVarStatement(statement: Var): R
    }

}

class Block(
    val statements: List<Statement>,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitBlockStatement(this);
    }
}

class Expr(
    val expr: Expression,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitExprStatement(this);
    }
}

class Print(
    val expr: Expression,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitPrintStatement(this);
    }
}

class Var(
    val name: Token,
    val init: Expression?,
) : Statement() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitVarStatement(this);
    }
}

