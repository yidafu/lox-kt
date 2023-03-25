package dev.yidafu.kotlin.lox.vm

import org.junit.jupiter.api.Test

class VMTest {

    @Test
    fun binaryPlusTest() {
        val vm = VM(mainFun = FunctionObject(0, "main"))

        vm.frame.chunk.addConstant(LoxValue.LoxNumber(2.3), 1)
        vm.frame.chunk.addConstant(LoxValue.LoxNumber(3.4), 1)
        vm.frame.chunk.write(OpCode.OpAdd, 1)
        vm.frame.chunk.write(OpCode.OpPrint, 2)

        vm.decompile()
        vm.reset()
        println("======= Execute Byte Code =======")
        vm.exec()
    }
}
