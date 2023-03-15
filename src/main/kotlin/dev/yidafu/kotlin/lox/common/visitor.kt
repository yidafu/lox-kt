package dev.yidafu.kotlin.lox.common

// ktlint-disable filename

class AstPrinter : Expression.Visitor<String>, Statement.Visitor<String> {
    fun print(stats: List<Statement>): String {
        return stats.joinToString("\n") {
            print(it)
        }
    }

    fun print(expr: Statement): String {
        return expr.accept(this)
    }
    fun print(expr: Expression): String {
        return expr.accept(this)
    }

    override fun visitAssignExpression(expression: Assign): String {
        return "(${expression.name.lexeme} = ${expression.value.accept(this)})"
    }

    override fun visitBinaryExpression(expression: Binary): String {
        return parenthesize(expression.operator.lexeme, expression.left, expression.right)
    }

    override fun visitFunCallExpression(expression: FunCall): String {
        TODO("Not yet implemented")
    }

    override fun visitGetExpression(expression: Get): String {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpression(expression: Grouping): String {
        return parenthesize("group", expression.expr)
    }

    override fun visitLiteralExpression(expression: Literal): String {
        return expression.value.toString()
    }

    override fun visitLogicalExpression(expression: Logical): String {
        return "(${expression.left.accept(this)} ${expression.operator.lexeme} ${expression.right.accept(this)})"
    }

    override fun visitUnaryExpression(expression: Unary): String {
        return parenthesize(expression.operator.lexeme, expression.right)
    }

    override fun visitSetExpression(expression: Set): String {
        TODO("Not yet implemented")
    }

    override fun visitSuperExpression(expression: Super): String {
        TODO("Not yet implemented")
    }

    override fun visitThisExpression(expression: This): String {
        TODO("Not yet implemented")
    }

    override fun visitVariableExpression(expression: Variable): String {
        return "[${expression.name.lexeme}]"
    }

    private fun parenthesize(name: String, vararg exprs: Expression): String {
        val builder = StringBuilder()
        builder.apply {
            append("($name")
            exprs.forEach {
                append(" ${it.accept(this@AstPrinter)}")
            }
            append(")")
        }

        return builder.toString()
    }

    override fun visitBlockStatement(statement: Block): String {
        return "{ ${statement.statements.joinToString("  ") { it.accept(this) }} }"
    }

    override fun visitClassStatement(statement: Class): String {
        TODO("Not yet implemented")
    }

    override fun visitExprStatement(statement: Expr): String {
        return "(expr ${statement.expr.accept(this)})"
    }

    override fun visitIfStatement(statement: If): String {
        return """
            |(if (${statement.condition.accept(this)})
            |    then ${statement.thenBranch.accept(this)}
            |    else ${statement.elseBranch?.accept(this)}
            |)
        """.trimMargin()
    }

    override fun visitPrintStatement(statement: Print): String {
        return "(print ${statement.expr.accept(this)})"
    }

    override fun visitFuncStatement(statement: Func): String {
        TODO("Not yet implemented")
    }

    override fun visitReturnStatement(statement: Return): String {
        TODO("Not yet implemented")
    }

    override fun visitVarStatement(statement: Var): String {
        return "(var [${statement.name.lexeme}] = ${statement.init?.accept(this)})"
    }

    override fun visitWhileStatement(statement: While): String {
        return "while (${statement.condition.accept(this)}) ${statement.body.accept(this)}"
    }
}
