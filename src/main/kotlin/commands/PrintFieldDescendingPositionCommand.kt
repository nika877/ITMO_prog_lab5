package commands

import сore.CollectionManager
import util.ConsoleIO

class PrintFieldDescendingPositionCommand(
    private val collectionManager: CollectionManager,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        if (args.isNotEmpty()) {
            ioManager.error("Команда не требует аргументов")
            throw IllegalArgumentException()
        }
        val positions = collectionManager.getPositionsDescending()
        ioManager.info("Должности в порядке убывания:")
        positions.forEach { ioManager.writeLine(it.toString()) }
    }

    override fun getDescription() = "вывести должности в порядке убывания"
}