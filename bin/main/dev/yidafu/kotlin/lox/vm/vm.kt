package dev.yidafu.kotlin.lox.vm

import java.util.Stack

class VM(
    val chunk: Chunk,
    val stack: Stack<LoxValue<Any>> = Stack(),
) {

    fun exec() {
        while (true) {
            if (chunk.ip >= chunk.codes.size) break

            val opCode = OpCode from chunk.codes[chunk.ip]
            opCode?.exec(this)
        }
    }

    fun decompile() {
        while (true) {
            if (chunk.ip >= chunk.codes.size) break

            val opCode = OpCode from chunk.codes[chunk.ip]

            opCode?.decompile(this)
        }
    }

    fun reset() {
        chunk.ip = 0
        stack.clear()
    }

    fun push(value: LoxValue<Any>) {
        stack.push(value)
    }

    fun pop(): LoxValue<Any> {
        return stack.pop()
    }

    fun increment(step: Int = 1): Int {
        chunk.ip += step
        return chunk.ip
    }
}
