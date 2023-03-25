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

    fun readShort(): Short {
        val offset = ((codes[ip].toInt() shr 8) or (codes[ip + 1].toInt())).toShort()
        ip += 2
        return offset
    }

    fun write(bytes: List<Byte>, line: Int) {
        codes.addAll(bytes)
    }

    fun writeJump(byte: Byte): Int {
        codes.addAll(listOf(byte, (0xff).toByte(), (0xff).toByte()))
        return codes.size - 2
    }

    fun addConstant(value: LoxValue<Any>, line: Int) {
        this.constants.add(value)
        val top = this.constants.size - 1
        write(OpCode.OpConstant.toByte(), line)
        write(top.toByte(), line)
    }
}
