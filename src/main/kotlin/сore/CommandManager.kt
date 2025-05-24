package core

import commands.*
import сore.CollectionManager
import util.*
import java.nio.file.Path
import java.util.*

class CommandManager(
    private val collectionManager: CollectionManager,
    private val inputManager: InputManager,
    private val ioManager: ConsoleIO,
    private val filePath: Path
) {
    private val commands = mutableMapOf<String, Command>()
    private val commandHistory = LinkedList<String>()
    private val reader = InteractiveReader(inputManager, ioManager)
    private val serializer = Serializer(ioManager)
    private var isRunning = true

    init {
        registerCommands()
    }

    private fun registerCommands() {
        with(commands) {
            put("help", HelpCommand(this@CommandManager, ioManager))
            put("info", InfoCommand(collectionManager, ioManager))
            put("show", ShowCommand(collectionManager, ioManager))

            put("insert", InsertCommand(collectionManager, reader, ioManager))
            put("update", UpdateCommand(collectionManager, reader, ioManager))
            put("remove_key", RemoveKeyCommand(collectionManager, ioManager))
            put("clear", ClearCommand(collectionManager, ioManager))

            put("save", SaveCommand(collectionManager, serializer, ioManager, filePath))
            put("execute_script", ExecuteScriptCommand(this@CommandManager, ioManager, inputManager))

            put("remove_greater", RemoveGreaterCommand(collectionManager, reader, ioManager))
            put("remove_greater_key", RemoveGreaterKeyCommand(collectionManager, ioManager))
            put("remove_lower_key", RemoveLowerKeyCommand(collectionManager, ioManager))
            put("remove_all_by_status", RemoveAllByStatusCommand(collectionManager, ioManager))
            put("average_of_salary", AverageOfSalaryCommand(collectionManager, ioManager))
            put("print_field_descending_position", PrintFieldDescendingPositionCommand(collectionManager, ioManager))

            put("exit", ExitCommand(ioManager) { isRunning = false })
        }
    }

    fun execute(commandLine: String) {
        if (!isRunning) return

        try {
            val (commandName, args) = parseCommand(commandLine)
            val command = getCommand(commandName)

            if (commandName !in setOf("history", "help")) {
                commandHistory.add(commandLine)
                if (commandHistory.size > 15) commandHistory.removeFirst()
            }

            command.execute(args)
        } catch (e: IllegalArgumentException) {
            ioManager.error("Ошибка: ${e.message}")
            ioManager.info("Введите 'help' для списка команд")
        } catch (e: NoSuchElementException) {
            ioManager.error("Элемент не найден: ${e.message}")
        } catch (e: IllegalStateException) {
            ioManager.error("Недопустимая операция: ${e.message}")
        } catch (e: Exception) {
            ioManager.error("Критическая ошибка: ${e.javaClass.simpleName} - ${e.message}")
            throw e
        }
    }

    private fun parseCommand(commandLine: String): Pair<String, Array<String>> {
        val trimmed = commandLine.trim()
        if (trimmed.isEmpty()) throw IllegalArgumentException("Пустая команда")

        val parts = trimmed.split("\\s+".toRegex())
        return Pair(parts[0], parts.drop(1).toTypedArray())
    }

    private fun getCommand(name: String): Command {
        return commands[name.toLowerCase()]
            ?: throw IllegalArgumentException("Неизвестная команда '$name'. Введите 'help' для списка команд")
    }

    fun getCommandList(): Map<String, String> = commands.mapValues { it.value.getDescription() }

    fun isRunning(): Boolean = isRunning

    companion object {
        val RESERVED_COMMANDS = setOf(
            "help", "info", "show", "insert", "update",
            "remove_key", "clear", "save", "execute_script",
            "exit", "history"
        )
    }
}