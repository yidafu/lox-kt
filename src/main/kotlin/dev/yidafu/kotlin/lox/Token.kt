package dev.yidafu.kotlin.lox

class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any = Any(),
    val line: Int = -1,
) {
    override fun toString(): String {
        return "$type $lexeme $literal"
    }
}

enum class TokenType(val value: String) {
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    COMMA(","),
    DOT("."),
    MINUS("-"),
    PLUS("+"),
    SEMICOLON(";"),
    SLASH("/"),
    STAR("*"),

    BANG("!"),
    BANG_EQUAL("!="),
    EQUAL("="),
    EQUAL_EQUAL("=="),

//    MINUS_EQUAL("-="),
//    PLUS_EQUAL("+="),
    GREATER(">"),
    GREATER_EQUAL(">="),
    LESS("<"),
    LESS_EQUAL("<="),

    IDENTIFIER("identifier"),
    STRING("string"),
    NUMBER("number"),

    AND("and"),
    CLASS("class"),
    ELSE("else"),
    FALSE("false"),
    FUN("fun"),
    FOR("for"),
    IF("if"),
    NIL("nil"),
    OR("or"),
    PRINT("print"),
    RETURN("return"),
    SUPER("super"),
    THIS("this"),
    TRUE("true"),
    VAR("var"),
    WHILE("while"),

    EDF("eof"),
    ;

    companion object {
        private val Separator = listOf<TokenType>(
            LEFT_PAREN,
            RIGHT_PAREN,
            LEFT_BRACE,
            RIGHT_BRACE,
            COMMA,
            DOT,
            MINUS,
            PLUS,
            SEMICOLON,
            SLASH,
            STAR,
            SLASH,
        )

        private val Keyword = listOf<TokenType>(
            AND,
            CLASS,
            ELSE,
            FALSE,
            FUN,
            FOR,
            IF,
            NIL,
            OR,
            PRINT,
            RETURN,
            SUPER,
            THIS,
            TRUE,
            VAR,
            WHILE,
        )

        private val KeywordMap = Keyword.associateBy { it.value }

        private val SeparatorList = Separator.map { it.value[0] }
        private val SeparatorMap = Separator.associateBy { it.value[0] }

        fun isSeparetor(c: Char): Boolean {
            return SeparatorList.contains(c)
        }

        fun toType(c: Char): TokenType {
            return SeparatorMap[c] ?: throw Exception("Expect a separator, but got a $c")
        }

        fun toType(identifer: String): TokenType? {
            return KeywordMap[identifer]
        }
    }

    override fun toString(): String {
        return this.name
    }
}
