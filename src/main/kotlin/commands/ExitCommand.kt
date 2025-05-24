package commands

import util.ConsoleIO

class ExitCommand(
    private val ioManager: ConsoleIO,
    private val onExit: (() -> Unit)? = null
) : Command {
    override fun execute(args: Array<String>) {
        if (args.isNotEmpty()) {
            ioManager.error("Команда не требует аргументов")
            throw IllegalArgumentException()
        }

        ioManager.info("Завершение работы программы")

        onExit?.invoke() ?: System.exit(0)
    }

    override fun getDescription() = "завершить программу (без сохранения)"
}