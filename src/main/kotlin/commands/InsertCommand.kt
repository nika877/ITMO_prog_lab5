package commands

import Data.Worker
import сore.CollectionManager
import util.ConsoleIO
import util.InteractiveReader
import java.lang.IllegalArgumentException

class InsertCommand(
    private val collectionManager: CollectionManager,
    private val reader: InteractiveReader,
    private val ioManager: ConsoleIO
) : Command {
    override fun execute(args: Array<String>) {
        try {
            val key = if (args.isNotEmpty()) {
                args[0]
            } else {
                ioManager.readWithRetry("Введите ключ для нового элемента: ")
            }

            validateKey(key)


            ioManager.info("Введите данные нового элемента:")
            val worker = readWorkerWithRetry()

            collectionManager.add(key, worker)
            ioManager.success("Элемент с ключом '$key' успешно добавлен")

        } catch (e: IllegalArgumentException) {
            ioManager.error(e.message ?: "Ошибка в аргументах команды")
            throw e
        } catch (e: Exception) {
            ioManager.error("Не удалось добавить элемент: ${e.message}")
            throw e
        }
    }

    private fun validateKey(key: String) {
        when {
            key.isBlank() -> throw IllegalArgumentException("Ключ не может быть пустым")
            collectionManager.containsKey(key) ->
                throw IllegalArgumentException("Элемент с ключом '$key' уже существует")
            !key.matches(Regex("[a-zA-Z0-9_]+")) ->
                throw IllegalArgumentException("Ключ может содержать только буквы, цифры и подчеркивание")
        }
    }

    private fun readWorkerWithRetry(maxAttempts: Int = 3): Worker {
        var attempts = 0
        var lastException: Exception? = null

        while (attempts < maxAttempts) {
            try {
                return reader.readWorker()
            } catch (e: Exception) {
                attempts++
                lastException = e
                ioManager.error("Ошибка ввода (попытка $attempts/$maxAttempts): ${e.message}")
                if (attempts < maxAttempts) {
                    ioManager.info("Пожалуйста, попробуйте еще раз:")
                }
            }
        }

        throw lastException ?: Exception("Неизвестная ошибка при вводе данных")
    }

    override fun getDescription() = "insert <key> - добавить новый элемент с заданным ключом"
}