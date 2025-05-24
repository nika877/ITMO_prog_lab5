package util

import Data.*

class Validator {
    fun validateWorker(worker: Worker) {
        validateId(worker.Id)
        validateName(worker.Name)
        validateCoordinates(worker.Coordinates)
        validateSalary(worker.Salary)
        validatePosition(worker.Position)
        worker.Status?.let { validateStatus(it) }
        worker.Person?.let { validatePerson(it) }
    }

    fun validateId(id: Long) {
        require(id > 0) { "ID должен быть положительным" }
    }

    fun validateName(name: String) {
        require(name.isNotBlank()) { "Имя не может быть пустым" }
    }

    fun validateCoordinates(coordinates: Coordinates) {
        require(coordinates.Y > -518) { "Координата Y должна быть > -518" }
    }

    fun validateSalary(salary: Double) {
        require(salary > 0) { "Зарплата должна быть положительной" }
    }

    fun validatePosition(position: Position) {
        require(position != Position.UNKNOWN) { "Укажите допустимую должность" }
    }

    fun validatePerson(person: Person) {
        validateWeight(person.Weight)
        person.Location?.let { validateLocation(it) }
    }

    private fun validateStatus(status: Status?) {
        if (status != null) {
            require(status in Status.values()) {
                "Недопустимый статус: $status. Доступные значения: ${Status.values().joinToString()}"
            }
        }
    }


    private fun validateWeight(weight: Float) {
        require(weight > 0) { "Вес должен быть положительным" }
    }

    private fun validateLocation(location: Location) {
        require(location.Name == null || location.Name.length <= 392) {
            "Название локации не должно превышать 392 символа"
        }
    }
}