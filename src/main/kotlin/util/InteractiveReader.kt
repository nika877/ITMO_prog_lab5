package util

import Data.*
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.*

class InteractiveReader(
    val inputManager: InputManager,
    val ioManager: ConsoleIO
) {
    fun readWorker(): Worker {
        ioManager.info("\nСоздание нового Worker:")
        return Worker(
            Id = generateId(),
            Name = readString("Имя работника", allowEmpty = false),
            Coordinates = readCoordinates(),
            CreationDate = Date(),
            Salary = readDouble("Зарплата", minValue = 0.01),
            StartDate = readDate("Дата начала работы (ГГГГ-ММ-ДД)"),
            Position = readEnum("Должность", Position.values())!!,
            Status = readEnum("Статус", Status.values(), nullable = true),
            Person = readPerson()
        )
    }

    private fun readCoordinates(): Coordinates {
        ioManager.info("\nВвод координат:")
        return Coordinates(
            X = readFloat("X", nullable = false),
            Y = readFloat("Y", minValue = -517.99f, nullable = false)
        )
    }

    private fun readPerson(): Person? {
        if (!readBoolean("Добавить персональные данные? (yes/no)")) return null

        ioManager.info("\nВвод персональных данных:")
        return Person(
            Weight = readFloat("Вес", minValue = 0.01f),
            EyeColor = readEnum("Цвет глаз", Color.values(), nullable = true),
            HairColor = readEnum("Цвет волос", Color.values(), nullable = true),
            Location = readLocation()
        )
    }

    private fun readLocation(): Location? {
        if (!readBoolean("Добавить локацию? (yes/no)")) return null

        ioManager.info("\nВвод локации:")
        return Location(
            X = readLong("Координата X", nullable = false),
            Y = readFloat("Координата Y"),
            Z = readFloat("Координата Z", nullable = false),
            Name = readString("Название локации", maxLength = 392)
        )
    }

    fun readString(prompt: String, allowEmpty: Boolean = true, maxLength: Int? = null): String {
        if (inputManager.isScriptMode()) {
            val input = inputManager.readLine() ?: throw IllegalArgumentException("Неожиданный конец скрипта")

            when {
                !allowEmpty && input.isEmpty() -> throw IllegalArgumentException("$prompt не может быть пустым")
                maxLength != null && input.length > maxLength -> throw IllegalArgumentException("Длина не должна превышать $maxLength символов")
                else -> return input
            }
        }

        while (true) {
            ioManager.write("$prompt: ")
            val input = inputManager.readLine() ?: continue

            when {
                !allowEmpty && input.isEmpty() -> ioManager.error("Значение не может быть пустым")
                maxLength != null && input.length > maxLength -> ioManager.error("Длина не должна превышать $maxLength символов")
                else -> return input
            }
        }
    }



    fun readFloat(prompt: String, minValue: Float? = null, nullable: Boolean = false): Float {
        while (true) {
            try {
                val input = readString(prompt)
                if (nullable && input.isEmpty()) return 0f

                val value = input.toFloat()
                minValue?.let {
                    if (value < it) throw IllegalArgumentException("Значение должно быть ≥ $minValue")
                }
                return value
            } catch (e: NumberFormatException) {
                if (inputManager.isScriptMode()) throw e
                ioManager.error("Введите число")
            } catch (e: IllegalArgumentException) {
                if (inputManager.isScriptMode()) throw e
                ioManager.error(e.message ?: "Некорректное значение")
            }
        }
    }

    fun readDouble(prompt: String, minValue: Double? = null): Double {
        while (true) {
            try {
                val value = readString(prompt).toDouble()
                minValue?.let {
                    if (value < it) throw IllegalArgumentException("Значение должно быть ≥ $minValue")
                }
                return value
            } catch (e: NumberFormatException) {
                if (inputManager.isScriptMode()) throw e
                ioManager.error("Введите число")
            } catch (e: IllegalArgumentException) {
                if (inputManager.isScriptMode()) throw e
                ioManager.error(e.message ?: "Некорректное значение")
            }
        }
    }

    fun readLong(prompt: String, nullable: Boolean = false): Long {
        while (true) {
            try {
                val input = readString(prompt)
                if (nullable && input.isEmpty()) return 0L
                return input.toLong()
            } catch (e: NumberFormatException) {
                if (inputManager.isScriptMode()) throw e
                ioManager.error("Введите целое число")
            }
        }
    }

    fun readBoolean(prompt: String): Boolean {
        while (true) {
            val input = readString("$prompt (yes/no)").lowercase()
            when (input) {
                "yes", "y", "да", "д" -> return true
                "no", "n", "нет", "н" -> return false
                else -> {
                    if (inputManager.isScriptMode()) throw IllegalArgumentException("Введите 'yes' или 'no'")
                    ioManager.error("Введите 'yes' или 'no'")
                }
            }
        }
    }

    fun readDate(prompt: String): LocalDate {
        while (true) {
            try {
                return LocalDate.parse(readString(prompt))
            } catch (e: DateTimeParseException) {
                if (inputManager.isScriptMode()) throw e
                ioManager.error("Введите дату в формате ГГГГ-ММ-ДД")
            }
        }
    }

    inline fun <reified T : Enum<T>> readEnum(prompt: String, values: Array<T>, nullable: Boolean = false): T? {
        while (true) {
            printEnumValues(values)
            val input = readString(prompt)

            if (nullable && input.isEmpty()) return null

            return try {
                enumValueOf<T>(input.uppercase())
            } catch (e: IllegalArgumentException) {
                if (inputManager.isScriptMode()) throw e
                ioManager.error("Недопустимое значение. Выберите из списка:")
                null
            } ?: continue
        }
    }

    fun <T : Enum<T>> printEnumValues(values: Array<T>) {
        ioManager.info("Допустимые значения:")
        values.forEach { ioManager.writeLine("- ${it.name}") }
    }

    private fun generateId(): Long = System.currentTimeMillis()
}