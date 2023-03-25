package dev.yidafu.kotlin.lox.compiler

import com.github.stefanbirkner.systemlambda.SystemLambda
import dev.yidafu.kotlin.lox.parser.Parser
import dev.yidafu.kotlin.lox.parser.Scanner
import dev.yidafu.kotlin.lox.vm.CallFrame
import dev.yidafu.kotlin.lox.vm.StackSlice
import dev.yidafu.kotlin.lox.vm.VM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CompilerTest {
    private fun execute(srcCode: String): String {
        return SystemLambda.tapSystemOut {
            val stats = Parser(Scanner((srcCode)).scanTokens()).parse()
            val compiler = Compiler()
            val mainFuncObj = compiler.compile(stats)
            val vm = VM(mainFuncObj)
//            vm.decompile()
//            vm.reset()
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

    @Test
    fun variableDeclareTest() {
        val output = execute(
            """
            var a = "ing";
            print "str" + a;
            """.trimIndent()
        )
        assertEquals("[LoxString] string", output)
    }

    @Test
    fun blockScopeTest() {
        val output = execute(
            """
            var a = "outer-";
            {
                var b = "inner";
                print a + b;
            }
            """.trimIndent()
        )
        assertEquals("[LoxString] outer-inner", output)
    }

    @Test
    fun ifStatementTest() {
        val output = execute(
            """
           if (true) {
                print "then";
           } else {
                print "else";
           }
           print "end if";
            """.trimIndent()
        )
        assertEquals("[LoxString] then[LoxString] end if", output)
    }

    @Test
    fun whileStatementTest() {
        val output = execute(
            """
            var a = 1;
            while (a < 3) {
                print a;
                a = a + 1;
            }
            """.trimIndent()
        )
        assertEquals("[LoxNumber] 1.0[LoxNumber] 2.0", output)
    }
    @Test
    fun forStatementTest() {
        val output = execute(
            """
            for (var i = 1; i < 3; i = i + 1) {
                print i;
            }
            """.trimIndent()
        )
        assertEquals("[LoxNumber] 1.0[LoxNumber] 2.0", output)
    }

    @Test
    fun funcDeclareTest() {

        val output = execute(
            """
            fun foo(a, b) {
                print a + b;
            }
            foo(1, 2);
            """.trimIndent()
        )
        assertEquals("[LoxNumber] 3.0", output)
    }

    @Test
    fun funcReturnValueTest() {

        val output = execute(
            """
            fun foo(a, b) {
                return a + b;
            }
            print foo(1, 2) + foo(3, 4);
            """.trimIndent()
        )
        assertEquals("[LoxNumber] 10.0", output)
    }

    @Test
    fun fibTest() {

        fun fib(a: Double): Double {
            if (a == 1.0) return 1.0
            if (a == 2.0) return 2.0
            return fib(a - 1) + fib(a - 2)
        }
        val output = execute(
            """
            fun fib(a) {
                if (a == 1) return 1;
                if (a == 2) return 2;
                return fib(a - 1) + fib(a - 2);
            }
            print fib(5);
            """.trimIndent()
        )
        assertEquals("[LoxNumber] 8.0", "[LoxNumber] ${fib(5.0)}")
    }
}
