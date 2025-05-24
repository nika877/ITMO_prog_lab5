package commands

import core.CommandManager
import util.ConsoleIO

class HelpCommand(
    private val commandManager: CommandManager,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        if (args.isNotEmpty()) {
            ioManager.error("Команда не требует аргументов")
            throw IllegalArgumentException()
        }

        ioManager.info("Доступные команды:")
        commandManager.getCommandList().forEach { (name, description) ->
            ioManager.writeLine("  $name - $description")
        }
    }

    override fun getDescription() = "вывести справку по доступным командам"
}