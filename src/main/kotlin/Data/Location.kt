package Data

/**
 * Локация персональных данных
 * @property X Координата X (не null)
 * @property Y Координата Y
 * @property Z Координата Z (не null)
 * @property Name Название локации (≤392 символов, может быть null)
 */
data class Location(
    val X: Long,
    val Y: Float,
    val Z: Float,
    val Name: String?
) {
    init {
        require(Name == null || Name.length <= 392) {
            "Длина названия локации не должна превышать 392 символа"
        }
    }
}