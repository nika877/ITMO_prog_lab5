package commands

import сore.CollectionManager
import util.ConsoleIO

class ClearCommand(
    private val collectionManager: CollectionManager,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        if (args.isNotEmpty()) {
            ioManager.error("Команда не требует аргументов")
            throw IllegalArgumentException()
        }
        collectionManager.clear()
        ioManager.success("Коллекция успешно очищена")
    }

    override fun getDescription() = "очистить коллекцию"
}