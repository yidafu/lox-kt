package dev.yidafu.kotlin.lox

import dev.yidafu.kotlin.lox.parser.Scanner
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

var hadError = false

fun runFile(path: String) {
    val bytes = Files.readAllBytes(Paths.get(path))
    run(String(bytes, Charset.defaultCharset()))

    if (hadError) exitProcess(65)
}

fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
        print("> ")
        val line = reader.readLine()
        if (!line.isNullOrBlank()) {
            run(line)
            hadError = false
        }
    }
}

fun run(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()
    tokens.forEach {
        println(it)
    }
}

fun error(line: Int, msg: String): Nothing {
    report(line, "", msg)
}

fun report(line: Int, where: String, msg: String): Nothing {
    println("$line $where $msg")
    exitProcess(65)
}
fun main(args: Array<String>) {
    when (args.size) {
        0 -> runPrompt()
        1 -> runFile(args[0])
        else -> {
            println("Usage: kotlin lox [script]")
            exitProcess(64)
        }
    }
}
