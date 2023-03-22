package dev.yidafu.kotlin.lox.vm

import dev.yidafu.kotlin.lox.common.unreachable

// ktlint-disable filename

inline fun binaryOp(vm: VM, crossinline operator: (pair: Pair<LoxValue<Any>, LoxValue<Any>>) -> LoxValue<Any>) {
    val left = vm.pop()
    val right = vm.pop()
    vm.push(operator(Pair(left, right)))
}

enum class OpCode {
    OpReturn {
        override fun exec(vm: VM) {
            println(vm.pop())
            vm.chunk.ip = vm.chunk.codes.size
        }

        override fun decompile(vm: VM) {
            super.decompile(vm)
            // move ip to end of chunk
            vm.chunk.ip = vm.chunk.codes.size
            return
        }
    },

    OpConstant {
        override fun exec(vm: VM) {
            val chunk = vm.chunk
            val cIdx = chunk.codes[chunk.ip + 1].toInt()
            vm.push(chunk.constants[cIdx])
            vm.increment(2)
        }

        override fun decompile(vm: VM) {
            val chunk = vm.chunk
            val cIdx = chunk.codes[chunk.ip + 1].toInt()

            super.decompile(vm)
            print("\t")
            printValue(chunk.constants[cIdx])
            println()

            vm.increment()
        }
    },

    OpNegate {
        override fun exec(vm: VM) {
            when (val value = vm.pop()) {
                is LoxValue.LoxNumber -> {
                    vm.push(-value)
                    vm.increment()
                }
                else -> unreachable()
            }
        }
    },

    OpAdd {
        override fun exec(vm: VM) {
            binaryOp(vm) {
                val (a, b) = it
                when {
                    (a is LoxValue.LoxNumber && b is LoxValue.LoxNumber) -> {
                        a + b
                    }
                    (a is LoxValue.LoxString && b is LoxValue.LoxString) -> {
                        a + b
                    }
                    else -> unreachable()
                }
            }
            vm.increment()
        }
    },

    OpSubtract {
        override fun exec(vm: VM) {
            binaryOp(vm) {
                val (a, b) = it
                when {
                    (a is LoxValue.LoxNumber && b is LoxValue.LoxNumber) -> {
                        a - b
                    }
                    else -> unreachable()
                }
            }
            vm.increment()
        }
    },

    OpMultiply {
        override fun exec(vm: VM) {
            binaryOp(vm) {
                val (a, b) = it
                when {
                    (a is LoxValue.LoxNumber && b is LoxValue.LoxNumber) -> {
                        a * b
                    }
                    else -> unreachable()
                }
            }
            vm.increment()
        }
    },

    OpDivide {
        override fun exec(vm: VM) {
            binaryOp(vm) {
                val (a, b) = it
                when {
                    (a is LoxValue.LoxNumber && b is LoxValue.LoxNumber) -> {
                        a * b
                    }
                    else -> unreachable()
                }
            }
            vm.increment()
        }
    },

    ;

    internal abstract fun exec(vm: VM)

    /**
     * ip will plus 1
     */
    internal open fun decompile(vm: VM) {
        println("0X${vm.chunk.ip.toString(16).padStart(8, '0')} ${this.name} [${this.ordinal}]")
        vm.chunk.ip += 1
    }

    fun toByte(): Byte {
        return this.ordinal.toByte()
    }

    companion object {
        private val map = OpCode.values().associateBy { it.ordinal.toByte() }
        infix fun from(value: Byte) = map[value]
    }
}

fun printValue(value: LoxValue<Any>) {
    print(value)
}
