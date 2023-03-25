package dev.yidafu.kotlin.lox.vm

import dev.yidafu.kotlin.lox.common.LoxCallableException
import dev.yidafu.kotlin.lox.common.unreachable
import dev.yidafu.kotlin.lox.vm.LoxValue.*
import dev.yidafu.kotlin.lox.vm.OpCode.*
import java.util.Stack

enum class InterpretResult {
    CompileError,
    RuntimeError,
    Ok;
}

class VM(
    mainFun: FunctionObject,
    val frames: Stack<CallFrame> = Stack(),
    val stack: Stack<LoxValue<Any>> = Stack(),
    private val globals: MutableMap<String, LoxValue<Any>> = mutableMapOf(),
) {
    init {
        frames.push(CallFrame(mainFun, StackSlice(stack, -1)))
    }

    internal val frame: CallFrame
        get() = frames.peek()

    private var ip: Int
        get() = frame.ip
        set(ip) { frame.ip = ip }

    fun exec(): InterpretResult {
        while (true) {
            if (ip >= frame.codes.size) break

            val opCode = frame.readOpCode()

            increment()
            when (opCode) {
                OpReturn -> {
                    val result = pop()
                    val lastFrame = frames.pop()
                    if (frames.size == 0) {
//                        pop()
                        return InterpretResult.Ok
                    }
                    lastFrame.slots.clear()
                    push(result)
                }
                OpConstant -> {
                    val cIdx = frame.codes[ip].toInt()
                    push(frame.constants[cIdx])
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
                    val index = frame.readByte()
                    increment()
                    push(frame.slots[index.toInt()])
                }
                OpSetLocal -> {
                    val index = frame.readByte()
                    frame.slots[index.toInt()] = peek()
                    increment()
                }

                OpJumpIfFalse -> {
                    val offset = frame.readShort()
                    if (peek().isFalsely().value) {
                        ip += offset.toInt()
                    }
                }

                OpJump -> {
                    val offset = frame.readShort()
                    ip += offset.toInt()
                }

                OpLoop -> {
                    val offset = frame.readShort()
                    ip -= offset
                }

                OpCall -> {
                    val argCount = frame.readByte()
                    increment()
                    callValue(peek((argCount + 1).toByte()), argCount)
                }
            }
        }
        return InterpretResult.RuntimeError
    }

    fun getValue(): LoxValue<Any> {
        increment()
        val index = frame.readByte().toInt()
        return frame.constants[index]
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
            if (ip >= frame.codes.size) break

            val opCode = OpCode from frame.codes[ip]

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
