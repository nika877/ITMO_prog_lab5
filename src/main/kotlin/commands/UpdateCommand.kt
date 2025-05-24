package commands

import сore.CollectionManager
import util.ConsoleIO
import util.InteractiveReader

class UpdateCommand(
    private val collectionManager: CollectionManager,
    private val reader: InteractiveReader,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        try {
            val key = if (args.isEmpty()) {
                ioManager.readWithRetry("Введите ключ элемента для обновления: ")
            } else {
                args[0]
            }

            if (!collectionManager.containsKey(key)) {
                ioManager.error("Элемент с ключом '$key' не найден")
                return
            }

            ioManager.info("Введите новые данные для элемента:")
            val worker = try {
                reader.readWorker()
            } catch (e: Exception) {
                ioManager.error("Ошибка при вводе данных: ${e.message}")
                return
            }

            collectionManager.update(key, worker)
            ioManager.success("Элемент с ключом '$key' успешно обновлен")

        } catch (e: Exception) {
            ioManager.error("Критическая ошибка: ${e.message}")
            throw e
        }
    }

    override fun getDescription() = "обновить значение элемента по ключу"
}