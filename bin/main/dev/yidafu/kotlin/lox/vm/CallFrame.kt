package dev.yidafu.kotlin.lox.vm

import dev.yidafu.kotlin.lox.common.unreachable
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
    private val function: FunctionObject,
    val slots: StackSlice,
    var ip: Int = 0,
) {
    val constants: List<LoxValue<Any>>
        get() = function.chunk.constants
    val codes: List<Byte>
        get() = function.chunk.codes

    val chunk: Chunk
        get() = function.chunk

    fun readOpCode(): OpCode {
        return (OpCode from codes[ip]) ?: unreachable()
    }

    fun readByte(): Byte {
        return codes[ip]
    }

    fun readShort(): Short {
        val offset = ((codes[ip].toInt() shr 8) or (codes[ip + 1].toInt())).toShort()
        ip += 2
        return offset
    }
}
