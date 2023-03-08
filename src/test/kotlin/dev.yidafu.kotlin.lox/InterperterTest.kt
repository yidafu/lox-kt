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
}
