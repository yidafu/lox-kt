package dev.yidafu.kotlin.lox.vm

import java.util.Stack

class StackSlice(
    private val originStack: Stack<LoxValue<Any>>,
    private var offset: Int,
) {
    operator fun get(index: Int): LoxValue<Any> {
        return originStack[offset + 1 + index]
    }

    operator fun set(index: Int, value: LoxValue<Any>) {
        originStack[offset + 1 + index] = value
    }

    fun clear() {
        while (originStack.size > offset) {
            originStack.pop()
        }
    }
}

class CallFrame(
    val function: FunctionObject,
    val slots: StackSlice,
    var ip: Int = 0,
)
