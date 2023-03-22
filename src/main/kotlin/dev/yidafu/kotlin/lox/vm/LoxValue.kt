package dev.yidafu.kotlin.lox.vm

sealed class LoxValue<out T>(val value: T) {
    class LoxString(value: String) : LoxValue<String>(value) {
        operator fun plus(that: LoxString): LoxString {
            return LoxString(this.value + that.value)
        }
    }

    class LoxNumber(value: Double) : LoxValue<Double>(value) {
        operator fun unaryMinus(): LoxNumber {
            return LoxNumber(-this.value)
        }
        operator fun plus(that: LoxNumber): LoxNumber {
            return LoxNumber(this.value + that.value)
        }
        operator fun minus(that: LoxNumber): LoxNumber {
            return LoxNumber(this.value - that.value)
        }
        operator fun times(that: LoxNumber): LoxNumber {
            return LoxNumber(this.value * that.value)
        }
        operator fun div(that: LoxNumber): LoxNumber {
            return LoxNumber(this.value + that.value)
        }

        override fun toString(): String {
            return "[LoxNumber] ${this.value}"
        }
    }

    class LoxNil(value: Any? = null) : LoxValue<Any?>(value)

    class LoxBool(value: Boolean) : LoxValue<Boolean>(value)

    override fun toString(): String {
        return "[LoxValue] ${this.value}"
    }
}
