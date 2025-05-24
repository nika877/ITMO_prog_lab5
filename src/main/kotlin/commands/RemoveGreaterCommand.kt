package commands

import сore.CollectionManager
import util.ConsoleIO
import util.InteractiveReader

class RemoveGreaterCommand(
    private val collectionManager: CollectionManager,
    private val reader: InteractiveReader,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        if (args.isNotEmpty()) {
            ioManager.error("Команда не требует аргументов")
            throw IllegalArgumentException()
        }
        val worker = reader.readWorker()
        val count = collectionManager.removeGreater(worker)
        ioManager.success("Удалено $count элементов, больших чем заданный")
    }

    override fun getDescription() = "удалить элементы, большие чем заданный"
}