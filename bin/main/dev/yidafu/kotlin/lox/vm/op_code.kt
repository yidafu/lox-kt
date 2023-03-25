package dev.yidafu.kotlin.lox.vm

inline fun binaryOp(vm: VM, crossinline operator: (pair: Pair<LoxValue<Any>, LoxValue<Any>>) -> LoxValue<Any>) {
    val right = vm.pop()
    val left = vm.pop()
    vm.push(operator(Pair(left, right)))
}

enum class OpCode {
    OpReturn {
        override fun decompile(vm: VM) {
            super.decompile(vm)
            // move ip to end of chunk
            vm.frame.ip = vm.frame.codes.size
            return
        }
    },

    OpConstant {
        override fun decompile(vm: VM) {
            val chunk = vm.frame.chunk
            val cIdx = chunk.codes[chunk.ip + 1].toInt()

            super.decompile(vm)
            print("\t $cIdx ==> ")
            printValue(chunk.constants[cIdx])
            println()

            vm.increment()
        }
    },

    OpNegate,

    OpAdd,

    OpSubtract,

    OpMultiply,

    OpDivide,

    OpFalse,

    OpTrue,

    OpNil,

    OpNot,

    OpEqual,

    OpGreater,
    OpLess,

    OpPrint,
    OpPop,

    OpDefineGlobal,

    OpSetGlobal {
        override fun decompile(vm: VM) {
            super.decompile(vm)
            print("\tset global [${vm.frame.readByte()}]")
            vm.increment()
            println()
        }
    },

    OpGetGlobal {
        override fun decompile(vm: VM) {
            super.decompile(vm)
            print("\tget global [${vm.frame.readByte()}]")
            vm.increment(2)
            println()
        }
    },

    OpGetLocal {
        override fun decompile(vm: VM) {
            super.decompile(vm)
            print("\tget local stack[${vm.frame.readByte()}]")
            vm.increment()

            println()
        }
    },

    OpSetLocal {
        override fun decompile(vm: VM) {
            super.decompile(vm)
            print("\tset local stack[${vm.frame.readByte()}]")
            vm.increment()

            println()
        }
    },

    OpJumpIfFalse {
        override fun decompile(vm: VM) {
            super.decompile(vm)
            val offset = vm.frame.readShort()
            println("\t offset $offset")
        }
    },
    OpJump {
        override fun decompile(vm: VM) {
            super.decompile(vm)
            val offset = vm.frame.readShort()
            println("\t offset $offset")
        }
    },

    OpLoop {
        override fun decompile(vm: VM) {
            super.decompile(vm)
            val offset = vm.frame.readShort()
            println("\t loop start at -$offset")
        }
    },

    OpCall
    ;

    /**
     * ip will plus 1
     */
    internal open fun decompile(vm: VM) {
        println("0X${vm.frame.ip.toString(16).padStart(8, '0')} ${this.name}")
        vm.increment()
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
