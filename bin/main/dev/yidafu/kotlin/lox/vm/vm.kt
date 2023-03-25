package dev.yidafu.kotlin.lox.vm

import dev.yidafu.kotlin.lox.common.unreachable
import dev.yidafu.kotlin.lox.vm.LoxValue.*
import dev.yidafu.kotlin.lox.vm.OpCode.*
import java.util.Stack

class VM(
    val chunk: Chunk,
    val stack: Stack<LoxValue<Any>> = Stack(),
    private val globals: MutableMap<String, LoxValue<Any>> = mutableMapOf(),
) {
    var ip: Int
        get() = chunk.ip
        set(ip) { chunk.ip = ip }
    fun exec() {
        while (true) {
            if (ip >= chunk.codes.size) break

            val opCode = chunk.peekOpCode()

            increment()
            when (opCode) {
                OpReturn -> {
                    ip = chunk.codes.size
                }
                OpConstant -> {
                    val cIdx = chunk.codes[ip].toInt()
                    push(chunk.constants[cIdx])
                    increment()
                }
                OpNegate -> {
                    when (val value = pop()) {
                        is LoxNumber -> {
                            push(-value)
                        }
                        else -> unreachable()
                    }
                }
                OpAdd -> {
                    binaryOp(this) {
                        val (a, b) = it
                        when {
                            (a is LoxNumber && b is LoxNumber) -> {
                                a + b
                            }
                            (a is LoxString && b is LoxString) -> {
                                a + b
                            }
                            else -> unreachable()
                        }
                    }
                }
                OpSubtract -> {
                    binaryOp(this) {
                        val (a, b) = it
                        when {
                            (a is LoxNumber && b is LoxNumber) -> {
                                a - b
                            }
                            else -> unreachable()
                        }
                    }
                }
                OpMultiply -> {
                    binaryOp(this) {
                        val (a, b) = it
                        when {
                            (a is LoxNumber && b is LoxNumber) -> {
                                a * b
                            }
                            else -> unreachable()
                        }
                    }
                }
                OpDivide -> {
                    binaryOp(this) {
                        val (a, b) = it
                        when {
                            (a is LoxNumber && b is LoxNumber) -> {
                                a * b
                            }
                            else -> unreachable()
                        }
                    }
                }
                OpFalse -> {
                    push(LoxBool(false))
                }
                OpTrue -> {
                    push(LoxBool(true))
                }
                OpNil -> {
                    push(LoxNil())
                }
                OpNot -> {
                    when (val value = pop()) {
                        is LoxBool -> {
                            push(value.isFalsely())
                        }
                        else -> unreachable()
                    }
                }
                OpEqual -> {
                    binaryOp(this) {
                        LoxBool(it.first == it.second)
                    }
                }
                OpGreater -> {
                    binaryOp(this) {
                        LoxBool(it.first > it.second)
                    }
                }
                OpLess -> {
                    binaryOp(this) {
                        LoxBool(it.first < it.second)
                    }
                }
                OpPrint -> {
                    printValue(pop())
                }

                OpPop -> {
                    pop()
                }

                OpDefineGlobal -> {
                    val name = getStringValue()
                    globals[name.value] = peek()
                    pop()
                }
                OpSetGlobal -> {
                    val name = getStringValue()
                    globals[name.value] = peek()
                }
                OpGetGlobal -> {
                    val name = getStringValue()
                    val value = globals[name.value] ?: unreachable()
                    push(value)
                }

                OpGetLocal -> {
                    val index = chunk.peekByte()
                    increment()
                    push(stack[index.toInt()])
                }
                OpSetLocal -> {
                    val index = chunk.peekByte()
                    stack[index.toInt()] = peek()
                    increment()
                }

                OpJumpIfFalse -> {
                    val offset = chunk.readShort()
                    if (peek().isFalsely().value) {
                        ip += offset.toInt()
                    }
                }

                OpJump -> {
                    val offset = chunk.readShort()
                    ip += offset.toInt()
                }

                OpLoop -> {
                    val offset = chunk.readShort()
                    ip -= offset
                }
            }
        }
    }

    fun getValue(): LoxValue<Any> {
        increment()
        val index = chunk.peekByte().toInt()
        return chunk.constants[index]
    }

    private fun getStringValue(): LoxString = when (val value = getValue()) {
        is LoxString -> {
            increment()
            value
        }
        else -> unreachable()
    }

    fun decompile() {
        while (true) {
            if (ip >= chunk.codes.size) break

            val opCode = OpCode from chunk.codes[ip]

            opCode?.decompile(this)
        }
    }

    fun reset() {
        ip = 0
        stack.clear()
    }

    fun push(value: LoxValue<Any>) {
        stack.push(value)
    }

    fun pop(): LoxValue<Any> {
        return stack.pop()
    }

    private fun peek(): LoxValue<Any> {
        return stack.peek()
    }

    fun increment(step: Int = 1): Int {
        ip += step
        return ip
    }
}
