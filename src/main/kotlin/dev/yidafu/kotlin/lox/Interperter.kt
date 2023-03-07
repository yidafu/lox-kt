package dev.yidafu.kotlin.lox

class Interperter : Visitor<Any> {

    fun interpert(expr: Expression): Any {
        return evaluate(expr)
    }

    override fun visitBinaryExpression(expression: Binary): Any {
        val left = evaluate(expression.left)
        val right = evaluate(expression.right)

        return when (expression.operator.type) {
            // TODO: > < >= <= != ==
            TokenType.MINUS -> {
                return (left as Double) - (right as Double)
            }
            TokenType.PLUS ->
                when {
                    left is Double && right is Double -> {
                        return left + right
                    }
                    left is String && right is String -> {
                        return left + right
                    }
                    else -> throw Exception("unreachable")
                }
            TokenType.SLASH -> {
                return (left as Double) / (right as Double)
            }

            TokenType.STAR -> {
                return (left as Double) * (right as Double)
            }
            else -> throw Exception("unreachable")
        }
    }

    override fun visitGroupingExpression(expression: Grouping): Any {
        return evaluate(expression.expr)
    }

    override fun visitLiteralExpression(expression: Literal): Any {
        return expression.value
    }

    override fun visitUnaryExpression(expression: Unary): Any {
        val right = evaluate(expression.right)

        return when (expression.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> -(right as Double)
            else -> throw Exception("unreachable")
        }
    }

    private fun isTruthy(value: Any): Boolean {
        return when (value) {
            is Boolean -> value
            else -> false
        }
    }
    private fun evaluate(expression: Expression): Any {
        return expression.accept(this)
    }
}
