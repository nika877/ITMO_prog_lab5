package commands

import сore.CollectionManager
import Data.Status
import util.ConsoleIO

class RemoveAllByStatusCommand(
    private val collectionManager: CollectionManager,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        if (args.size != 1) {
            ioManager.error("Требуется 1 аргумент - статус")
            throw IllegalArgumentException()
        }

        try {
            val status = Status.valueOf(args[0].uppercase())
            val removedCount = collectionManager.removeAllByStatus(status)
            ioManager.success("Удалено элементов: $removedCount")
        } catch (e: IllegalArgumentException) {
            ioManager.error("Недопустимый статус. Допустимые значения: ${Status.values().joinToString()}")
            throw e
        }
    }

    override fun getDescription() = "удалить все элементы с указанным статусом"
}