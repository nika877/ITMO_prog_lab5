package commands

import сore.CollectionManager
import util.ConsoleIO
import util.Serializer
import java.nio.file.Path

class SaveCommand(
    private val collectionManager: CollectionManager,
    private val serializer: Serializer,
    private val ioManager: ConsoleIO,
    private val filePath: Path
) : Command {
    override fun execute(args: Array<String>) {
        if (args.isNotEmpty()) {
            ioManager.error("Команда не требует аргументов")
            throw IllegalArgumentException()
        }

        try {
            ioManager.info("В коллекции ${collectionManager.getCollection().size} элементов")
            serializer.serialize(collectionManager.getCollection(), filePath)
            ioManager.success("Коллекция сохранена успешно")
        } catch (e: Exception) {
            ioManager.error("Ошибка сохранения: ${e.message}")
            throw e
        }
    }

    override fun getDescription() = "сохранить коллекцию в файл"
}