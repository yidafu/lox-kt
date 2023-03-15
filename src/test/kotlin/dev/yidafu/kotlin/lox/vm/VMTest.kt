package dev.yidafu.kotlin.lox.vm

import org.junit.jupiter.api.Test

class VMTest {

    @Test
    fun vmTest() {
        val vm = VM(Chunk())
        vm.chunk.addConstant(2.3, 1)
        vm.chunk.addConstant(3.4, 1)
        vm.chunk.write(OpCode.OpAdd.toByte(), 1)
        vm.chunk.write(OpCode.OpReturn.toByte(), 2)

        vm.decompile()

        vm.exec()
    }
}
