package сore

import Data.*
import util.Validator
import java.time.LocalDateTime
import java.util.*

class CollectionManager {
    private val collection = Hashtable<String, Worker>()
    private val initDate = LocalDateTime.now()
    private val workerComparator = compareBy<Worker> { it.Salary }
        .thenBy { it.Name }
        .thenBy { it.Position }

    fun removeGreater(worker: Worker): Int {
        val initialSize = collection.size
        collection.values.removeIf {
            workerComparator.compare(it, worker) > 0
        }
        return initialSize - collection.size
    }
    fun getCollection(): Hashtable<String, Worker> = collection
    fun getSize(): Int = collection.size
    fun getInitDate(): LocalDateTime = initDate
    fun getCollectionType(): String = collection.javaClass.name

    fun add(key: String, worker: Worker) {
        require(!containsKey(key)) { "Ключ '$key' уже существует" }
        collection[key] = worker
    }

    fun update(key: String, worker: Worker) {
        require(containsKey(key)) { "Ключ '$key' не найден" }
        collection[key] = worker
    }

    fun remove(key: String): Boolean = collection.remove(key) != null
    fun clear() = collection.clear()


    fun findByKey(key: String): Worker? = collection[key]
    fun containsKey(key: String): Boolean = collection.containsKey(key)

    fun filterByStatus(status: Status?): Map<String, Worker> =
        collection.filterByStatus(status)

    fun findMaxSalaryWorker(): Worker? =
        collection.values
            .filter { worker -> worker.Person?.Weight != null && worker.Person.Weight > 0 } // Исключаем null или вес <= 0
            .maxByOrNull { it.Person!!.Weight }  // Находим максимальный вес


    fun findMinSalaryWorker(): Worker? =
        collection.values.minByOrNull { it.Salary }

    fun averageSalary(): Double =
        collection.values.map { it.Salary }.average().takeIf { !isEmpty() } ?: 0.0

    fun getPositionsDescending(): List<Position> =
        collection.values
            .map { it.Position }
            .sortedByDescending { it.ordinal }
            .distinct()


    fun validateAll() {
        collection.forEach { (key, worker) ->
            try {
                Validator().validateWorker(worker)
            } catch (e: IllegalArgumentException) {
                throw IllegalStateException("Невалидные данные для ключа '$key': ${e.message}")
            }
        }
    }

    fun loadData(data: Map<String, Worker>) {
        clear()
        collection.putAll(data)
        validateAll()
    }

    fun removeAllByStatus(status: Status): Int {
        val initialSize = collection.size
        collection.values.removeIf { worker -> worker.Status == status }
        return initialSize - collection.size
    }

    fun removeLowerKey(key: String): Int {
        val numericKey = key.toIntOrNull()
            ?: throw IllegalArgumentException("Ключ должен быть числом")

        val keysToRemove = collection.keys.filter {
            it.toIntOrNull()?.let { it < numericKey } ?: false
        }

        for (k in keysToRemove) {
            collection.remove(k)
        }

        return keysToRemove.size
    }



    fun removeGreaterKey(key: String): Int {
        val initialSize = collection.size
        collection.keys.removeIf { it > key }
        return initialSize - collection.size
    }

    fun isEmpty(): Boolean = collection.isEmpty()
}