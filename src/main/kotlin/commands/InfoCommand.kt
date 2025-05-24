package commands

import сore.CollectionManager
import util.ConsoleIO
import java.time.format.DateTimeFormatter

class InfoCommand(
    private val collectionManager: CollectionManager,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        if (args.isNotEmpty()) {
            ioManager.error("Команда не требует аргументов")
            throw IllegalArgumentException()
        }

        ioManager.info("""
            Тип коллекции: ${collectionManager.getCollectionType()}
            Количество элементов: ${collectionManager.getSize()}
            Дата инициализации: ${collectionManager.getInitDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}
        """.trimIndent())
    }

    override fun getDescription() = "вывести информацию о коллекции"
}