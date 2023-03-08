package dev.yidafu.kotlin.lox

class Nil : AnyValue() {
    override fun toString(): String {
        return "Nil"
    }
}

class Environment(
    private var enclosing: Environment? = null,
) {
    private val values: MutableMap<String, AnyValue> = mutableMapOf()

    fun define(name: String, value: AnyValue?) {
        values[name] = value ?: Nil()
    }

    fun assign(name: Token, value: AnyValue) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
        }
        enclosing?.assign(name, value) ?: unreachable()
    }

    operator fun get(name: Token): AnyValue {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme] ?: Nil()
        }

        return enclosing?.get(name) ?: throw LoxUndefinedException(name.lexeme)
    }
}
