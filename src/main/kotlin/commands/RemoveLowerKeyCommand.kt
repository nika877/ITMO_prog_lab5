package commands


import сore.CollectionManager
import util.ConsoleIO

class RemoveLowerKeyCommand(
    private val collectionManager: CollectionManager,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        if (args.size != 1) {
            ioManager.error("Требуется 1 аргумент - ключ")
            throw IllegalArgumentException()
        }
        val key = args[0]
        val count = collectionManager.removeLowerKey(key)
        ioManager.success("Удалено $count элементов с ключами меньше '$key'")
    }

    override fun getDescription() = "удалить элементы с ключами меньше заданного"
}