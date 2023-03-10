package dev.yidafu.kotlin.lox

class LoxInstance(private val klass: LoxClass) {
    val fields = mutableMapOf<String, AnyValue>()

    override fun toString(): String {
        return "<object ${klass.name}>"
    }

    fun get(name: Token): AnyValue {
        if (fields.containsKey(name.lexeme)) {
            return fields[name.lexeme] ?: Nil()
        }

        return klass.methods[name.lexeme]?.bind(this) ?: throw LoxObjectNotHavePropertiesException()
    }

    fun set(name: Token, value: AnyValue) {
        fields[name.lexeme] = value
    }
}

class LoxClass(
    internal val name: String,
    internal val methods: Map<String, LoxFunction>,
) : LoxCallable {
    override fun arity(): Int {
        TODO("Not yet implemented")
    }

    override fun call(interperter: Interperter, args: List<AnyValue>): AnyValue {
        return LoxInstance(this)
    }

    override fun toString(): String {
        return "<class $name>"
    }
}
