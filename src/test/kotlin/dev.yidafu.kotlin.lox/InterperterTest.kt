package dev.yidafu.kotlin.lox

import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut
import kotlin.test.Test
import kotlin.test.assertEquals

class InterperterTest {
    private fun parse(src: String): List<Statement> {
        val tokens = Scanner(src).scanTokens()
        return Parser(tokens).parse()
    }

    @Test
    fun `should print true string to std out`() {
        val output1 = tapSystemOut {
            Interperter().interpert(parse("print true;"))
        }
        assertEquals("true", output1.trim())
    }

    @Test
    fun `should print number 6 to std out`() {
        val output2 = tapSystemOut {
            Interperter().interpert(parse("print  1 + 2 * 3 - 1;"))
        }
        assertEquals("6.0", output2.trim())
    }

    @Test
    fun `declare variable 'a' then print 'a' should print '2'`() {
        val output = tapSystemOut {
            Interperter().interpert(parse("var a  = 2; print a;"))
        }
        assertEquals("2.0", output.trim())
    }

    @Test
    fun `block scope should shadow variable`() {
        val output = tapSystemOut {
            Interperter().interpert(parse("var a  = 2; { var a = 3; print a; } print a;"))
        }
        assertEquals("3.0\n2.0", output.trim())
    }

    @Test
    fun `short circuit operator should print true`() {
        val output = tapSystemOut {
            Interperter().interpert(
                parse(
                    """
                print "hi" or 2;
                print nil or "yes";
                print true and 3;
                    """.trimIndent(),
                ),
            )
        }
        assertEquals("hi\nyes\n3.0", output.trim())
    }

    @Test
    fun `should print a if condition is true`() {
        val output = tapSystemOut {
            Interperter().interpert(
                parse(
                    """
                    if (true) print "a"; else print "b";
                    """.trimIndent(),
                ),
            )
        }

        assertEquals("a", output.trim())
    }

    @Test
    fun `should print a 3 time (while statement)`() {
        val output = tapSystemOut {
            Interperter().interpert(
                parse(
                    """
                    var a = 1;
                     while (a < 4) {
                       print a;
                       a = a + 1;
                     }
                    """.trimIndent(),
                ),
            )
        }

        assertEquals("1.0\n2.0\n3.0", output.trim())
    }

    @Test
    fun `should print a 3 time (for statement)`() {
        val output = tapSystemOut {
            Interperter().interpert(
                parse(
                    """
                    for (var a = 1; a < 4; a = a + 1) {
                        print a;
                    }
                    """.trimIndent(),
                ),
            )
        }

        assertEquals("1.0\n2.0\n3.0", output.trim())
    }
}
