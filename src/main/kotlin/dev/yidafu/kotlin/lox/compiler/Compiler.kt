package dev.yidafu.kotlin.lox.compiler

import dev.yidafu.kotlin.lox.common.*
import dev.yidafu.kotlin.lox.common.Class
import dev.yidafu.kotlin.lox.common.Grouping
import dev.yidafu.kotlin.lox.common.Set
import dev.yidafu.kotlin.lox.interperter.LoxCallable
import dev.yidafu.kotlin.lox.parser.TokenType
import dev.yidafu.kotlin.lox.vm.Chunk
import dev.yidafu.kotlin.lox.vm.FunctionObject
import dev.yidafu.kotlin.lox.vm.LoxValue
import dev.yidafu.kotlin.lox.vm.LoxValue.*
import dev.yidafu.kotlin.lox.vm.OpCode.*
import java.util.Stack

enum class FunctionType {
    FunctionType,
    ScriptType;
}

class Local(
    val name: String,
    val depth: Int,
)

class Compiler(
//    val chunk: Chunk,
    val functions: Stack<FunctionObject> = Stack(),
//    var funcType: FunctionType = FunctionType.ScriptType,
    val locals: MutableList<Local> = mutableListOf(),
    var scopeDepth: Int = 0,
) : Expression.Visitor<Unit>, Statement.Visitor<Unit> {
    init {
        functions.add(FunctionObject(funcName = "main"))
    }

    val chunk: Chunk
        get() = functions.peek().chunk

    fun compile(stats: List<Statement>): FunctionObject {
        stats.forEach {
            compile(it)
        }
        return functions.peek()
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
        val opCodes = when (expression.operator.type) {
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
        compile(expression.callee)

        expression.args.forEach { compile(it) }
        val argCount = expression.args.size
        if (argCount > Byte.MAX_VALUE) {
            throw LoxTooManyArgumentException()
        }

        chunk.write(listOf(OpCall.toByte(), argCount.toByte()), -1)
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
            is Boolean -> {
                chunk.addConstant(LoxBool(value), -1)
            }
            else -> unreachable()
        }
    }

    override fun visitLogicalExpression(expression: Logical) {
        when (expression.operator.type) {
            TokenType.AND -> {
                compile(expression.left)
                val endJump = chunk.writeJump(OpJumpIfFalse.toByte())
                chunk.write(OpPop.toByte(), expression.operator.line)
                compile(expression.right)
                chunk.patchJump(endJump)
            }
            TokenType.OR -> {
                compile(expression.left)
                val elseJump = chunk.writeJump(OpJumpIfFalse.toByte())
                val endJump = chunk.writeJump(OpJump.toByte())
                chunk.patchJump(elseJump)
                chunk.write(OpPop, -1)

                compile(expression.right)

                chunk.patchJump(endJump)
            }
            else -> unreachable()
        }
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
        compile(statement.condition)

        val thenJump = chunk.writeJump(OpJumpIfFalse.toByte())
        chunk.write(OpPop.toByte(), -1) // pop condition
        compile(statement.thenBranch)

        val elseJump = chunk.writeJump(OpJump.toByte())

        chunk.patchJump(thenJump)

        chunk.write(OpPop.toByte(), -1) // pop condition

        if (statement.elseBranch != null) {
            compile(statement.elseBranch)
        }
        chunk.patchJump(elseJump)
    }

    override fun visitPrintStatement(statement: Print) {
        compile(statement.expr)
        chunk.write(OpPrint.toByte(), -1)
    }

    override fun visitFuncStatement(statement: Func) {
        val globalFuncName = statement.name.lexeme
        val line = statement.name.line
        if (scopeDepth > 0) {
            addLocal(globalFuncName)
        }
//        funcType = FunctionType.FunctionType

        val funcObj = FunctionObject(statement.params.size, globalFuncName)
        functions.push(funcObj)
        scopeWrapper {
            statement.params.forEach {
                addLocal(it.lexeme)
            }
            compile(statement.body)
        }
        functions.pop()
        chunk.addConstant(LoxFunction(funcObj), line)

        chunk.write(OpSetGlobal, line)
        chunk.addConstant(LoxString(globalFuncName), line)
    }

    override fun visitReturnStatement(statement: Return) {
        statement.value?.let { compile(it) }
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
        val loopStart = chunk.codes.size
        compile(statement.condition)

        val exitJump = chunk.writeJump(OpJumpIfFalse)
        chunk.write(OpPop, -1)

        compile(statement.body)
        chunk.writeLoop(loopStart)
        chunk.patchJump(exitJump)
        chunk.write(OpPop, -1)
    }
    private fun compile(expr: Expression) {
        expr.accept(this)
    }

    private fun compile(stat: Statement) {
        stat.accept(this)
    }
}
