package dev.yidafu.kotlin.lox

class Nil : AnyValue() {
    override fun toString(): String {
        return "Nil"
    }
}

class Environment(
    internal var enclosing: Environment? = null,
) {
    private val values: MutableMap<String, AnyValue> = mutableMapOf()

    fun define(name: String, value: AnyValue?) {
        values[name] = value ?: Nil()
    }

    fun assign(name: Token, value: AnyValue) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
        } else {
            enclosing?.assign(name, value) ?: unreachable()
        }
    }

    fun assignAt(distance: Int, name: Token, value: AnyValue) {
        ancestor(distance)?.values?.set(name.lexeme, value)
    }

    operator fun get(name: Token): AnyValue {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme] ?: Nil()
        }

        return enclosing?.get(name) ?: throw LoxUndefinedException(name.lexeme)
    }

    internal fun getAt(distance: Int, name: String): AnyValue {
        return ancestor(distance)?.values?.get(name) ?: throw LoxUndefinedException(name)
    }

    private fun ancestor(distance: Int): Environment? {
        var env: Environment? = this
        for (i in 0 until distance) {
            env = env?.enclosing
        }
        return env
    }
}
