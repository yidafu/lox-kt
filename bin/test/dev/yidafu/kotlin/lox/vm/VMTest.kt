package dev.yidafu.kotlin.lox.vm

import org.junit.jupiter.api.Test

class VMTest {

    @Test
    fun binaryPlusTest() {
        val vm = VM(Chunk())
        vm.chunk.addConstant(LoxValue.LoxNumber(2.3), 1)
        vm.chunk.addConstant(LoxValue.LoxNumber(3.4), 1)
        vm.chunk.write(OpCode.OpAdd.toByte(), 1)
        vm.chunk.write(OpCode.OpReturn.toByte(), 2)

        vm.decompile()
        vm.reset()
        println("======= Execute Byte Code =======")
        vm.exec()
    }
}
