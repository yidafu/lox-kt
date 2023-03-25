package dev.yidafu.kotlin.lox.vm

import dev.yidafu.kotlin.lox.common.LoxMaxOffsetLimitException
import dev.yidafu.kotlin.lox.common.unreachable
import dev.yidafu.kotlin.lox.vm.OpCode.*

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

    fun write(opCode: OpCode, line: Int) {
        codes.add(opCode.toByte())
        lines.add(line)
    }

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

    fun write(bytes: List<Byte>, line: Int) {
        codes.addAll(bytes)
    }

//    fun write(bytes: List<OpCode>, line: Int) {
//        codes.addAll(bytes.map{ it.toByte() })
//    }

    fun writeJump(byte: Byte): Int {
        codes.addAll(listOf(byte, (0xff).toByte(), (0xff).toByte()))
        return codes.size - 2
    }
    fun writeJump(opCode: OpCode): Int {
        return writeJump(opCode.toByte())
    }

    fun patchJump(offset: Int) {
        val thenBlockLen = codes.size - offset - 2
        if (thenBlockLen > Short.MAX_VALUE) {
            throw LoxMaxOffsetLimitException()
        }

        codes[offset] = ((thenBlockLen shr 8) and 0xff).toByte()
        codes[offset + 1] = ((thenBlockLen) and 0xff).toByte()
    }

    fun writeLoop(loopStart: Int) {
        write(OpLoop, -1)

        val offset = codes.size - loopStart + 2
        write(((offset shr 8) and 0xff).toByte(), -1)
        write((offset and 0xff).toByte(), -1)
    }

    fun addConstant(value: LoxValue<Any>, line: Int) {
        this.constants.add(value)
        val top = this.constants.size - 1
        write(OpConstant, line)
        write(top.toByte(), line)
    }
}
