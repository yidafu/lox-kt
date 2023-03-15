package dev.yidafu.kotlin.lox.vm

typealias Value = Double

class Chunk(
    /**
     * instruction pointer
     */
    var ip: Int = 0,
    val codes: MutableList<Byte> = mutableListOf(),
    val constants: MutableList<Value> = mutableListOf(),
    val lines: MutableList<Int> = mutableListOf(),
) {
    fun write(byte: Byte, line: Int) {
        codes.add(byte)
        lines.add(line)
    }


    fun write(vararg bytes: Byte, line: Int) {
        codes.addAll(bytes.toList())
    }

    fun addConstant(value: Value, line: Int) {
        this.constants.add(value)
        val top = this.constants.size - 1
        write(OpCode.OpConstant.toByte(), line)
        write(top.toByte(), line)
    }
}
