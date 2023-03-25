package dev.yidafu.kotlin.lox.vm

import dev.yidafu.kotlin.lox.compiler.Local

class FunctionObject(
    val arity: Int = 0,
    val funcName: String = "anonymous",
    val chunk: Chunk = Chunk(),
)
