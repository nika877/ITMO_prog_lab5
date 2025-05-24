package commands

import сore.CollectionManager
import util.ConsoleIO

class AverageOfSalaryCommand(
    private val collectionManager: CollectionManager,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        if (args.isNotEmpty()) {
            ioManager.error("Команда не требует аргументов")
            throw IllegalArgumentException()
        }
        val average = collectionManager.averageSalary()
        ioManager.success("Средняя зарплата: $average")
    }

    override fun getDescription() = "вывести среднюю зарплату"
}