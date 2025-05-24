package main

import core.CommandManager
import util.*
import сore.CollectionManager
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Application {
    private lateinit var collectionManager: CollectionManager
    private lateinit var commandManager: CommandManager
    private lateinit var ioManager: ConsoleIO
    private lateinit var filePath: Path
    private lateinit var inputManager: StackInputManager // Изменено на конкретный тип

    fun run(args: Array<String>) {
        initialize(args)
        loadCollection()
        startCommandLoop()
    }

    private fun initialize(args: Array<String>) {
        // 1. Сначала создаём StackInputManager (без ioManager)
        inputManager = StackInputManager()

        // 2. Затем создаём ConsoleIO
        ioManager = ConsoleIO(inputManager)

        // 3. Устанавливаем обратную ссылку
        inputManager.outputManager = ioManager

        // 4. Инициализируем остальное
        filePath = Paths.get(args.firstOrNull() ?: "default_workers.json").toAbsolutePath()
        collectionManager = CollectionManager()
        commandManager = CommandManager(collectionManager, inputManager, ioManager, filePath)
    }

    private fun loadCollection() {
        try {
            if (Files.exists(filePath)) {
                val serializer = Serializer(ioManager)
                val data = serializer.deserialize(filePath)
                collectionManager.loadData(data)
                ioManager.success("Данные загружены. Элементов: ${collectionManager.getSize()}")
            } else {
                ioManager.info("Файл не найден. Будет создана новая коллекция.")
            }
        } catch (e: Exception) {
            ioManager.error("Ошибка при загрузке: ${e.message}")
        }
    }

    private fun startCommandLoop() {
        ioManager.info("Введите команду (help для справки):")

        while (true) {
            try {
                if (!inputManager.isScriptMode()) {
                    ioManager.write("> ")
                }

                val userInput = ioManager.readLine()
                if (userInput == null) {
                    if (inputManager.isScriptMode()) {
                        continue
                    } else {
                        ioManager.info("\nЗавершение работы...")
                        return
                    }
                }

                commandManager.execute(userInput)


                if (!inputManager.isScriptMode() &&
                    userInput.trim().equals("exit", ignoreCase = true)) {
                    break
                }

            } catch (e: Exception) {
                ioManager.error("Ошибка: ${e.message}")

                if (!inputManager.isScriptMode()) {
                    break
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    Application().run(args)
}
