package dev.yidafu.kotlin.lox.compiler

import dev.yidafu.kotlin.lox.common.*
import dev.yidafu.kotlin.lox.common.Class
import dev.yidafu.kotlin.lox.common.Grouping
import dev.yidafu.kotlin.lox.common.Set
import dev.yidafu.kotlin.lox.parser.TokenType
import dev.yidafu.kotlin.lox.vm.Chunk
import dev.yidafu.kotlin.lox.vm.LoxValue
import dev.yidafu.kotlin.lox.vm.OpCode

class Compiler(val chunk: Chunk) : Expression.Visitor<Unit>, Statement.Visitor<Unit> {
    fun compile(stats: List<Statement>) {
        stats.forEach {
            compile(it)
        }
    }

    override fun visitAssignExpression(expression: Assign) {
        TODO("Not yet implemented")
    }

    override fun visitBinaryExpression(expression: Binary) {
        compile(expression.left)
        compile(expression.right)
        val line = expression.operator.line
        val opCodes = when (val operator = expression.operator.type) {
            TokenType.PLUS -> arrayOf(OpCode.OpAdd)
            TokenType.MINUS -> arrayOf(OpCode.OpSubtract)
            TokenType.STAR -> arrayOf(OpCode.OpMultiply)
            TokenType.SLASH -> arrayOf(OpCode.OpDivide)
            TokenType.BANG_EQUAL -> arrayOf(OpCode.OpEqual, OpCode.OpNot)
            TokenType.EQUAL_EQUAL -> arrayOf(OpCode.OpEqual)
            TokenType.GREATER -> arrayOf(OpCode.OpGreater)
            TokenType.GREATER_EQUAL -> arrayOf(OpCode.OpLess, OpCode.OpNot)
            TokenType.LESS -> arrayOf(OpCode.OpLess)
            TokenType.LESS_EQUAL -> arrayOf(OpCode.OpGreater, OpCode.OpNot)
            else -> unreachable()
        }.map { it.toByte() }
        chunk.write(opCodes, line)
    }

    override fun visitFunCallExpression(expression: FunCall) {
        TODO("Not yet implemented")
    }

    override fun visitGetExpression(expression: Get) {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpression(expression: Grouping) {
        TODO("Not yet implemented")
    }

    override fun visitLiteralExpression(expression: Literal) {
        when (val value = expression.value) {
            is Number -> {
                chunk.addConstant(LoxValue.LoxNumber(value.toDouble()), -1)
            }
            is String -> {
//                chunk.
            }
            else -> unreachable()
        }
    }

    override fun visitLogicalExpression(expression: Logical) {
        TODO("Not yet implemented")
    }

    override fun visitUnaryExpression(expression: Unary) {
        TODO("Not yet implemented")
    }

    override fun visitSetExpression(expression: Set) {
        TODO("Not yet implemented")
    }

    override fun visitSuperExpression(expression: Super) {
        TODO("Not yet implemented")
    }

    override fun visitThisExpression(expression: This) {
        TODO("Not yet implemented")
    }

    override fun visitVariableExpression(expression: Variable) {
        TODO("Not yet implemented")
    }

    override fun visitBlockStatement(statement: Block) {
        TODO("Not yet implemented")
    }

    override fun visitClassStatement(statement: Class) {
        TODO("Not yet implemented")
    }

    override fun visitExprStatement(statement: Expr) {
        compile(statement.expr)
    }

    override fun visitIfStatement(statement: If) {
        TODO("Not yet implemented")
    }

    override fun visitPrintStatement(statement: Print) {
        TODO("Not yet implemented")
    }

    override fun visitFuncStatement(statement: Func) {
        TODO("Not yet implemented")
    }

    override fun visitReturnStatement(statement: Return) {
        chunk.write(OpCode.OpReturn.toByte(), statement.keyword.line)
    }

    override fun visitVarStatement(statement: Var) {
        TODO("Not yet implemented")
    }

    override fun visitWhileStatement(statement: While) {
        TODO("Not yet implemented")
    }
    private fun compile(expr: Expression) {
        expr.accept(this)
    }

    private fun compile(stat: Statement) {
        stat.accept(this)
    }
}
