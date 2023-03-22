package dev.yidafu.kotlin.lox.compiler

import com.github.stefanbirkner.systemlambda.SystemLambda
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
        val output = execute("print 1.2 + 3;")
        assertEquals("[LoxNumber] 4.2", output)
    }

    @Test
    fun stringPlusTest() {
        val output = execute(
            """
            print "st" + "ri" + "ng";
            """
        )
        assertEquals("[LoxString] string", output)
    }

    @Test
    fun binaryEqualTest() {
        val output = execute("print 2 < 3;")
        assertEquals("[LoxBool] true", output)

        val output2 = execute("print 2 >= 3;")
        assertEquals("[LoxBool] false", output2)
    }
}
