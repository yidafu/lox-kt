package dev.yidafu.kotlin.lox.interperter

import dev.yidafu.kotlin.lox.common.*
import dev.yidafu.kotlin.lox.common.Grouping
import dev.yidafu.kotlin.lox.common.Set
import dev.yidafu.kotlin.lox.parser.*

typealias AnyValue = Any
fun checkNumberAndOperands(token: Token, left: AnyValue, right: AnyValue) {
    if (left is Double && right is Double) return

    throw LoxRuntimeException(token, "Operands must be numbers.")
}

operator fun AnyValue.plus(that: AnyValue): AnyValue {
    return when {
        this@plus is Double && that is Double -> {
            this@plus + that
        }

        this@plus is String && that is String -> this@plus + that
        else -> LoxRuntimeException(Token.plus(), "${this.javaClass} can't plus ${that.javaClass}")
    }
}

operator fun AnyValue.minus(that: AnyValue): Double {
    checkNumberAndOperands(Token.plus(), this, that)
    return this as Double - that as Double
}

operator fun AnyValue.div(that: AnyValue): Double {
    checkNumberAndOperands(Token.plus(), this, that)
    return this as Double / that as Double
}

operator fun AnyValue.times(that: AnyValue): Double {
    checkNumberAndOperands(Token.plus(), this, that)
    return this as Double * that as Double
}
operator fun AnyValue.compareTo(that: AnyValue): Int {
    checkNumberAndOperands(Token.plus(), this, that)
    return (this as Double - that as Double).toInt()
}

// operator fun AnyValue.equals(that: AnyValue?): Boolean {
//    if (that == null) return false
//    checkNumberAndOperands(Token.plus(), this, that)
//    return this == that
// }

class Interperter : Expression.Visitor<AnyValue>, dev.yidafu.kotlin.lox.common.Statement.Visitor<Void?> {
    internal val global = Environment()

    private lateinit var environment: Environment

    private val locals: MutableMap<Expression, Int> = mutableMapOf()

    init {
        this.environment = this.global
        this.global.define(
            "clock",
            object : LoxCallable {
                override fun arity(): Int = 0

                override fun call(interperter: Interperter, args: List<AnyValue>): AnyValue {
                    return System.currentTimeMillis() / 1000.0
                }

                override fun toString(): String {
                    return "<native fns>"
                }
            },
        )
    }

    fun interpert(stats: List<Statement>) {
        stats.forEach {
            evaluate(it)
        }
    }

    override fun visitAssignExpression(expression: Assign): AnyValue {
        val value = evaluate(expression.value)
        val distance = locals[expression]

        distance?.let {
            environment.assignAt(distance, expression.name, value)
        } ?: global.assign(expression.name, value)

        return value
    }

    override fun visitBinaryExpression(expression: Binary): AnyValue {
        val left = evaluate(expression.left)
        val right = evaluate(expression.right)

        return when (expression.operator.type) {
            // TODO: > < >= <= != ==
            TokenType.GREATER -> left > right
            TokenType.GREATER_EQUAL -> left >= right
            TokenType.LESS -> left < right
            TokenType.LESS_EQUAL -> left <= right
            TokenType.EQUAL_EQUAL -> left == right
            TokenType.BANG_EQUAL -> left != right
            TokenType.MINUS -> left - right
            TokenType.PLUS -> left + right
            TokenType.SLASH -> left / right
            TokenType.STAR -> left * right
            else -> throw Exception("unreachable")
        }
    }

    override fun visitFunCallExpression(expression: FunCall): AnyValue {
        val callee = evaluate(expression.callee)
        val args = expression.args.map {
            evaluate(it)
        }

        if (callee !is LoxCallable) {
            throw LoxCallableException()
        }

        val function = callee as LoxCallable
        return function.call(this, args)
    }

    override fun visitGetExpression(expression: Get): AnyValue {
        val obj = evaluate(expression.obj)
        if (obj is LoxInstance) {
            return obj.get(expression.name)
        }

        throw LoxNotObjectException()
    }

    override fun visitGroupingExpression(expression: Grouping): AnyValue {
        return evaluate(expression.expr)
    }

    override fun visitLiteralExpression(expression: Literal): AnyValue {
        return expression.value
    }

    override fun visitLogicalExpression(expression: Logical): AnyValue {
        val leftResult = evaluate(expression.left)
        if (expression.operator.type == TokenType.OR) {
            if (isTruthy(leftResult)) return leftResult
        } else if (!isTruthy(leftResult)) {
            return leftResult
        }
        return evaluate(expression.right)
    }

    override fun visitUnaryExpression(expression: Unary): AnyValue {
        val right = evaluate(expression.right)

        return when (expression.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> -(right as Double)
            else -> throw Exception("unreachable")
        }
    }

    override fun visitSetExpression(expression: Set): AnyValue {
        val obj = evaluate(expression.obj)
        if (obj !is LoxInstance) {
            throw LoxNotObjectException()
        }

        val value = evaluate(expression.value)
        obj.set(expression.name, value)

        return value
    }

    override fun visitSuperExpression(expression: Super): AnyValue {
        val distance = locals[expression]

        return distance?.let {
            val superClass = environment.getAt(it, "super") as LoxClass
            val obj = environment.getAt(it - 1, "this") as LoxInstance
            val method = superClass.findMethod(expression.method.lexeme)
            method?.bind(obj) ?: throw LoxObjectNotHavePropertiesException()
        } ?: Nil()
    }

    override fun visitThisExpression(expression: This): AnyValue {
        return lookupVariable(expression.keyword, expression)
    }

    override fun visitVariableExpression(expression: Variable): AnyValue {
        return lookupVariable(expression.name, expression)
    }

    private fun isTruthy(value: AnyValue): Boolean {
        return when (value) {
            is Boolean -> value
            is String -> true
            is Nil -> false
            else -> false
        }
    }

    private fun evaluate(expr: Expression): AnyValue {
        return expr.accept(this)
    }

    private fun evaluate(stat: dev.yidafu.kotlin.lox.common.Statement) {
        stat.accept(this)
    }

    override fun visitBlockStatement(statement: dev.yidafu.kotlin.lox.common.Block): Void? {
        executeBlock(statement.statements, Environment(this.environment))
        return null
    }

    override fun visitClassStatement(statement: Class): Void? {
        val superClass = statement.supperClass?.let {
            val maybeSuperClass = evaluate(statement.supperClass)
            if (maybeSuperClass !is LoxClass) throw LoxSupperClassException()
            maybeSuperClass
        }

        environment.define(statement.name.lexeme, Nil())

        statement.supperClass?.let {
            environment = Environment(environment)
            environment.define("super", superClass)
        }
        val methods = statement.methods.associate {
            val function = LoxFunction(it, environment, it.name.lexeme === "init")
            it.name.lexeme to function
        }
        val klass = LoxClass(statement.name.lexeme, superClass, methods)

        superClass?.let { environment = environment.enclosing!! }

        environment.assign(statement.name, klass)
        return null
    }

    internal fun executeBlock(stats: List<dev.yidafu.kotlin.lox.common.Statement>, environment: Environment) {
        val previous = this.environment
        this.environment = environment
        try {
            stats.forEach {
                evaluate(it)
            }
        } finally {
            this.environment = previous
        }
    }

    override fun visitExprStatement(statement: dev.yidafu.kotlin.lox.common.Expr): Void? {
        evaluate(statement.expr)
        return null
    }

    override fun visitIfStatement(statement: dev.yidafu.kotlin.lox.common.If): Void? {
        if (isTruthy(evaluate(statement.condition))) {
            evaluate(statement.thenBranch)
        } else {
            statement.elseBranch?.let {
                evaluate(it)
            }
        }
        return null
    }

    override fun visitPrintStatement(statement: dev.yidafu.kotlin.lox.common.Print): Void? {
        println(evaluate(statement.expr))
        return null
    }

    override fun visitFuncStatement(statement: dev.yidafu.kotlin.lox.common.Func): Void? {
        val func = LoxFunction(statement, environment, false)
        environment.define(statement.name.lexeme, func)
        return null
    }

    override fun visitReturnStatement(statement: dev.yidafu.kotlin.lox.common.Return): Void? {
        val value = statement.value?.let { evaluate(it) } ?: Nil()
        throw LoxReturnInterruptException(value)
    }

    override fun visitVarStatement(statement: dev.yidafu.kotlin.lox.common.Var): Void? {
        val value: AnyValue? = statement.init?.let { evaluate(statement.init) }

        environment.define(statement.name.lexeme, value)
        return null
    }

    override fun visitWhileStatement(statement: dev.yidafu.kotlin.lox.common.While): Void? {
        while (isTruthy(evaluate(statement.condition))) {
            evaluate(statement.body)
        }
        return null
    }

    internal fun resolve(expr: Expression, depth: Int) {
        locals[expr] = depth
    }

    private fun lookupVariable(name: Token, expr: Expression): AnyValue {
        val distance = locals[expr]
        return distance?.let {
            environment.getAt(distance, name.lexeme)
        } ?: global[name]
    }
}
