package dev.yidafu.kotlin.lox

class LoxInstance(private val klass: LoxClass) {
    private val fields = mutableMapOf<String, AnyValue>()

    override fun toString(): String {
        return "<object ${klass.name}>"
    }

    fun get(name: Token): AnyValue {
        if (fields.containsKey(name.lexeme)) {
            return fields[name.lexeme] ?: Nil()
        }

        return klass.findMethod(name.lexeme)?.bind(this) ?: throw LoxObjectNotHavePropertiesException()
    }

    fun set(name: Token, value: AnyValue) {
        fields[name.lexeme] = value
    }
}

class LoxClass(
    internal val name: String,
    private val superclass: LoxClass?,
    private val methods: Map<String, LoxFunction>,
) : LoxCallable {
    override fun arity(): Int {
        return methods["init"]?.arity() ?: 0
    }

    override fun call(interperter: Interperter, args: List<AnyValue>): AnyValue {
        val instance = LoxInstance(this)
        findMethod("init")?.bind(instance)?.call(interperter, args)

        return instance
    }

    override fun toString(): String {
        return "<class $name>"
    }

    internal fun findMethod(name: String): LoxFunction? {
        if (methods.containsKey(name)) {
            return methods[name]
        }

        return superclass?.findMethod(name)
    }
}
