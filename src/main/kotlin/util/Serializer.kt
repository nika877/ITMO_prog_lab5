package util

import Data.Worker
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.nio.file.*
import java.time.LocalDate
import java.util.*

class Serializer(private val ioManager: ConsoleIO) {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateSerializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        .setPrettyPrinting()
        .create()

    private val defaultPath = Paths.get("default_workers.json")

    fun serialize(collection: Map<String, Worker>, filePath: Path? = null) {
        val path = filePath ?: defaultPath
        try {
            Files.writeString(
                path,
                gson.toJson(collection),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            )
            ioManager.success("Коллекция успешно сохранена в файл: ${path.toAbsolutePath()}")
        } catch (e: AccessDeniedException) {
            ioManager.error("Ошибка доступа: нет прав для записи в файл ${path.toAbsolutePath()}")
            throw e
        } catch (e: Exception) {
            ioManager.error("Ошибка при сохранении файла: ${e.localizedMessage}")
            throw e
        }
    }

    fun deserialize(filePath: Path? = null): Map<String, Worker> {
        val path = filePath ?: defaultPath
        return try {
            if (!Files.exists(path)) {
                ioManager.info("Файл ${path.fileName} не найден. Будет создана новая коллекция.")
                return emptyMap()
            }

            val content = Files.readString(path)
            if (content.isBlank()) {
                ioManager.info("Файл ${path.fileName} пуст.")
                return emptyMap()
            }

            val type = object : TypeToken<Map<String, Worker>>() {}.type
            gson.fromJson<Map<String, Worker>>(content, type)?.also {
                ioManager.success("Успешно загружено ${it.size} элементов из файла ${path.fileName}")
            } ?: emptyMap()
        } catch (e: JsonSyntaxException) {
            ioManager.error("Ошибка формата JSON в файле ${path.fileName}")
            throw e
        } catch (e: Exception) {
            ioManager.error("Ошибка чтения файла ${path.fileName}: ${e.localizedMessage}")
            throw e
        }
    }

    private inner class DateSerializer : JsonSerializer<Date>, JsonDeserializer<Date> {
        override fun serialize(src: Date, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.time)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
            return Date(json.asLong)
        }
    }

    private inner class LocalDateSerializer : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        override fun serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.toString())
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate {
            return LocalDate.parse(json.asString)
        }
    }
}