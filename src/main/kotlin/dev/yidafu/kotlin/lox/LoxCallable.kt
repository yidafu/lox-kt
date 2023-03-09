package dev.yidafu.kotlin.lox

interface LoxCallable {
    fun arity(): Int

    fun call(interperter: Interperter, args: List<AnyValue>): AnyValue
}

class LoxFunction(private val declaration: Func) : LoxCallable {
    override fun arity(): Int {
        TODO("Not yet implemented")
    }

    override fun call(interperter: Interperter, args: List<AnyValue>): AnyValue {
        val env = Environment(interperter.global)
        for (idx in 0 until declaration.params.size) {
            val name = declaration.params[idx]
            val value = args[idx]
            env.define(name.lexeme, value)
        }
        interperter.executeBlock(declaration.body, env)
        return Nil()
    }

    override fun toString(): String {
        return "<fn ${declaration.name.lexeme}>"
    }
}
