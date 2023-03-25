package dev.yidafu.kotlin.lox.vm

import dev.yidafu.kotlin.lox.common.unreachable

class Chunk(
    /**
     * instruction pointer
     */
    var ip: Int = 0,
    val codes: MutableList<Byte> = mutableListOf(),
    val constants: MutableList<LoxValue<Any>> = mutableListOf(),
    val lines: MutableList<Int> = mutableListOf(),
) {
    fun write(byte: Byte, line: Int) {
        codes.add(byte)
        lines.add(line)
    }

    fun peekOpCode(): OpCode {
        return (OpCode from codes[ip]) ?: unreachable()
    }

    fun peekByte(): Byte {
        return codes[ip]
    }

    fun write(bytes: List<Byte>, line: Int) {
        codes.addAll(bytes)
    }

    fun addConstant(value: LoxValue<Any>, line: Int) {
        this.constants.add(value)
        val top = this.constants.size - 1
        write(OpCode.OpConstant.toByte(), line)
        write(top.toByte(), line)
    }
}
