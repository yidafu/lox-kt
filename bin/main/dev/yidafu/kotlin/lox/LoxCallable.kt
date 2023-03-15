package dev.yidafu.kotlin.lox

interface LoxCallable {
    fun arity(): Int

    fun call(interperter: Interperter, args: List<AnyValue>): AnyValue
}

class LoxFunction(
    private val declaration: Func,
    private val closure: Environment,
    internal val isInitializer: Boolean,
) : LoxCallable, AnyValue() {

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
        } catch (rValue: LoxReturnInterruptException) {
            if (isInitializer) {
                return closure.getAt(0, "this")
            }
            return rValue.value
        }
        return if (isInitializer) closure.getAt(0, "this") else Nil()
    }

    fun bind(instance: LoxInstance): LoxFunction {
        val env = Environment(closure)
        env.define("this", instance)
        return LoxFunction(declaration, env, isInitializer)
    }

    override fun toString(): String {
        return "<fn ${declaration.name.lexeme}>"
    }
}
