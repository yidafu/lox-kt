package dev.yidafu.kotlin.lox

interface LoxCallable {
    fun arity(): Int

    fun call(interperter: Interperter, args: List<AnyValue>): AnyValue
}

class LoxFunction(
    private val declaration: Func,
    private val closure: Environment,
) : LoxCallable {
    override fun arity(): Int {
        TODO("Not yet implemented")
    }

    override fun call(interperter: Interperter, args: List<AnyValue>): AnyValue {
        val env = Environment(closure)
        for (idx in 0 until declaration.params.size) {
            val name = declaration.params[idx]
            val value = args[idx]
            env.define(name.lexeme, value)
        }
        try {
            interperter.executeBlock(declaration.body, env)
        } catch (rValue: ReturnInterruptException) {
            return rValue.value
        }

        return Nil()
    }

    override fun toString(): String {
        return "<fn ${declaration.name.lexeme}>"
    }
}
