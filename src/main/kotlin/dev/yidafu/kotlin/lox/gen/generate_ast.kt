package dev.yidafu.kotlin.lox.gen

import java.io.PrintWriter

val OUT_PUT_DIR = "/Users/dovyih/Codes/lox-kt/src/main/kotlin/dev/yidafu/kotlin/lox"
val types = listOf<String>(
    "Binary  | left: Expression, operator: Token, right: Expression",
    "Grouping          | expr: Expression",
    "Literal           | value: Any",
    "Unary             | operator: Token, right: Expression",
)

fun main(args: Array<String>) {
    //
    defineAst(OUT_PUT_DIR, "Expression", types)
}

fun defineAst(output: String, basename: String, types: List<String>) {
    val path: String = "$OUT_PUT_DIR/expression.kt"
    val writer = PrintWriter(path, "UTF-8")
    writer.println("package dev.yidafu.kotlin.lox")
    writer.println("")

    writer.println("interface Visitor<R> {")
    // define visitor
    types.forEach {
        val typeName = it.split("|")[0].trim()
        writer.println("    fun visit$typeName$basename(${basename.lowercase()}: $typeName): R")
    }
    writer.println("}\n")
    writer.println(
        """
        |
        |abstract class $basename {
        |    abstract fun <R> accept(visitor: Visitor<R>): R
        |}
        |
        """.trimMargin(),
    )

    // define exprssion type
    types.forEach {
        val className = it.split("|")[0].trim()
        val fields = it
            .split("|")[1]
            .trim()
            .split(',')
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
