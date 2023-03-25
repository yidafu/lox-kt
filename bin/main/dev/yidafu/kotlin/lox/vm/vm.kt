package dev.yidafu.kotlin.lox.vm

import dev.yidafu.kotlin.lox.common.LoxCallableException
import dev.yidafu.kotlin.lox.common.unreachable
import dev.yidafu.kotlin.lox.vm.LoxValue.*
import dev.yidafu.kotlin.lox.vm.OpCode.*
import java.util.Stack

class VM(
    val frames: Stack<CallFrame> = Stack(),
//    val chunk: Chunk,
    val stack: Stack<LoxValue<Any>> = Stack(),
    private val globals: MutableMap<String, LoxValue<Any>> = mutableMapOf(),
) {

    private val frame: CallFrame
        get() = frames.peek()
    val chunk: Chunk
        get() = frame.function.chunk

    private var ip: Int
        get() = chunk.ip
        set(ip) { chunk.ip = ip }

    fun exec() {
        while (true) {
            if (ip >= chunk.codes.size) break

            val opCode = chunk.readOpCode()

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
                    val index = chunk.readByte()
                    increment()
                    push(frame.slots[index.toInt()])
                }
                OpSetLocal -> {
                    val index = chunk.readByte()
                    frame.slots[index.toInt()] = peek()
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

                OpCall -> {
                    val argCount = chunk.readByte()
                    callValue(peek((argCount + 1).toByte()), argCount)
                }
            }
        }
    }

    fun getValue(): LoxValue<Any> {
        increment()
        val index = chunk.readByte().toInt()
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
        frame.slots.clear()
    }

    fun push(value: LoxValue<Any>) {
        stack.push(value)
    }

    fun pop(): LoxValue<Any> {
        return stack.pop()
    }

    private fun peek(n: Byte = 1): LoxValue<Any> {
        val index = stack.size - n
        return stack[index]
    }

    fun increment(step: Int = 1): Int {
        ip += step
        return ip
    }

    fun callValue(callee: LoxValue<Any>, argCount: Byte) {
        when (callee) {
            is LoxFunction -> {
                call(callee, argCount)
            }
            else -> throw LoxCallableException()
        }
    }

    fun call(callee: LoxFunction, argCount: Byte) {
        val top = stack.size
        val frame = CallFrame(
            callee.value,
            StackSlice(stack, top - 1 - argCount),
        )

        frames.push(frame)
    }
}
