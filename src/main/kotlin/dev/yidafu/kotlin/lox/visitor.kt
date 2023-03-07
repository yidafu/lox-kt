package dev.yidafu.kotlin.lox // ktlint-disable filename

class AstPrinter : Visitor<String> {
    fun print(expr: Expression): String {
        return expr.accept(this)
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
}
