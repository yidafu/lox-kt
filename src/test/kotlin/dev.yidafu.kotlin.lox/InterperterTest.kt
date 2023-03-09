package dev.yidafu.kotlin.lox

import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut
import kotlin.test.Test
import kotlin.test.assertEquals

class InterperterTest {

    private fun execute(srcCode: String): String {
        return tapSystemOut {
            Interperter().interpert(Parser(Scanner((srcCode)).scanTokens()).parse())
        }.trim()
    }

    @Test
    fun `should print true string to std out`() {
        val output = execute("print true;")
        assertEquals("true", output)
    }

    @Test
    fun `should print number 6 to std out`() {
        val output = execute("print  1 + 2 * 3 - 1;")
        assertEquals("6.0", output)
    }

    @Test
    fun `declare variable 'a' then print 'a' should print '2'`() {
        val output = execute("var a  = 2; print a;")
        assertEquals("2.0", output.trim())
    }

    @Test
    fun `block scope should shadow variable`() {
        val output = execute("var a  = 2; { var a = 3; print a; } print a;")
        assertEquals("3.0\n2.0", output.trim())
    }

    @Test
    fun `short circuit operator should print true`() {
        val output = execute(
            """
                print "hi" or 2;
                print nil or "yes";
                print true and 3;
                    """,
        )
        assertEquals("hi\nyes\n3.0", output)
    }

    @Test
    fun `should print a if condition is true`() {
        val output = execute(
            """
                    if (true) print "a"; else print "b";
                    """,
        )

        assertEquals("a", output)
    }

    @Test
    fun `should print a 3 time (while statement)`() {
        val output = execute(
            """
                    var a = 1;
                     while (a < 4) {
                       print a;
                       a = a + 1;
                     }
                    """,
        )

        assertEquals("1.0\n2.0\n3.0", output.trim())
    }

    @Test
    fun `should print a 3 time (for statement)`() {
        val output = execute(
            """
                    for (var a = 1; a < 4; a = a + 1) {
                        print a;
                    }
                    """,
        )

        assertEquals("1.0\n2.0\n3.0", output.trim())
    }

    @Test
    fun `native clock() function`() {
        val output = execute("print clock();")
        assert(output.isNotEmpty())
    }

    @Test
    fun `define a function then call it`() {
        val output = execute(
            """
            fun add(a, b, c) {
                print a + b + c;
            }
            add(1, 2, 3);
            """.trimIndent(),
        )

        assertEquals("6.0", output)
    }

    @Test
    fun `nest 'count' function call`() {
        val output = execute(
            """
            fun count(n) {
                if (n > 1) count(n - 1);
                print n;
            }
            count(3);
            """.trimIndent(),
        )

        assertEquals("1.0\n2.0\n3.0", output)
    }

    @Test
    fun `fibonacci recursion implement`() {
        val output = execute(
            """
        fun fib(n) {
            if (n <= 1) return n;
            return fib(n - 2) + fib(n - 1);
        }
        print fib(6);
            """.trimIndent(),
        )
        fun fib(n: Double): Double {
            if (n <= 1) return n
            return fib(n - 2) + fib(n - 1)
        }

        assertEquals(fib(6.0).toString(), output)
    }

    @Test
    fun `function closure`() {
        val output = execute(
            """
        fun makeCounter() {
            var i = 0;
            fun count() {
                i = i + 1;
                print i;
            }
            return count;
        }
        var counter = makeCounter();
        counter();
        counter();
            """.trimIndent(),
        )

        assertEquals("1.0\n2.0", output)
    }
}
