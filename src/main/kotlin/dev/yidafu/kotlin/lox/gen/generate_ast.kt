package dev.yidafu.kotlin.lox.gen // ktlint-disable filename

import java.io.PrintWriter

val OUT_PUT_DIR = "/Users/dovyih/Codes/lox-kt/src/main/kotlin/dev/yidafu/kotlin/lox"
val exprTypes = mapOf<String, List<String>>(
    "Assign" to listOf("name: Token", "value: Expression"),
    "Binary" to listOf("left: Expression", "operator: Token", "right: Expression"),
    "FunCall" to listOf("callee: Expression", "paren: Token", "args: List<Expression>"),
    "Grouping" to listOf("expr: Expression"),
    "Literal" to listOf("value: Any"),
    "Logical" to listOf("left: Expression", "operator: Token", "right: Expression"),
    "Unary" to listOf("operator: Token", "right: Expression"),
    "Variable" to listOf("name: Token"),
)

val statTypes = mapOf<String, List<String>>(
    "Block" to listOf("statements: List<Statement>"),
    "Expr" to listOf("expr: Expression"),
    "If" to listOf("condition: Expression", "thenBranch: Statement", "elseBranch: Statement?"),
    "Print" to listOf("expr: Expression"),
    "Func" to listOf("name: Token", "params: List<Token>", "body: List<Statement>"),
    "Var" to listOf("name: Token", "init: Expression?"),
    "While" to listOf("condition: Expression", "body: Statement"),
)

fun main() {

    defineAst(OUT_PUT_DIR, "Expression", exprTypes)

    defineAst(OUT_PUT_DIR, "Statement", statTypes)
}

fun defineAst(output: String, basename: String, types: Map<String, List<String>>) {
    val path: String = "$output/${basename.lowercase()}.kt"
    val writer = PrintWriter(path, "UTF-8")
    writer.println("package dev.yidafu.kotlin.lox")
    writer.println("")

    writer.println(
        """
        |
        |abstract class $basename {
        |    abstract fun <R> accept(visitor: Visitor<R>): R
        |
        """.trimMargin(),
    )

    writer.println("    interface Visitor<R> {")
    // define visitor
    types.forEach {
        val typeName = it.key
        writer.println("        fun visit$typeName$basename(${basename.lowercase()}: $typeName): R")
    }

    writer.println("    }\n")

    writer.println("}\n")

    // define exprssion type
    types.forEach {
        val className = it.key
        val fields = it.value
            .map { s -> s.trim() }.joinToString("\n    ") { s -> "val $s," }

        writer.println(
            """
            |class $className(
            |    $fields
            |) : $basename() {
            |    override fun <R> accept(visitor: Visitor<R>): R {
            |        return visitor.visit$className$basename(this);
            |    }
            |}
            |
            """.trimMargin(),
        )
    }

    writer.close()
}
