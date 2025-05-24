package commands

import сore.CollectionManager
import util.ConsoleIO

class RemoveKeyCommand(
    private val collectionManager: CollectionManager,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        if (args.size != 1) {
            ioManager.error("Требуется 1 аргумент - ключ")
            throw IllegalArgumentException()
        }
        val key = args[0]
        if (collectionManager.remove(key)) {
            ioManager.success("Элемент с ключом '$key' успешно удален")
        } else {
            ioManager.error("Элемент с ключом '$key' не найден")
            throw IllegalArgumentException()
        }
    }

    override fun getDescription() = "удалить элемент по ключу"
}