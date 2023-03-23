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
            vm.chunk.ip = vm.chunk.codes.size
            return
        }
    },

    OpConstant {
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

    OpSetGlobal,

    OpGetGlobal,
    ;

    /**
     * ip will plus 1
     */
    internal open fun decompile(vm: VM) {
        println("0X${vm.chunk.ip.toString(16).padStart(8, '0')} ${this.name} [${this.ordinal}]")
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
