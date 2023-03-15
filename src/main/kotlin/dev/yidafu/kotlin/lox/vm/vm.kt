package dev.yidafu.kotlin.lox.vm

import java.util.Stack

class VM(
    val chunk: Chunk,
    val stack: Stack<Value> = Stack(),
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

    fun push(value: Value) {
        stack.push(value)
    }

    fun pop(): Value {
        return stack.pop()
    }

    fun increment(step: Int = 1): Int {
        chunk.ip += step
        return chunk.ip
    }
}
