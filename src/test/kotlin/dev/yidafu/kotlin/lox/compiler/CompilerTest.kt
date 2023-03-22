package dev.yidafu.kotlin.lox.compiler

import com.github.stefanbirkner.systemlambda.SystemLambda
import dev.yidafu.kotlin.lox.interperter.Interperter
import dev.yidafu.kotlin.lox.interperter.Resolver
import dev.yidafu.kotlin.lox.parser.Parser
import dev.yidafu.kotlin.lox.parser.Scanner
import dev.yidafu.kotlin.lox.vm.Chunk
import dev.yidafu.kotlin.lox.vm.VM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CompilerTest {
    private fun execute(srcCode: String): String {
        return SystemLambda.tapSystemOut {
            val stats = Parser(Scanner((srcCode)).scanTokens()).parse()
            val compiler = Compiler(Chunk())
            compiler.compile(stats)
            val vm = VM(compiler.chunk)
            vm.exec()
        }.trim()
    }

    @Test
    fun binaryTest() {
        val output = execute("1.2 + 3; return;")
        assertEquals("[LoxNumber] 4.2", output)
    }
}