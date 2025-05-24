package commands

import core.CommandManager
import util.ConsoleIO
import util.InputManager
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.NoSuchFileException

class ExecuteScriptCommand(
    private val commandManager: CommandManager,
    private val ioManager: ConsoleIO,
    private val inputManager: InputManager
) : Command {
    companion object {
        private const val MAX_RECURSION_DEPTH = 5
        private val SKIPPABLE_ERRORS = setOf(
            "рекурс",
            "неизвестная команда",
            "неверные аргументы"
        )
    }

    private val executedScripts = LinkedHashSet<String>()
    private var currentLineNumber = 0
    private var successCount = 0
    private var skipCount = 0
    private var errorCount = 0

    override fun execute(args: Array<String>) {
        validateArguments(args)
        val scriptPath = Paths.get(args[0]).toAbsolutePath().normalize()
        checkFileExists(scriptPath)

        try {
            registerScript(scriptPath)
            inputManager.pushSource(scriptPath)
            ioManager.info("Начало выполнения скрипта: ${scriptPath.fileName}")

            processScriptLines()

        } finally {
            cleanup(scriptPath)
        }
    }

    private fun processScriptLines() {
        while (inputManager.hasInput()) {
            val line = inputManager.readLine()?.trim() ?: break
            currentLineNumber++

            if (shouldSkipLine(line)) continue

            try {
                processCommand(line)
                successCount++
            } catch (e: Exception) {
                handleCommandError(line, e)
            }
        }
    }

    private fun processCommand(line: String) {
        ioManager.info("[Строка $currentLineNumber] > $line")
        commandManager.execute(line)
    }

    private fun handleCommandError(line: String, e: Exception) {
        when {
            isRecursionError(e) -> {
                ioManager.warning("Пропуск команды (рекурсия): $line")
                skipCount++
            }
            isSkippableError(e) -> {
                ioManager.error("Ошибка в строке $currentLineNumber: ${e.message}")
                errorCount++
            }
            else -> throw e
        }
    }


    private fun registerScript(scriptPath: Path) {
        if (executedScripts.size >= MAX_RECURSION_DEPTH) {
            throw IllegalStateException("Превышена максимальная глубина рекурсии ($MAX_RECURSION_DEPTH)")
        }

        val pathStr = scriptPath.toString()
        if (executedScripts.contains(pathStr)) {
            throw IllegalStateException("Рекурсивный вызов скрипта: ${scriptPath.fileName}")
        }

        executedScripts.add(pathStr)
    }

    private fun checkFileExists(scriptPath: Path) {
        when {
            !Files.exists(scriptPath) -> throw NoSuchFileException("Файл скрипта не найден: $scriptPath")
            !Files.isRegularFile(scriptPath) -> throw IllegalArgumentException("Указанный путь не является файлом: $scriptPath")
        }
    }

    private fun cleanup(scriptPath: Path) {
        try {
            ioManager.popSource()
            executedScripts.remove(scriptPath.toString())

            if (executedScripts.isEmpty()) {
                ioManager.exitScriptMode()
                resetCounters()
            }
        } catch (e: Exception) {
            ioManager.error("Ошибка при завершении скрипта: ${e.message}")
        }
    }

    private fun resetCounters() {
        currentLineNumber = 0
        successCount = 0
        skipCount = 0
        errorCount = 0
    }

    private fun shouldSkipLine(line: String): Boolean {
        return line.isEmpty() || line.startsWith("#") || line.startsWith("//")
    }

    private fun isRecursionError(e: Exception): Boolean {
        return e is IllegalStateException && e.message?.contains("рекурс", ignoreCase = true) == true
    }

    private fun isSkippableError(e: Exception): Boolean {
        return e is IllegalArgumentException ||
                e is NoSuchElementException ||
                SKIPPABLE_ERRORS.any { e.message?.contains(it, ignoreCase = true) == true }
    }

    private fun validateArguments(args: Array<String>) {
        when {
            args.isEmpty() -> throw IllegalArgumentException("Не указан путь к скрипту")
            args.size > 1 -> throw IllegalArgumentException("Слишком много аргументов")
        }
    }

    override fun getDescription() = """
        execute_script <file_path> - выполнить скрипт из файла
        • Поддерживает вложенные скрипты (макс. глубина: $MAX_RECURSION_DEPTH)
        • Пропускает команды с ошибками и продолжает выполнение
        • Статистика по завершении: успешные/пропущенные/ошибочные команды
    """.trimIndent()
}