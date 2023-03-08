package dev.yidafu.kotlin.lox // ktlint-disable filename

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
        TODO("Not yet implemented")
    }

    override fun visitBinaryExpression(expression: Binary): String {
        return parenthesize(expression.operator.lexeme, expression.left, expression.right)
    }

    override fun visitGroupingExpression(expression: Grouping): String {
        return parenthesize("group", expression.expr)
    }

    override fun visitLiteralExpression(expression: Literal): String {
        return expression.value.toString()
    }

    override fun visitUnaryExpression(expression: Unary): String {
        return parenthesize(expression.operator.lexeme, expression.right)
    }

    override fun visitVariableExpression(expression: Variable): String {
        TODO("Not yet implemented")
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

    override fun visitExprStatement(statement: Expr): String {
        return "(expr ${statement.expr.accept(this)})"
    }

    override fun visitPrintStatement(statement: Print): String {
        return "(print ${statement.expr.accept(this)})"
    }

    override fun visitVarStatement(statement: Var): String {
        return "(var[${statement.name}] ${statement.init?.accept(this)})"
    }
}
