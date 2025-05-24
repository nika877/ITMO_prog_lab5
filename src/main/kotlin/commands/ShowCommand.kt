package commands

import сore.CollectionManager
import util.ConsoleIO

class ShowCommand(
    private val collectionManager: CollectionManager,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        try {
            if (args.isNotEmpty()) {
                ioManager.error("Ошибка: команда 'show' не принимает аргументы")
                ioManager.info("Правильное использование: show")
                return
            }

            if (collectionManager.getCollection().isEmpty()) {
                ioManager.info("Коллекция пуста")
                return
            }

            ioManager.success("Элементы коллекции (${collectionManager.getCollection().size} шт.):")
            ioManager.writeLine("─".repeat(50))  // Разделитель

            collectionManager.getCollection().entries
                .sortedBy { it.key }  // Сортировка по ключу
                .forEachIndexed { index, (key, worker) ->
                    ioManager.writeLine("${index + 1}. $key:")
                    ioManager.writeLine("   ${worker.toString().replace("\n", "\n   ")}")
                    ioManager.writeLine("─".repeat(50))  // Разделитель между элементами
                }

        } catch (e: Exception) {
            ioManager.error("Произошла ошибка при отображении коллекции: ${e.message}")
            throw e
        }
    }

    override fun getDescription() = "show - вывести все элементы коллекции с подробной информацией"
}