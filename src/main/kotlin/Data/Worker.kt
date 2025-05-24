package Data

import java.util.*
import java.time.LocalDate

/**
 * Класс работника
 * @property Id Уникальный идентификатор (>0)
 * @property Name Имя работника (не пустое)
 * @property Coordinates Координаты (не null)
 * @property CreationDate Дата создания (автоматическая)
 * @property Salary Зарплата (>0)
 * @property StartDate Дата начала работы (не null)
 * @property Position Должность (не null)
 * @property Status Статус (может быть null)
 * @property Person Персональные данные (может быть null)
 */
data class Worker(
    val Id: Long,
    val Name: String,
    val Coordinates: Coordinates,
    val CreationDate: Date = Date(),
    val Salary: Double,
    val StartDate: LocalDate,
    val Position: Position,
    val Status: Status?,
    val Person: Person?
) {
    init {
        require(Id > 0) { "ID должен быть больше 0" }
        require(Name.isNotEmpty()) { "Имя не может быть пустым" }
        require(Salary > 0) { "Зарплата должна быть больше 0" }
    }
}
