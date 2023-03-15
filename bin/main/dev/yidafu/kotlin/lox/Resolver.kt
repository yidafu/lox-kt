package dev.yidafu.kotlin.lox

import java.util.Stack

enum class FunctionType {
    NONE,
    FUNCTION,
    INITIALIZER,
    METHOD,
}

enum class ClassType {
    NONE,
    CLASS,
    SUBCLASS,
}
class Resolver(
    private val interperter: Interperter,
    private val scopes: Stack<MutableMap<String, Boolean>> = Stack(),
) : Expression.Visitor<Void?>, Statement.Visitor<Void?> {
    private var currentFunction: FunctionType = FunctionType.NONE
    private var currentClass: ClassType = ClassType.NONE
    override fun visitAssignExpression(expression: Assign): Void? {
        resolve(expression.value)
        resolveLocal(expression, expression.name)

        return null
    }

    override fun visitBinaryExpression(expression: Binary): Void? {
        resolve(expression.left)
        resolve(expression.right)
        return null
    }

    override fun visitFunCallExpression(expression: FunCall): Void? {
        resolve(expression.callee)
        for (arg in expression.args) {
            resolve(arg)
        }

        return null
    }

    override fun visitGetExpression(expression: Get): Void? {
        resolve(expression.obj)
        return null
    }

    override fun visitGroupingExpression(expression: Grouping): Void? {
        resolve(expression.expr)
        return null
    }

    override fun visitLiteralExpression(expression: Literal): Void? {
        return null
    }

    override fun visitLogicalExpression(expression: Logical): Void? {
        resolve(expression.left)
        resolve(expression.right)
        return null
    }

    override fun visitUnaryExpression(expression: Unary): Void? {
        resolve(expression.right)
        return null
    }

    override fun visitSetExpression(expression: Set): Void? {

        resolve(expression.obj)
        resolve(expression.value)
        return null
    }

    override fun visitSuperExpression(expression: Super): Void? {
        if (currentClass == ClassType.NONE) {
            throw LoxTopLevelSuperException()
        }
        if (currentClass != ClassType.SUBCLASS) {
            throw LoxSubClassSuerException()
        }
        resolveLocal(expression, expression.keyword)
        return null
    }

    override fun visitThisExpression(expression: This): Void? {
        if (currentClass == ClassType.NONE) {
            throw LoxTopLevelThisException()
        }
        resolveLocal(expression, expression.keyword)

        return null
    }

    override fun visitVariableExpression(expression: Variable): Void? {
        if (!scopes.isEmpty() && scopes.peek()[expression.name.lexeme] == false) {
            throw LoxVariableNotInitialException(expression.name.lexeme)
        }
        resolveLocal(expression, expression.name)

        return null
    }

    override fun visitBlockStatement(statement: Block): Void? {
        beginScope()
        resolve(statement.statements)
        endScope()
        return null
    }

    override fun visitClassStatement(statement: Class): Void? {
        val enclosingClass = currentClass
        currentClass = ClassType.CLASS

        declare(statement.name)
        define(statement.name)
        if (statement.supperClass?.name?.lexeme == statement.name.lexeme) {
            throw LoxInheritSelfException()
        }
        statement.supperClass?.let {
            currentClass = ClassType.SUBCLASS
            resolve(it)
        }
        statement.supperClass?.let {
            beginScope()
            scopes.peek()["super"] = true
        }
        beginScope()

        scopes.peek()["this"] = true
        statement.methods.forEach {
            resolveFunction(it, if (it.name.lexeme === "init") FunctionType.INITIALIZER else FunctionType.METHOD)
        }

        endScope()
        statement.supperClass?.let { endScope() }
        currentClass = enclosingClass
        return null
    }

    override fun visitExprStatement(statement: Expr): Void? {
        resolve(statement.expr)

        return null
    }

    override fun visitIfStatement(statement: If): Void? {
        resolve(statement.condition)
        resolve(statement.thenBranch)
        statement.elseBranch?.let {
            resolve(it)
        }

        return null
    }

    override fun visitPrintStatement(statement: Print): Void? {
        resolve(statement.expr)
        return null
    }

    override fun visitFuncStatement(statement: Func): Void? {
        declare(statement.name)
        define(statement.name)
        resolveFunction(statement, FunctionType.FUNCTION)

        return null
    }

    override fun visitReturnStatement(statement: Return): Void? {
        if (currentFunction == FunctionType.NONE) {
            throw LoxTopReturnException()
        }
        statement.value?.let {
            resolve(it)
        }

        return null
    }

    override fun visitVarStatement(statement: Var): Void? {
        declare(statement.name)
        if (statement.init != null) {
            resolve(statement.init)
        }
        define(statement.name)

        return null
    }

    override fun visitWhileStatement(statement: While): Void? {
        resolve(statement.condition)
        resolve(statement.body)

        return null
    }

    internal fun resolve(stats: List<Statement>) {
        stats.forEach {
            resolve(it)
        }
    }

    private fun resolve(stat: Statement) {
        stat.accept(this)
    }

    private fun resolve(expr: Expression) {
        expr.accept(this)
    }

    private fun beginScope() {
        scopes.push(mutableMapOf())
    }

    private fun endScope() {
        scopes.pop()
    }

    private fun declare(name: Token) {
        if (!scopes.isEmpty()) {
            val scope = scopes.peek()
            if (scope.containsKey(name.lexeme)) {
                throw LoxDeclareDuplicateException()
            }
            scope[name.lexeme] = false
        }
    }

    private fun define(name: Token) {
        if (!scopes.isEmpty()) {
            val scope = scopes.peek()
            scope[name.lexeme] = true
        }
    }

    private fun resolveLocal(expr: Expression, name: Token) {
        for (i in (scopes.size - 1) downTo 0) {
            if (scopes[i].containsKey(name.lexeme)) {
                interperter.resolve(expr, scopes.size - 1 - i)
            }
        }
    }

    private fun resolveFunction(func: Func, type: FunctionType) {
        val enclosingFunction = currentFunction
        currentFunction = type
        beginScope()

        for (param in func.params) {
            declare(param)
            define(param)
        }

        resolve(func.body)

        endScope()
        currentFunction = enclosingFunction
    }
}
