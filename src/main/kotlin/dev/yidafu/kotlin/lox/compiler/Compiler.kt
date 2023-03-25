package dev.yidafu.kotlin.lox.compiler

import dev.yidafu.kotlin.lox.common.*
import dev.yidafu.kotlin.lox.common.Class
import dev.yidafu.kotlin.lox.common.Grouping
import dev.yidafu.kotlin.lox.common.Set
import dev.yidafu.kotlin.lox.parser.TokenType
import dev.yidafu.kotlin.lox.vm.Chunk
import dev.yidafu.kotlin.lox.vm.LoxValue.*
import dev.yidafu.kotlin.lox.vm.OpCode.*

class Local(
    val name: String,
    val depth: Int,
)

class Compiler(
    val chunk: Chunk,
    val locals: MutableList<Local> = mutableListOf(),
    var scopeDepth: Int = 0,
) : Expression.Visitor<Unit>, Statement.Visitor<Unit> {
    fun compile(stats: List<Statement>) {
        stats.forEach {
            compile(it)
        }
    }

    override fun visitAssignExpression(expression: Assign) {
        compile(expression.value)
        val name = expression.name.lexeme
        val line = expression.name.line
        val localIndex = resolveLocal(name)
        if (localIndex == -1) {
            chunk.write(OpSetGlobal.toByte(), line)
            chunk.addConstant(LoxString(name), line)
        } else {
            chunk.write(listOf(OpSetLocal.toByte(), localIndex.toByte()), line)
        }
    }

    override fun visitBinaryExpression(expression: Binary) {
        compile(expression.left)
        compile(expression.right)
        val line = expression.operator.line
        val opCodes = when (val operator = expression.operator.type) {
            TokenType.PLUS -> arrayOf(OpAdd)
            TokenType.MINUS -> arrayOf(OpSubtract)
            TokenType.STAR -> arrayOf(OpMultiply)
            TokenType.SLASH -> arrayOf(OpDivide)
            TokenType.BANG_EQUAL -> arrayOf(OpEqual, OpNot)
            TokenType.EQUAL_EQUAL -> arrayOf(OpEqual)
            TokenType.GREATER -> arrayOf(OpGreater)
            TokenType.GREATER_EQUAL -> arrayOf(OpLess, OpNot)
            TokenType.LESS -> arrayOf(OpLess)
            TokenType.LESS_EQUAL -> arrayOf(OpGreater, OpNot)
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
                chunk.addConstant(LoxNumber(value.toDouble()), -1)
            }
            is String -> {
                chunk.addConstant(LoxString(value.toString()), -1)
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
        val nameToken = expression.name
        val name = nameToken.lexeme
        val line = nameToken.line
        val i = resolveLocal(name)
        if (i == -1) {
            chunk.write(OpGetGlobal.toByte(), line)
            chunk.addConstant(LoxString(name), line)
        } else {
            chunk.write(listOf(OpGetLocal.toByte(), i.toByte()), line)
        }
    }

    private fun resolveLocal(name: String): Int {
        for (i in locals.lastIndex downTo 0) {
            val local = locals[i]
            if (local.name == name) return i
        }
        return -1
    }
    override fun visitBlockStatement(statement: Block) {
        scopeWrapper {
            statement.statements.forEach { compile(it) }
        }
        // 函数执行结束后弹出块里的变量
        locals.filter { it.depth > scopeDepth }
            .forEach {
                chunk.write(OpPop.toByte(), -1)
                locals.remove(it)
            }
    }

    private fun scopeWrapper(block: Compiler.() -> Unit) {
        scopeDepth += 1

        block()

        scopeDepth -= 1
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
        compile(statement.expr)
        chunk.write(OpPrint.toByte(), -1)
    }

    override fun visitFuncStatement(statement: Func) {
        TODO("Not yet implemented")
    }

    override fun visitReturnStatement(statement: Return) {
        chunk.write(OpReturn.toByte(), statement.keyword.line)
    }

    override fun visitVarStatement(statement: Var) {

        statement.init?.let {
            compile(it)
        }
        val name = statement.name.lexeme
        if (scopeDepth > 0) {
            locals
                .filter { it.depth < scopeDepth }
                .forEach {
                    if (it.name == name) {
                        throw LoxDuplicateVariableException()
                    }
                }
            addLocal(name)
        } else {
            chunk.write(OpDefineGlobal.toByte(), statement.name.line)
            chunk.addConstant(LoxString(name), statement.name.line)
        }
    }
    private fun addLocal(name: String) {
        locals.add(Local(name, scopeDepth))
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
