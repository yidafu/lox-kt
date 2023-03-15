package dev.yidafu.kotlin.lox.vm // ktlint-disable filename

inline fun binaryOp(vm: VM, crossinline operator: (pair: Pair<Value, Value>) -> Value) {
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
            vm.push(-vm.pop())
            vm.increment()
        }
    },

    OpAdd {
        override fun exec(vm: VM) {
            binaryOp(vm) {
                it.first + it.second
            }
            vm.increment()
        }
    },

    OpSubtract {
        override fun exec(vm: VM) {
            binaryOp(vm) {
                it.first - it.second
            }
            vm.increment()
        }
    },

    OpMultiply {
        override fun exec(vm: VM) {
            binaryOp(vm) {
                it.first * it.second
            }
            vm.increment()
        }
    },

    OpDivide {
        override fun exec(vm: VM) {
            binaryOp(vm) {
                it.first / it.second
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

fun printValue(value: Value) {
    print(value)
}
